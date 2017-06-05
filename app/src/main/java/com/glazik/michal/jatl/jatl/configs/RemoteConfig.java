package com.glazik.michal.jatl.jatl.configs;

import android.support.annotation.NonNull;

import com.glazik.michal.jatl.jatl.BuildConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RemoteConfig
{

    // Remote Config keys
    private static final String COLUMNS_NUMBER_CONFIG_KEY = "columns_number";
    private static final int COLUMNS_NUMBER_CONFIG_DEFAULT_VALUE = 1;

    private final static FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private final static Set<OnConfigLoadedListener> listeners = new HashSet<>();

    private static boolean isLoaded = false;

    static {
        init();
    }

    private static void init() {
        //TODO set up config settings and default values for firebaseRemoteConfig using FirebaseRemoteConfigSettings and call fetch()

        //SOLUTION
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(COLUMNS_NUMBER_CONFIG_KEY, COLUMNS_NUMBER_CONFIG_DEFAULT_VALUE);

        firebaseRemoteConfig.setDefaults(defaultConfigMap);
        fetch();
    }

    private static void fetch() {
        long cacheExpiration = 3600;

        if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseRemoteConfig.activateFetched();

                        isLoaded = true;
                        for (OnConfigLoadedListener listener : listeners) {
                            listener.onConfigLoaded();
                        }

                        listeners.clear();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                    }
                });
    }

    public static void addOnConfigLoadedListener(OnConfigLoadedListener onConfigLoadedListener) {
        if (isLoaded) {
            onConfigLoadedListener.onConfigLoaded();
        } else {
            listeners.add(onConfigLoadedListener);
        }
    }

    public static int getDefaultNumberOfColumns() {
        return (int) firebaseRemoteConfig.getLong(COLUMNS_NUMBER_CONFIG_KEY);
    }
}