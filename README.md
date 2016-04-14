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
- Open the **ResearchStack project** in Android Studio
- Go to Run > Edit Configurations...
- Add new configuration (plus icon)
- Choose Gradle as the configuration type
- Set the name to "Deploy Backbone locally" or something
- Choose the **/backbone/build.gradle** file as the "Gradle project" field
- Add "install" to the tasks field
- Save
- Run that configuration and check ~/.m2 to see if it deployed
- Repeat with **/skin/build.gradle**
- Open your own Android Studio project and follow the instructions below to add these newly-deployed libraries as dependencies.

## Using the library in a gradle Android app

After deploying the library module(s) that you need to your local machine using the above instructions, add it to your Android Studio project by doing this:

In the build.gradle file in your app folder, add the following to your root build.gradle so that it knows to search the ~/.m2 directory for dependencies:

```
allprojects {
    repositories {
        ...
        mavenLocal()
        ...
    }
}
```

Then add the following entry to your dependencies in your app build.gradle:

```
dependencies {
    ...
    compile 'org.researchstack:backbone:0.0.3'
    // or (if using Skin, you don't need Backbone since it is included)
    compile 'org.researchstack:skin:0.0.3'
    ...
}
```

You will need to add some ResearchStack Activities to your apps AndroidManifest.xml. Look at Backbone App or Sample App's manifest for an example.

## Converting an existing ResearchKit AppCore iOS app to ResearchStack's Skin

This isn't well documented or even well implemented at this point, but this should get you started:
- Assets can be dragged and dropped into the assets folder of your project. 
- Please make sure to validate and check for malformed JSON
- Please make sure your links define a url in your HTML. An empty HREF attribute will reload the same file in a different activity.
- ResearchStack adds attributes to the documentProperties object for consent. Additionally, ResearchStack also supports quiz conent define in the same json file. Please update your ConsentSection json file to reflect these changes. An example can be found in the SampleApp project. 
- Images can be defined in the same directory of an HTML doc or through android's drawable directories. You can do this using the following path as an example "file:///android_res/drawable/image_name.png". Please note, loading assets through drawable path breaks when using applicationIdSuffix in gradle.
- CSS font-family attribute should be changed to what exists on the system (i.e. sans-serif, sans-serif-light, etc..).
- You will need to implement a class that extends the ResearchStack class and pass an instance of it into ResearchStack.init() in your Application.onCreate() method
- Inside your ResearchStack implementation you may need to return your own implementations of things such as ResourceManager to point to your own resources
- Look at sample app for examples of all of this. Most of the resources were pulled from the [Asthma iOS app](https://github.com/researchkit/AsthmaHealth) and modified base on the above points.

## Tasks and Steps

Tasks and Steps should function very similarly to Apple's ResearchKit. Extend Task if you need to do something different with step order that's not in OrderedTask or SmartSurveyTask.

If you want to implement a custom Step, create a step and make getStepLayoutClass() return the Class of your own extension of StepLayout. This provides the View for your custom step and is responsible for creating the StepResult and passing it back up to the ViewTaskActivity.

If you just want a custom QuestionStep with an answer type that isn't supported yet, you will need to just create your own AnswerFormat subclass. All QuestionSteps use the same StepLayout, but the AnswerFormat provides a StepBody class that determines what the inner UI for the question looks like (date picker, text field, slider, etc). Right now this is a weird hybrid of the way ResearchKit does things and the way ResearchStack does things, so it will probably change and break any custom steps you make at some point.

## Code Style

Contributors should import code_style_settings.jar into Android Studio and use the 'researchstack' Java code style. Make sure to 'Reformat Code' and 'Optimize Imports' using this style (but don't 'Reorganize Code') before submitting a pull request.

## 3<sup>rd</sup>-party library disclosures

<b>
com.android.support:appcompat-v7<br />
com.android.support:cardview-v7<br />
com.android.support:design<br />
com.android.support:preference-v14
</b>

- Used for theming and styling views within the framework. Libraries also provide backward-compatible versions of Android framework APIs (e.g. vector icons, preferences)

<b>com.github.PhilJay:MPAndroidChart</b>

- Charting library used to visualize data

<b>com.android.support:multidex</b>

- MultiDex support library enables us to go past the default 65K method limit for an android project

<b>
co.touchlab.squeaky:squeaky-query
</b>

- Squeaky is a Database ORM for Android, simplifying database functions and used to store Task / Step result information 

<b>
co.touchlab.squeaky:squeaky-processor
</b>

-  Annotation processor for the Squeaky ORMLite database library. The library creates auto-generated code at compile time for our database pojos (see TaskRecord or StepRecord classes)

<b>
net.zetetic:android-database-sqlcipher
</b>

- Enables full 256-bit AES encryption of SQLite database

<b>
com.scottyab:aes-crypto
</b>

-  API to perform AES encryption on Android, used within the FileAccess class to write raw data to disk

<b>
compile 'com.cronutils:cron-utils
</b>

- Used to parse crons within the tasks_and_schedules json file and calculating system notification execution time

<b>
com.google.code.gson:gson
</b>

- Parses and maps json to java objects, used for converting json files defined in raw resource directory

<b>
io.reactivex:rxandroid
io.reactivex:rxjava
</b>

- Used to compose asynchronous and event-based networking and database calls. Also used for event-based UI calls for varius UI widgets. 
    
<b>
com.jakewharton.rxbinding:rxbinding-appcompat-v7<br />
com.jakewharton.rxbinding:rxbinding-design<br />
com.jakewharton.rxbinding:rxbinding-support-v4<br />
com.jakewharton.rxbinding:rxbinding
</b>

- RxJava binding APIs for Android UI widgets from the platform and support libraries. Provides helper methods that wrap API methods and returns Rx Observables

<b>
com.madgag.spongycastle:core<br />
com.madgag.spongycastle:pkix<br />
com.madgag.spongycastle:prov
</b>

- Spongy Castle is a Bouncy Castle implementation for Android. These libraries are used during data upload to Sage Bridge CMS.

<b>
com.squareup.okhttp3:logging-interceptor
com.squareup.retrofit2:adapter-rxjava
com.squareup.retrofit2:converter-gson
com.squareup.retrofit2:retrofit
</b>

- Networking libraries used for communication w/ Sage Bridge REST API
    
