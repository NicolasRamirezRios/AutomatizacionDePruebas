pipeline {
    agent {
        docker {
            image 'georgeyu/maven-corretto:3.8.6-22' // Imagen pública con Maven y Corretto 22
            args '-v /root/.m2:/root/.m2'            // Montar volumen para la caché de dependencias
        }
    }

    environment {
        GIT_REPO = 'https://github.com/NicolasRamirezRios/AutomatizacionDePruebas.git'
        BRANCH = 'main'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${env.BRANCH}", url: "${env.GIT_REPO}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -Dtest=runners.TestRunner test'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/cucumber-reports.html', allowEmptyArchive: true
        }
    }
}
