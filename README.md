# WhoAreYou

### USAGE

1. add dependencies

```groovy
compile 'io.dove:findactivity:0.0.2'
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
