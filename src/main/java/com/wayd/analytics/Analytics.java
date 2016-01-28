package com.wayd.analytics;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.amplitude.api.Amplitude;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Analytics is common interface for Flurry, Amplitude, AppsFlyer and Fabric Answers.
 * Correctly initialize Fabric Answers(but don't send events to it).
 */
public final class Analytics {

    public static final String KEY_AMPLITUDE = "amplitude";
    public static final String KEY_APPS_FLYER = "appsflyer";
    public static final String KEY_FLURRY = "flurry";

    private Analytics() {
    }

    private static class Holder {
        public static final Analytics HOLDER_INSTANCE = new Analytics();
    }

    public static Analytics getInstance() {
        return Holder.HOLDER_INSTANCE;
    }

    /**
     * Amplitude, AppsFlyer, Flurry initializing
     *
     * @param app  - current application
     * @param keys - api keys
     */
    public static void init(Application app, Map<String, String> keys) {
        if (keys.containsKey(KEY_AMPLITUDE)) {
            Amplitude.getInstance().initialize(app, keys.get(KEY_AMPLITUDE))
                    .enableForegroundTracking(app);
        }

        if (keys.containsKey(KEY_APPS_FLYER)) {
            AppsFlyerLib.setAppsFlyerKey(keys.get(KEY_APPS_FLYER));
            AppsFlyerLib.sendTracking(app);
        }

        if (keys.containsKey(KEY_FLURRY)) {
            FlurryAgent.init(app, keys.get(KEY_FLURRY));
        }

        Log.i("Analytics", "Please init Fabric Answers if you need");
    }

    /**
     * Track event(Amplitude, AppsFlyer, Flurry and Fabric Answers if inited)
     *
     * @param context        - activity context
     * @param name           - event name
     * @param properties     - event properties
     * @param sendToTrackers - send to trackers Fabric Answers and AppsFlyer or not
     */
    public void trackEvent(Context context, String name, Map<String, String> properties,
                           boolean sendToTrackers) {
        //Amplitude
        Amplitude.getInstance().logEvent(name, new JSONObject(properties));

        //Flurry
        FlurryAgent.logEvent(name, properties);

        if (sendToTrackers) {
            //Answers
            if (Fabric.isInitialized() && Answers.getInstance() != null) {
                CustomEvent answersEvent = new CustomEvent(name);
                for (String k : properties.keySet()) {
                    answersEvent.putCustomAttribute(k, properties.get(k));
                }
                Answers.getInstance().logCustom(answersEvent);
            }

            //AppsFlyer
            HashMap<String, Object> dataObjects = new HashMap<>();
            for (String k : properties.keySet()) {
                dataObjects.put(k, properties.get(k));
            }
            AppsFlyerLib.trackEvent(context, AFInAppEventType.LEVEL_ACHIEVED, dataObjects);
        }

        Log.i("Analytics", String.format(
                "ANALYTICS%s: %s, %s", sendToTrackers ? "+TRACKERS" : "", name, properties));
    }

    public void trackEvent(Context context, String name, Map<String, String> properties) {
        trackEvent(context, name, properties, false);
    }

    public void trackEvent(Context context, String name) {
        trackEvent(context, name, new HashMap<String, String>());
    }

    public void trackScreen(Context context, String name) {
        trackEvent(context, name + "_SHOWN", new HashMap<String, String>(), false);
        Log.i("Analytics", String.format("ANALYTICS SCREEN: %s", name));
    }

    public void trackError(Context context, String name, Map<String, String> properties) {
        HashMap<String, String> props = new HashMap<>(properties);
        props.put("error_name", name);

        trackEvent(context, "ERROR", props, false);
    }

    public void trackPurchaseWithItemName(Context context, String name, int amount, String currency) {
        HashMap<String, String> props = new HashMap<>();
        props.put("name", name);
        props.put("amount", amount + "");
        props.put("currency", currency);

        trackEvent(context, "PURCHASE_" + name, props, true);
    }

    public void trackSignUp(Context context, String method, Map<String, String> properties) {
        HashMap<String, String> props = new HashMap<>(properties);
        props.put("method", method);

        trackEvent(context, "SIGNUP", props, true);
    }

    public void trackLogin(Context context, String method, Map<String, String> properties) {
        HashMap<String, String> props = new HashMap<>(properties);
        props.put("method", method);

        trackEvent(context, "LOGGED_IN", props, true);
    }
}