# ResearchStack

ResearchStack is an SDK and UX framework for building research study apps on Android.

Be sure to check out [researchstack.org](http://researchstack.org/) and the [ResearchStack forum](https://groups.google.com/forum/#!forum/researchstack) for general information and announcements about the framework.

## Documentation

Documentation is written and maintained using [Javadoc](http://www.oracle.com/technetwork/java/javase/documentation/index-jsp-135444.html):
- [Backbone Documentation](http://researchstack.org/documentation/backbone/)
- [Skin Documentation](http://researchstack.org/documentation/skin/)

## Download
Add one or both to your app/build.gradle:
```groovy
compile 'org.researchstack:backbone:1.1.1'
compile 'org.researchstack:skin:1.1.1'
```

You may also need to add the following source repos to your project's build.gradle:
```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" } // for MPAndroidChart dependency, not on jcenter yet
    }
}
```

##Backbone
- The core building blocks of Research Stack
- Tasks, Steps, and Results
- Consent
- File/Database Storage and Encryption

##Skin
- Framework/template that pulls together Backbone components to take most of the work out of building a ResearchStack app
- Designed to work with minor changes to an existing ResearchKit™ iOS app's resources

##Examples
- [Sample App](https://github.com/ResearchStack/SampleApp)
  * Shows how to create a ResearchStack app using the skin framework.
- [Backbone Example App](https://github.com/ResearchStack/BackboneExampleApp)
  * Uses only backbone features.
  * Shows how to create simple tasks (consent and a survey), present them to the user, and process the results.
- [MoleMapper Android](https://github.com/ResearchStack/MoleMapperAndroid)
  * The full source code of the MoleMapper Android app, which will be available soon on Google Play.
  * Useful to compare against [the open source code of iOS/ResearchKit implementation of Mole Mapper](https://github.com/Sage-Bionetworks/MoleMapper).

## Things to look out for when converting your ResearchKit™ app

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

Tasks and Steps should function very similarly to Apple's ResearchKit™. Extend Task if you need to do something different with step order that's not in OrderedTask or SmartSurveyTask.

If you want to implement a custom Step, create a step and make getStepLayoutClass() return the Class of your own extension of StepLayout. This provides the View for your custom step and is responsible for creating the StepResult and passing it back up to the ViewTaskActivity.

If you just want a custom QuestionStep with an answer type that isn't supported yet, you will need to just create your own AnswerFormat subclass. All QuestionSteps use the same StepLayout, but the AnswerFormat provides a StepBody class that determines what the inner UI for the question looks like (date picker, text field, slider, etc).

## Third-party libraries used

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
com.squareup.okhttp3:logging-interceptor
com.squareup.retrofit2:adapter-rxjava
com.squareup.retrofit2:converter-gson
com.squareup.retrofit2:retrofit
</b>

- Networking libraries used for network interface
