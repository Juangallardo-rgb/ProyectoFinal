// ══════════════════════════════════════════════════════════════
// ESTRATEGIA 2 — Pipeline modular y reutilizable
// ══════════════════════════════════════════════════════════════
def runMaven(String goal) {
    echo "Ejecutando Maven: ${goal}"
    sh "mvn ${goal} -B"
}

pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    environment {
        PROYECTO   = 'ProyectoFinal'
        MAVEN_OPTS = '-Xmx512m'
    }

    stages {

        // ──────────────────────────────────────────────────────
        // CHECKOUT
        // ──────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "Repositorio ${PROYECTO} clonado correctamente"
                sh 'ls -la'
            }
        }

        // ══════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 1: Compilación
        // ══════════════════════════════════════════════════════
        stage('Compilacion') {
            steps {
                script {
                    runMaven('clean compile')
                }
            }
        }

        // ══════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPAS 2 y 3: Pruebas + Calidad
        // ESTRATEGIA 4 — Paralelismo
        // ══════════════════════════════════════════════════════
        stage('Pruebas y Calidad en Paralelo') {
            parallel {

                stage('Pruebas Unitarias') {
                    steps {
                        script {
                            runMaven('test')
                        }
                        echo 'Pruebas unitarias completadas.'
                    }
                }

                stage('Analisis de Calidad - Checkstyle') {
                    steps {
                        script {
                            runMaven('checkstyle:check')
                        }
                        echo 'Analisis de calidad completado.'
                    }
                }

            }
        }

        // ══════════════════════════════════════════════════════
        // ESTRATEGIA 1 — ETAPA 4: Despliegue
        // ESTRATEGIA 3 — CI por rama: solo corre en main
        // ══════════════════════════════════════════════════════
        stage('Despliegue') {
            when {
                expression {
                    return env.GIT_BRANCH == 'origin/main' ||
                           env.GIT_BRANCH == 'main'        ||
                           env.BRANCH_NAME == 'main'
                }
            }
            steps {
                echo 'Rama main detectada: ejecutando despliegue completo...'
                script {
                    runMaven('package -DskipTests')
                }
                echo 'Artefacto generado en /target. Listo para despliegue.'
                sh 'ls -la target/'
            }
        }

    }

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