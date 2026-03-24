pipeline {
    agent any

    environment {
        // 1. 프로젝트 설정 변수
        DOCKER_IMAGE = "jhh0101/portfolio"      // 도커 허브 이미지 이름
        DOCKER_TAG = "v${BUILD_NUMBER}"         // 이번 빌드의 고유 태그 (예: v25)
        GIT_REPO_URL = "github.com/jhh0101/portfolio.git" // 깃허브 주소 (https:// 제외)

        // 이메일과 이름은 깃허브 커밋 기록용
        GIT_EMAIL = "jenkins@bot.com"
        GIT_NAME = "Jenkins Bot"
    }

    stages {
        stage('1. Git Checkout') {
            steps {
                // 깃허브에서 소스 코드 가져오기
                git branch: 'main', url: "https://${GIT_REPO_URL}"
            }
        }

        stage('2. Build Gradle') {
            steps {
                // 백엔드 프로젝트 폴더로 이동
                dir('AuctionMarket') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('3. Docker Build & Push') {
            steps {
                dir('AuctionMarket') {
                    script {
                        // 도커 허브 로그인 및 이미지 푸시
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-login', passwordVariable: 'DOCKER_PW', usernameVariable: 'DOCKER_ID')]) {
                            // 1. 로그인
                            sh "docker login -u ${DOCKER_ID} -p ${DOCKER_PW}"

                            // 2. 버전 태그(v25)로 빌드하고 푸시
                            sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                            sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"

                            // 3. latest 태그로도 하나 더 만들어서 푸시 (관리 편의용)
                            sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                            sh "docker push ${DOCKER_IMAGE}:latest"
                        }
                    }
                }
            }
        }

        stage('4. Update Manifest & Git Push') {
            steps {
                // 깃허브 푸시를 위한 인증 정보 로드 (Personal Access Token 필수)
                withCredentials([usernamePassword(credentialsId: 'github-token', passwordVariable: 'GIT_PW', usernameVariable: 'GIT_USER')]) {
                    dir('AuctionMarket') {
                        script {
                            // --- [ 핵심: YAML 파일 내용 수정 ] ---

                            // sed 명령어로 'image:' 뒤에 뭐가 있든 현재 빌드 버전(v${BUILD_NUMBER})으로 교체
                            sh "sed -i 's|image: ${DOCKER_IMAGE}:.*|image: ${DOCKER_IMAGE}:${DOCKER_TAG}|g' k8s/deployment.yml"

                            // 잘 바뀌었는지 로그로 확인 (디버깅용)
                            sh "echo '변경된 이미지 태그 확인:'"
                            sh "cat k8s/deployment.yml | grep image:"

                            // --- [ 핵심: 변경 사항 깃허브에 저장 ] ---

                            sh """
                                git config user.email "${GIT_EMAIL}"
                                git config user.name "${GIT_NAME}"

                                # 변경된 파일 스테이징
                                git add k8s/deployment.yml

                                # 커밋 (무한 루프 방지를 위해 [skip ci] 추가)
                                git commit -m "Deploy: Update image to ${DOCKER_TAG} [skip ci]"

                                # 깃허브로 푸시 (인증 정보 포함)
                                git push https://${GIT_USER}:${GIT_PW}@${GIT_REPO_URL} HEAD:main
                            """
                        }
                    }
                }
            }
        }

        stage('5. Deploy to Kubernetes') {
            steps {
                withKubeConfig([credentialsId: 'k8s-config']) {
                    dir('AuctionMarket') {
                        script {
                            // 1. 변경된 deployment.yml 적용 (이 파일엔 이미 v25가 적혀있음)
                            sh "kubectl apply -f k8s/deployment.yml"

                            // 2. Service도 적용 (변경사항 없어도 안전함)
                            sh "kubectl apply -f k8s/service.yml"

                            // 3. ★ 강제 롤아웃 재시작 (가장 중요)
                            // 이미지가 확실하게 새로 받아지도록 기존 파드를 죽이고 새로 띄움
                            sh "kubectl rollout restart deployment/backend-deployment"

                            // 4. 배포 상태 확인
                            sh "kubectl rollout status deployment/backend-deployment"
                        }
                    }
                }
            }
        }
    }
}