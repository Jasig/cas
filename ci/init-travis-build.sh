#!/bin/bash

echo -e "Building branch: ${TRAVIS_BRANCH}"
echo -e "Build directory: ${TRAVIS_BUILD_DIR}"
echo -e "Build id: ${TRAVIS_BUILD_ID}"
echo -e "Builder number: ${TRAVIS_BUILD_NUMBER}"
echo -e "Job id: ${TRAVIS_JOB_ID}"
echo -e "Job number: ${TRAVIS_JOB_NUMBER}"
echo -e "Repo slug: ${TRAVIS_REPO_SLUG}"
echo -e "OS name: ${TRAVIS_OS_NAME}"
echo -e "Pull Request: ${TRAVIS_PULL_REQUEST}"
echo -e "Commit Message: ${TRAVIS_COMMIT_MESSAGE}"
echo -e "Job Type: ${MATRIX_JOB_TYPE}"

if [ "$TRAVIS_SECURE_ENV_VARS" == "false" ]
then
  echo -e "Secure environment variables are NOT available...\n"
else
  echo -e "Secure environment variables are available...\n"
fi

echo -e "Stopping current services...\n"
sudo service mysql stop

echo -e "Setting build environment...\n"
sudo mkdir -p /etc/cas/config /etc/cas/saml /etc/cas/services /etc/cas/config/saml

echo -e "Configuring Oracle JDK8 JCE...\n"
sudo unzip -j -o ./etc/jce8.zip *.jar -d $JAVA_HOME/jre/lib/security
sudo cp ./etc/java.security $JAVA_HOME/jre/lib/security

echo -e "Configuring Gradle wrapper...\n"
chmod -R 777 ./gradlew

echo "Home directory: $HOME"

echo "Gradle Home directory:"
./gradlew gradleHome

echo -e "Installing NPM...\n"
./gradlew npmInstall --stacktrace -q

echo -e "Configured build environment\n"
