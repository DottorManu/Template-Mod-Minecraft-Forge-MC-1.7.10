rm -r /home/vscode/.gradle
cp -r ./.gradle/hidden_gradle /home/vscode/.gradle
./gradlew build