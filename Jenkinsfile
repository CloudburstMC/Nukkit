pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    option {
        buildDiscarder(logRotator(artifactNumToKeepStr: '1'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/nukkit-*-SNAPSHOT.jar', fingerprint: true
                    junit '**/target/surefire-reports/**/*.xml'
                }
            }
        }

        stage ('Javadoc') {
            steps {
                sh 'mvn javadoc:javadoc -pl api'
            }
            post {
                success {
                    step([
                        $class: 'JavadocArchiver',
                        javadocDir: 'api/target/site/apidocs',
                        keepAll: true
                    ])
                }
            }
        }

        stage ('Deploy') {
            when {
                branch "master"
            }
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }
}