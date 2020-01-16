pipeline {
    agent any
    tools {
        jdk 'Java 8'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5'))
    }
    stages {
        stage ('Build') {
            steps {
                sh './gradlew clean build test'
            }
            post {
                success {
                    junit 'build/test-results/**/*.xml'
                    archiveArtifacts artifacts: 'target/nukkit-*-SNAPSHOT.jar', fingerprint: true
                }
            }
        }

        stage ('Deploy') {
            when {
                branch "master"
            }
            steps {
                sh './gradlew javadoc distZip deploy -DskipTests'
                step([$class: 'JavadocArchiver', javadocDir: 'build/docs/javadoc', keepAll: false])
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}