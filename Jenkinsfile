pipeline {

    agent any

    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }

    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '2'))
    }

    environment {
        COMMIT = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
        VERSION = readMavenPom().getVersion()
    }

    stages {
        stage ('Build') {
            steps {
                script {
                    currentBuild.displayName = "${VERSION}-${COMMIT}"
                }
                sh 'mvn clean package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/nukkit-*.jar', fingerprint: true
                }
            }
        }

        stage ('Javadoc') {
            when {
                branch "master"
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