pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    option {
        buildDiscarder(logRotator(artifactNumToKeepStr: '10'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package install javadoc:javadoc'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                    archiveArtifacts artifacts: 'target/nukkit-*-SNAPSHOT.jar', fingerprint: true
                    step([
                        $class: 'JavadocArchiver',
                        javadocDir: 'target/site/apidocs',
                        keepAll: true
                    ])
                }
            }
        }
    }
}