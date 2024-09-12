pipeline {
    agent any

    options {
        buildDiscarder(
            logRotator(
                numToKeepStr: '5'
            )
        )
    }

    stages {
        stage("Build on Linux") {
            agent {
                label 'linux'
            }

            tools {
                jdk "jdk17"
            }

            steps {
                script {
                    sh '''
                    #!/bin/bash

                    git clean -xfd
                    git reset --hard
                    git fetch

                    java -version

                    chmod +x gradlew
                    ./gradlew -Dorg.gradle.java.home="$JAVA_HOME" build publish

                    '''
                }
            }

            post {
                always {
                    archiveArtifacts artifacts: "build/libs/*.jar"
                    deleteDir()
                }
            }


        }
    }
}
