pipeline {
    agent any

    environment {
        timestamp = "${System.currentTimeMillis() / 1000L}"
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    // Get the ID of the sbb:latest image
                    def oldImageId = sh(script: "docker images be_ksis:latest -q", returnStdout: true).trim()
                    env.oldImageId = oldImageId
                }

                git branch: 'main',
                    url: 'https://github.com/KangHyeonJu/ksis'
            }

            post {
                success {
                    sh 'echo "Successfully Cloned Repository"'
                }
                failure {
                    sh 'echo "Fail Cloned Repository"'
                }
            }
        }

        stage('Build Gradle') {
            steps {
                dir('.') {
                    sh """
                    chmod +x gradlew
                    """
                }

                dir('.') {
                    sh """
                    ./gradlew clean bootjar
                    """
                }
            }

            post {
                success {
                    sh 'echo "Successfully Build Gradle Test"'
                }
                 failure {
                    sh 'echo "Fail Build Gradle Test"'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t be_ksis:${timestamp} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // Check if the container is already running
                    def isRunning = sh(script: "docker ps -q -f name=be_ksis", returnStdout: true).trim()

                    if (isRunning) {
                        sh "docker rm -f be_ksis"
                    }

                    // Run the new container
                    try {
                        sh """
                        docker run \
                          --name=be_ksis \
                          -v /docker_projects/be_ksis/volumes/gen:/gen \
                          --restart unless-stopped \
                          --network app \
                          -e TZ=Asia/Seoul \
                          -d \
                          be_ksis:${timestamp}
                        """
                    } catch (Exception e) {
                        // If the container failed to run, remove it and the image
                        isRunning = sh(script: "docker ps -q -f name=be_ksis", returnStdout: true).trim()

                        if (isRunning) {
                            sh "docker rm -f be_ksis"
                        }

                        def imageExists = sh(script: "docker images -q be_ksis:${timestamp}", returnStdout: true).trim()

                        if (imageExists) {
                            sh "docker rmi be_ksis:${timestamp}"
                        }

                        error("Failed to run the Docker container.")
                    }

                    // If there's an existing 'latest' image, remove it
                    def latestExists = sh(script: "docker images -q be_ksis:latest", returnStdout: true).trim()

                    if (latestExists) {
                        sh "docker rmi be_ksis:latest"

                        if(!oldImageId.isEmpty()) {
                        	sh "docker rmi ${oldImageId}"
                        }
                    }

                    // Tag the new image as 'latest'
                    sh "docker tag be_ksis:${env.timestamp} be_ksis:latest"
                }
            }
        }
    }
}