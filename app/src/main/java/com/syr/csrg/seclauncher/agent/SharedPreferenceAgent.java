package com.syr.csrg.seclauncher.agent;

import android.content.Context;
import android.content.SharedPreferences;

import com.syr.csrg.seclauncher.util.SecureLauncherConstants;

public class SharedPreferenceAgent {
    public static SharedPreferences getSharedPrefPrivate(Context context){
        return context.getApplicationContext().getSharedPreferences(SecureLauncherConstants.PROJECT_PACKAGE, Context.MODE_PRIVATE);
    }

}
