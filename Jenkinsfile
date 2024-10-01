pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/NicolasRamirezRios/AutomatizacionDePruebas.git'
        BRANCH = 'main'
    }

    stages {
        stage('Checkout') {
            steps {
                // Descarga el código desde el repositorio
                git branch: "${env.BRANCH}", url: "${env.GIT_REPO}"
            }
        }

        stage('Build') {
            steps {
                // Compilar el proyecto
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                // Ejecutar la clase TestRunner directamente usando Maven
                sh 'mvn -Dtest=runners.TestRunner test'
            }
        }
    }

    post {
        always {
            // Publicar los resultados de las pruebas en el reporte de Jenkins
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/cucumber-reports.html', allowEmptyArchive: true
        }
    }
}
