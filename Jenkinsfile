// ══════════════════════════════════════════════════════════════════════════════
// ESTRATEGIA 2 — Pipeline modular y reutilizable
//
// Las tres funciones de abajo encapsulan lógica que cualquier microservicio
// Maven puede invocar sin repetir código. En proyectos multi-módulo se
// extraerían a una Jenkins Shared Library (vars/mavenUtils.groovy) y se
// importarían con: @Library('shared-lib') _
// ══════════════════════════════════════════════════════════════════════════════

/** Ejecuta cualquier goal de Maven con flags estándar de CI. */
def runMaven(String goal) {
    echo "Ejecutando Maven: ${goal}"
    sh "mvn ${goal} -B --no-transfer-progress"
}

/** Ejecuta una herramienta de calidad e imprime su nombre para trazabilidad. */
def runQualityTool(String tool, String goal) {
    echo "Analizando calidad con ${tool}..."
    sh "mvn ${goal} -B --no-transfer-progress"
    echo "${tool} completado."
}

/** Publica los reportes de pruebas en la interfaz de Jenkins. */
def publishTestReports() {
    junit testResults: 'target/surefire-reports/*.xml',
          allowEmptyResults: true
}

// ══════════════════════════════════════════════════════════════════════════════

pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk   'JDK17'
    }

    environment {
        PROYECTO    = 'ProyectoFinal'
        MAVEN_OPTS  = '-Xmx512m'
        // Resolución robusta del nombre de rama en cualquier modo de ejecución
        RAMA_ACTUAL = "${env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'main'}"
    }

    stages {

        // ──────────────────────────────────────────────────────────────────────
        // CHECKOUT — se ejecuta en TODAS las ramas
        // ──────────────────────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "════════════════════════════════"
                echo " Proyecto : ${PROYECTO}"
                echo " Rama     : ${RAMA_ACTUAL}"
                echo "════════════════════════════════"
                sh 'ls -la'
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 1: Compilación
        // Se ejecuta en TODAS las ramas para dar retroalimentación rápida al
        // desarrollador ante cualquier error de compilación.
        // ══════════════════════════════════════════════════════════════════════
        stage('Compilacion') {
            steps {
                script {
                    runMaven('clean compile')   // reutiliza Estrategia 2
                }
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 2a: Pruebas Unitarias
        // Se ejecuta en TODAS las ramas para retroalimentación rápida.
        // ── NOTA: las pruebas unitarias y de integración son SECUENCIALES   ──
        // ── porque Maven Surefire escribe archivos temporales en             ──
        // ── target/surefire/ y dos procesos simultáneos en el mismo          ──
        // ── workspace colisionan sobre esos archivos.                        ──
        // ══════════════════════════════════════════════════════════════════════
        stage('Pruebas Unitarias') {
            steps {
                script {
                    runMaven('test')   // reutiliza Estrategia 2
                }
                script {
                    publishTestReports()   // reutiliza Estrategia 2
                }
                echo 'Pruebas unitarias completadas.'
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 2b: Pruebas de Integración
        // ESTRATEGIA 3 — IC por rama: pipeline más completo al fusionar en main
        //
        // -Dsurefire.skip=true evita que Surefire vuelva a ejecutar las
        // pruebas unitarias (ya corrieron en la etapa anterior); solo se
        // completa la fase verify para generar el reporte de cobertura JaCoCo
        // y ejecutar los plugins ligados a esa fase.
        // ══════════════════════════════════════════════════════════════════════
        stage('Pruebas de Integracion') {
            when {
                expression {
                    return RAMA_ACTUAL == 'main'       ||
                           RAMA_ACTUAL == 'origin/main'
                }
            }
            steps {
                echo 'Rama main: completando fase verify (cobertura JaCoCo)...'
                script {
                    runMaven('verify -Dsurefire.skip=true')   // reutiliza Estrategia 2
                }
                echo 'Pruebas de integracion y cobertura completadas.'
            }
        }

        // ══════════════════════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 3: Análisis de calidad de código
        // ESTRATEGIA 3 — IC por rama: solo se ejecuta en rama main
        //   Las ramas de características reciben retroalimentación rápida
        //   (solo compile + unit tests). Al fusionarse en main, los tres
        //   analizadores corren en paralelo para un análisis completo.
        // ESTRATEGIA 4 — Paralelismo: Checkstyle, PMD y SpotBugs simultáneos
        //   (son independientes entre sí y no comparten archivos temporales)
        // ══════════════════════════════════════════════════════════════════════
        stage('Analisis de Calidad') {
            when {
                expression {
                    return RAMA_ACTUAL == 'main'       ||
                           RAMA_ACTUAL == 'origin/main'
                }
            }
            parallel {

                // ── Checkstyle: convenciones de estilo ────────────────────────
                stage('Checkstyle') {
                    steps {
                        script {
                            runQualityTool('Checkstyle', 'checkstyle:check')
                        }
                    }
                }

                // ── PMD: detección de malas prácticas ─────────────────────────
                stage('PMD') {
                    steps {
                        script {
                            runQualityTool('PMD', 'pmd:check')
                        }
                    }
                }

                // ── SpotBugs: detección de bugs potenciales ───────────────────
                stage('SpotBugs') {
                    steps {
                        script {
                            runQualityTool('SpotBugs', 'spotbugs:check')
                        }
                    }
                }

            } // fin parallel Analisis de Calidad
        }

        // ══════════════════════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 4: Despliegue (Entrega Continua)
        // ESTRATEGIA 3 — IC por rama: solo corre en rama main
        //   Las ramas de características nunca generan ni publican artefactos.
        // ══════════════════════════════════════════════════════════════════════
        stage('Despliegue') {
            when {
                expression {
                    return RAMA_ACTUAL == 'main'       ||
                           RAMA_ACTUAL == 'origin/main'
                }
            }
            steps {
                echo 'Rama main: generando artefacto desplegable...'
                script {
                    runMaven('package -DskipTests')   // reutiliza Estrategia 2
                }
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true,
                                 allowEmptyArchive: true
                echo 'Artefacto empaquetado y archivado. Listo para despliegue.'
                sh 'ls -lh target/'
            }
        }

    } // fin stages

    // ──────────────────────────────────────────────────────────────────────────
    // NOTIFICACIONES POST-EJECUCIÓN
    // ──────────────────────────────────────────────────────────────────────────
    post {
        success {
            echo "Pipeline de ${PROYECTO} [${RAMA_ACTUAL}] finalizado con exito."
        }
        failure {
            echo "Pipeline de ${PROYECTO} [${RAMA_ACTUAL}] fallo. Revisar la etapa en rojo."
        }
        always {
            echo "Rama ejecutada: ${RAMA_ACTUAL}"
        }
    }
}
