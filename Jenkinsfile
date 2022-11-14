def p = [:]
node {
	checkout scm
	p = readProperties interpolate: true, file: 'ci/release.properties'
}

pipeline {
	agent none

	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '14'))
	}

	stages {
		stage('Ship It') {
			when {
				branch 'release'
			}
			agent {
				docker {
					image 'springci/spring-data-release-tools:0.1'
				}
			}
			options { timeout(time: 4, unit: 'HOURS') }

			environment {
				GIT_USERNAME = credentials('spring-data-release-git-username')
				GIT_AUTHOR = credentials('spring-data-release-git-author')
				GIT_EMAIL = credentials('spring-data-release-git-email')
				GIT_PASSWORD = credentials('3a20bcaa-d8ad-48e3-901d-9fbc941376ee')
				DEPLOYMENT_API_KEY = credentials('repo_spring_io-jenkins-release-token')
				STAGING_PROFILE_ID = credentials('spring-data-release-deployment-maven-central-staging-profile-id')
				PASSPHRASE = credentials('spring-gpg-passphrase')
				KEYRING = credentials('spring-signing-secring.gpg')
				GIT_SIGNING_KEY = credentials('spring-gpg-github-private-key-jenkins')
				GIT_SIGNING_KEY_PASSWORD = credentials('spring-gpg-github-passphrase-jenkins')
				SONATYPE = credentials('oss-login')
				GPG_KEYNAME = credentials('spring-data-release-gpg-keyname')
			}

			steps {
				script {
					sh "ci/build-spring-data-release-cli.bash"
					sh "ci/prepare-and-build.bash ${p['release.version']}"

					slackSend(
						color: (currentBuild.currentResult == 'SUCCESS') ? 'good' : 'danger',
						channel: '#spring-data-dev',
						message: (currentBuild.currentResult == 'SUCCESS')
								? "`${env.BUILD_URL}` - Build and deploy passed! Conduct smoke tests then report back here."
								: "`${env.BUILD_URL}` - Push and distribute failed!")

					input("SMOKE TEST: Did the smoke tests for ${p['release.version']} pass? Accept to conclude and distribute the release.")

					sh "ci/conclude.bash ${p['release.version']}"
					sh "ci/push-and-distribute.bash ${p['release.version']}"

					slackSend(
						color: (currentBuild.currentResult == 'SUCCESS') ? 'good' : 'danger',
						channel: '#spring-data-dev',
						message: (currentBuild.currentResult == 'SUCCESS')
								? "`${env.BUILD_URL}` - Push and distribute ${p['release.version']} passed! Release the build (if needed)."
								: "`${env.BUILD_URL}` - Push and distribute ${p['release.version']} failed!")
				}
			}
		}
	}

	post {
		changed {
			script {
				slackSend(
						color: (currentBuild.currentResult == 'SUCCESS') ? 'good' : 'danger',
						channel: '#spring-data-dev',
						message: "${currentBuild.fullDisplayName} - `${currentBuild.currentResult}`\n${env.BUILD_URL}")
				emailext(
						subject: "[${currentBuild.fullDisplayName}] ${currentBuild.currentResult}",
						mimeType: 'text/html',
						recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']],
						body: "<a href=\"${env.BUILD_URL}\">${currentBuild.fullDisplayName} is reported as ${currentBuild.currentResult}</a>")
			}
		}
	}
}
