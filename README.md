# wayd-analytics
Common interface for Flurry, Amplitude, AppsFlyer and Fabric Answers.

Correctly initialize Fabric Answers (but don't send events to them).

```java
//initialize Analytics
public class TheApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Answers());

        HashMap<String, String> keys = new HashMap<>();

        //todo: set keys or remove lines
        keys.put(Analytics.KEY_AMPLITUDE, "your_amplitude_key");
        keys.put(Analytics.KEY_APPS_FLYER, "your_apps_flyer_key");
        keys.put(Analytics.KEY_FLURRY, "your_flurry_key");

        Analytics.init(this, keys);
    }
}
```

```java
	Analytics.getInstance().trackScreen(this, "MAIN");
```

```java
    HashMap<String, String> data = new HashMap<>();
    data.put("type", "test");
    data.put("age", "25");
    data.put("sex", "male");

    Analytics.getInstance().trackEvent(MainActivity.this, "Test Event", data);
```
