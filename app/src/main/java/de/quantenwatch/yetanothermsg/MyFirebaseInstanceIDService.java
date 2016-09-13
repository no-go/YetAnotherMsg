package de.quantenwatch.yetanothermsg;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onTokenRefresh() {
        String freshToken = FirebaseInstanceId.getInstance().getToken();
        String oldToken = mSharedPreferences.getString("preference_token", "");
        if (!oldToken.equals(freshToken)) {
            Log.d("token", freshToken);
            mSharedPreferences.edit().putString("preference_token", freshToken).apply();
            MainActivity.registerToken(freshToken);
        }
    }
}
