SnackBar
========
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Kennyc1012%2FSnackBar-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/997)

#Designed after the docs at [Google Material Design](http://www.google.com/design/spec/components/snackbars-and-toasts.html)

![phone](https://github.com/Kennyc1012/SnackBar/raw/master/phone.gif)

![tablet](https://github.com/Kennyc1012/SnackBar/raw/master/tablet.gif)

#Features
- Customization including message, action message, action color, message color, background color, action click handle, animation duration, and animation interpolator  
- Tablet support 
- Swipe to dismiss
- Callbacks for the different SnackBar states (started, actionClicked, finished)
- One Message at a time
- Support for Api version 8+


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
SnackBarItem sbi = new SnackBarItem.Builder()
.setMessage("Message")
.setActionMessage("Action")
.setObject(myObject)
.setActionClickListener(myClickListener)
.setActionMessageColor(getResources().getColor(R.color.my_red))
.setActionMessagePressedColor(getResources().getColor(R.color.my_blue))
.setSnackBarMessageColor(getResources().getColor(R.color.my_yellow))
.setSnackBarBackgroundColor(getResources().getColor(R.color.my_green)
.setInterpolator(new OvershootInterpolator())
.setDuration(5000)
.setSnackBarListener(myListener)
.build();

SnackBar.show(getActivity(),sbi);
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

#Including in your project
To include SnackBar in your project, add the following to your build.gradle file.
```groovy
repositories {
   maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    mavenCentral()
}


dependencies {
    // For api11+
    compile 'com.github.kennyc1012:snackbar:1.0.2-SNAPSHOT:api11Release@aar'
    // For api8+
    compile 'com.github.kennyc1012:snackbar:1.0.2-SNAPSHOT:api8Release@aar'
}
```


#Contribution
Pull requests are welcomed and encouraged. If you experience any bugs, please [file an issue](https://github.com/Kennyc1012/SnackBar/issues/new)
