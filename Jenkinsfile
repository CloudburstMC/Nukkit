pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '1'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package javadoc:javadoc'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
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
                allOf {
                    currentBuild.result == 'SUCCESS'
                    branch "master"
                }
            }
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }
}