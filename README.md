SnackBar
========
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Kennyc1012%2FSnackBar-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/997)
[![API](https://img.shields.io/badge/API-11%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=11)

#Designed after the docs at [Google Material Design](http://www.google.com/design/spec/components/snackbars-and-toasts.html)

![screenshot](https://github.com/Kennyc1012/SnackBar/blob/master/art/phone.gif)

#Features
- Customization including message, action message, action color, message color, background color, action click handle, animation duration, and animation interpolator  
- Tablet support 
- Swipe to dismiss
- Callbacks for the different SnackBar states (started, actionClicked, finished)
- One Message at a time
- XML Style support


#Using SnackBar
#### Using SnackBar is simple, just one line of code is needed!
```java
SnackBar.show(getActivity(), R.string.hello_world);
```
### Or if you want to set an action
```java
SnackBar.show(getActivity(), R.string.hello_world, R.string.undo, onClickListener);
```
#Customization
### SnackBars can be customized by creating a SnackBarItem with the Builder factory
```java
SnackBarItem sbi = new SnackBarItem.Builder(getActivity())
.setMessageResource(R.string.message)
.setActionMessageResource(R.string.action)
.setObject(myObject)
.setActionClickListener(myClickListener)
.setActionMessageColorResource(R.color.my_red)
.setSnackBarMessageColorResource(R.color.my_yellow)
.setSnackBarBackgroundColorResource(R.color.my_green)
.setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
.setDuration(5000)
.setSnackBarListener(myListener)
.show();
```

###SnackBars can also be styled via the application theme
The following attributes can be used for styling a SnackBar
```xml
   <attr name="snack_bar_background_color" format="color" />
   <attr name="snack_bar_text_color" format="color" />
   <attr name="snack_bar_text_action_color" format="color" />
   <attr name="snack_bar_duration" format="integer" />
   <attr name="snack_bar_interpolator" format="reference" />
   ...
   ...
   ...
   ...
   <style name="MyTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:windowBackground">@color/background_material_light</item>
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="snack_bar_text_color">@color/my_color</item>
        <item name="snack_bar_duration">5000</item>
    </style>
```


### Receive callbacks from SnackBars with an Object
```java
// Called when the SnackBar begins to animate
@Override
publc void onSnackBarStarted(Object object){
}

// Called when the action button is pressed
@Override
public void onSnackBarAction(Object object){
}

// Called when the SnackBar finishes with its animation
// Will be called if the action button is pressed
@Override
public void onSnackBarFinished(Object object){
}
```

# Canceling SnackBars
### When your activity goes into a Paused or Destroyed state, remove the SnackBars from the queue
```java
SnackBar.cancelSnackBars(getActivity());
```

#Migrating from 1.X 
Version 2.X has brought many changes. 

First, support for pre HoneyComb has been dropped. Version 1.1 is still available, but no more work will be devoted to anything pre SDK 11. 

Second, the snack_bar_text_action_color_pressed attribute has been removed. The action on the SnackBar is now a native button, so it will used the built in selector for the pressed states.

Lastly, the SnackBarItem.Builder class now takes an activity in the constructor to allow the passing of resource ids into the builder methods. 


#Including in your project
To include SnackBar in your project, add the following to your build.gradle file.
```groovy
repositories {
   maven { url 'https://dl.bintray.com/kennyc1012/maven' }
}


dependencies {
    compile 'com.kennyc:snackbar:2.0.1'
}
```

If you need support for pre HoneyComb, you can still use the 1.1 release, but all support has been dropped for it
```groovy
repositories {
   maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}


dependencies {
    // For api8+ you also need to comple NineOldAndroids
    compile 'com.github.kennyc1012:snackbar:1.1-SNAPSHOT:api8Release@aar'
    compile 'com.nineoldandroids:library:2.4.0'
}
```


#Contribution
Pull requests are welcomed and encouraged. If you experience any bugs, please [file an issue](https://github.com/Kennyc1012/SnackBar/issues/new)
