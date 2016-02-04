# ResearchStack

ResearchStack is an SDK and UX framework for building research study apps on Android. This is very much a work in progress that will constantly be changing and breaking anything you build on top of it.

###Backbone
- The core building blocks of Research Stack
- Tasks, Steps, and Results
- Consent
- File/Database Storage and Encryption

###Skin
- Framework/template that pulls together Backbone components to take most of the work out of building a ResearchStack app
- Designed to work with minor changes to an existing ResearchKit iOS app's resources

###Backbone App
- An example app using only the Backbone module
- Shows how to present a Consent document and a survey to the user
- Shows how to use StorageAccess to save and load consent, surveys, and files

###Sample App
- An example app using the Skin module
- Currently using resources from the Asthma ResearchKit app

## Deploying libraries to MavenLocal

- The libraries are not on jcenter or maven central yet, so you need to deploy them to your local machine to use in other projects
- Go to Run > Edit Configurations...
- Add new configuration (plus icon)
- Choose Gradle as the configuration type
- Set the name to "Deploy Backbone locally" or something
- Choose the **/backbone/build.gradle** file as the "Gradle project" field
- Add "install" to the tasks field
- Save
- Run that configuration and check ~/.m2 to see if it deployed
- Repeat with **/skin/build.gradle**

## Using the library in a gradle Android app

After deploying the library module(s) that you need to your local machine using the above instructions, add it to your Android Studio project by doing this:

In the build.gradle file in your app folder, add the following to your root build.gradle so that it knows to search the ~/.m2 directory for dependencies:

```
repositories {
    mavenLocal()
}
```

Then add the following entry to your dependencies in your app build.gradle:

```
dependencies {
    ...
    compile 'co.touchlab.researchstack:backbone:0.0.3'
    // or (if using Skin, you don't need Backbone since it is included)
    compile 'co.touchlab.researchstack:skin:0.0.3'
    ...
}
```

You will need to add some ResearchStack Activities to your apps AndroidManifest.xml. Look at Backbone App or Sample App's manifest for an example.

## Converting an existing ResearchKit AppCore iOS app to ResearchStack's Skin

This isn't well documented or even well implemented at this point, but this should get you started:

- All file-names must to be changed to lowercase (with or without ‘_’ separator).
- JSON / HTML / CSS / MP4 files exist under sampleapp/src/main/res/raw
- Any images must be defined in res/drawable with density bucket attribute (i.e. drawable-xhdpi). Any HTML Doc that defines an image will look at that directory.
- CSS font-family attribute must be changed to what exists on the system (i.e. sans-serif, sans-serif-light, etc..)
- You will need to implement a class that extends the ResearchStack class and pass an instance of it into ResearchStack.init() in your Application.onCreate() method
- Inside your ResearchStack implementation you may need to return your own implementations of things such as ResourceManager to point to your own resources
- Look at sample app for examples of all of this. Most of the resources were pulled from the [Asthma iOS app](https://github.com/researchkit/AsthmaHealth) and modified base on the above points.

## Tasks and Steps

Tasks and Steps should function very similarly to Apple's ResearchKit. Extend Task if you need to do something different with step order that's not in OrderedTask or SmartSurveyTask.

If you want to implement a custom Step, create a step and make getStepLayoutClass() return the Class of your own extension of StepLayout. This provides the View for your custom step and is responsible for creating the StepResult and passing it back up to the ViewTaskActivity.

If you just want a custom QuestionStep with an answer type that isn't supported yet, you will need to just create your own AnswerFormat subclass. All QuestionSteps use the same StepLayout, but the AnswerFormat provides a StepBody class that determines what the inner UI for the question looks like (date picker, text field, slider, etc). Right now this is a weird hybrid of the way ResearchKit does things and the way ResearchStack does things, so it will probably change and break any custom steps you make at some point.
