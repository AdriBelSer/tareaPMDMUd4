package dam.pmdm.spyrothedragon;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApp extends Application {

    protected static final String PREFS_NAME = "AppPreferences";
    protected static final String KEY_GUIDE = "needGuide";

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public Boolean getSavedNeedGuide() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_GUIDE, true);
    }

    public void saveNeedGuide(Boolean needGuide) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_GUIDE, needGuide);
        editor.apply();
    }
}
