# Kotlin + Gradle Project Setup

## Project Location
`/home/user/playground-10/`

## Installed Tools
| Tool   | Version | Path                                         |
|--------|---------|----------------------------------------------|
| JDK    | 21.0.6  | `/home/user/.local/opt/java/jdk-21.0.6+7`    |
| Gradle | 8.10    | `/home/user/.local/opt/gradle/gradle-8.10`   |
| Kotlin | 2.1.0   | `/home/user/.local/opt/kotlin/kotlinc`       |

## PATH (in `~/.bashrc`)
```bash
JAVA_HOME=$HOME/.local/opt/java/jdk-21.0.6+7
GRADLE_HOME=$HOME/.local/opt/gradle/gradle-8.10
KOTLIN_HOME=$HOME/.local/opt/kotlin/kotlinc
export JAVA_HOME GRADLE_HOME KOTLIN_HOME
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$KOTLIN_HOME/bin:$PATH"
```

## Project Structure
```
playground-10/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── gradle/
└── src/main/kotlin/Main.kt
```

## Commands
- Build: `./gradlew build --no-daemon`
- Run:   `./gradlew run --no-daemon`
- Clean: `./gradlew clean --no-daemon`
