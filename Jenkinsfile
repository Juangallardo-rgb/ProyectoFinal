// ══════════════════════════════════════════════════════════════
// ESTRATEGIA 2 — Pipeline modular y reutilizable
// La función runMaven() puede usarse en cualquier etapa
// o en otros proyectos Maven copiando solo esta función.
// ══════════════════════════════════════════════════════════════
def runMaven(String goal) {
    echo "Ejecutando Maven: ${goal}"
    bat "mvn ${goal} -B"
}

pipeline {
    agent any

    environment {
        PROYECTO   = 'ProyectoFinal'
        MAVEN_OPTS = '-Xmx512m'
    }

    stages {

        // ──────────────────────────────────────────────────────
        // CHECKOUT — Obtiene el código desde el repo privado
        // ──────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "Repositorio ${PROYECTO} clonado correctamente"
                bat 'dir'
            }
        }

        // ══════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 1: Compilación
        // Compila el código fuente con Maven
        // ══════════════════════════════════════════════════════
        stage('Compilacion') {
            steps {
                script {
                    // ESTRATEGIA 2 — uso del módulo reutilizable
                    runMaven('clean compile')
                }
            }
        }

        // ══════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 2 y 3 combinadas: Pruebas + Calidad
        // ESTRATEGIA 4 — Paralelismo: corren al mismo tiempo
        // ══════════════════════════════════════════════════════
        stage('Pruebas y Calidad en Paralelo') {
            parallel {

                // ── Rama paralela 1: Pruebas unitarias ────────
                stage('Pruebas Unitarias') {
                    steps {
                        script {
                            // ESTRATEGIA 2 — módulo reutilizable
                            runMaven('test')
                        }
                        echo 'Pruebas unitarias completadas.'
                    }
                }

                // ── Rama paralela 2: Análisis de calidad ──────
                stage('Analisis de Calidad - Checkstyle') {
                    steps {
                        script {
                            // ESTRATEGIA 2 — módulo reutilizable
                            // Checkstyle ya configurado en checkstyle.xml
                            runMaven('checkstyle:check')
                        }
                        echo 'Analisis de calidad completado.'
                    }
                }

            }
        }

        // ══════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 4: Despliegue (CD)
        // ESTRATEGIA 3 — CI por rama:
        //   Solo se ejecuta si la rama activa es 'main'.
        //   En ramas feature/* esta etapa se omite automáticamente.
        // ══════════════════════════════════════════════════════
        stage('Despliegue') {
            when {
                expression {
                    return env.GIT_BRANCH == 'origin/main' ||
                           env.GIT_BRANCH == 'main' ||
                           env.BRANCH_NAME == 'main'
                }
            }
            steps {
                echo 'Rama main detectada: ejecutando despliegue completo...'
                script {
                    // ESTRATEGIA 2 — módulo reutilizable
                    runMaven('package -DskipTests')
                }
                echo 'Artefacto generado en /target. Listo para despliegue.'
                bat 'dir target'
            }
        }

    }

    // ══════════════════════════════════════════════════════════
    // POST — Resultado final del pipeline
    // ══════════════════════════════════════════════════════════
    post {
        success {
            echo "Pipeline de ${PROYECTO} ejecutado con exito."
        }
        failure {
            echo "Pipeline de ${PROYECTO} fallo. Revisar la etapa en rojo."
        }
        always {
            echo "Rama ejecutada: ${env.GIT_BRANCH ?: env.BRANCH_NAME ?: 'main'}"
        }
    }
}