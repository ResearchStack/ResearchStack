# RK

## Development Setup

- Import project from version control in Android Studio
- Go to Run > Edit Configurations...
- Add new configuration (plus icon)
- Choose Gradle as the configuration type
- Set the name to "Deploy locally" or something
- Choose the project root build.gradle file as the "Gradle project" field
- Add "install" to the tasks field
- Save
- Run that configuration and see if it worked

## Using the library in a gradle Android app

In the build.gradle file in your app folder, add the following:

```
repositories {
    mavenLocal()
}
```

Then add the following entry to your dependencies:

```
dependencies {
    ...
    compile 'co.touchlab.researchstack:researchstack:0.0.3'
    ...
}
```
