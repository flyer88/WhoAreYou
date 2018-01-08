# WhoAreYou

### About

A repo to get current activity's name.
When you open your app, you may forget which activity it is.
This repo will help you to find out current activity's name

### Usage

1. add dependencies

```groovy
compile 'io.dove:findactivity:0.1.1'
```

2. insert code to your application

```java
public class YourApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Dove.getInstance(this).init();
    }
}
```
