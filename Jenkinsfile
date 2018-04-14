pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '2'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/nukkit-*-SNAPSHOT.jar', fingerprint: true
                }
            }
        }

        stage ('Javadoc') {
            when {
                branch "main"
            }
            steps {
                sh 'mvn javadoc:aggregate -DskipTests -pl api'
            }
            post {
                success {
                    step([
                        $class: 'JavadocArchiver',
                        javadocDir: 'target/site/apidocs',
                        keepAll: true
                    ])
                }
            }
        }

        stage ('Deploy') {
            when {
                branch "rewrite"
            }
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }
}