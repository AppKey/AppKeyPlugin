package com.appkey.plugin;

import com.appkey.sdk.AppKeyChecker;
import com.appkey.sdk.AppKeyCheckerCallback;

import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.util.Log;

public class AppKeyPlugin {

    private String TAG="AppKeyPlugin";
    private boolean LOGD=false;
    private static AppKeyPlugin mINSTANCE;
    private Activity mActivity;
    private boolean mPromptUser;
    private String mPremiumContentDescription;

    @SuppressWarnings("unused")
    private static AppKeyPlugin INSTANCE() {
        if (mINSTANCE == null) {
            mINSTANCE=new AppKeyPlugin();
        }
        return mINSTANCE;
    }
    
    public void checkAccess(Activity activity, String AppID, boolean debugLogging, boolean userAnalytics) {
    	LOGD=debugLogging;
        if (LOGD) Log.d(TAG+".checkAccess", "Called with AppID="+AppID+", analyticsEnabled="+userAnalytics);
        checkAccessWithWizard(activity, AppID, debugLogging, userAnalytics,"");
    }
    
    public void checkAccessWithWizard(Activity activity, String AppID, boolean debugLogging, boolean userAnalytics, String premiumContentDescription) {
    	LOGD=debugLogging;
        if (LOGD) Log.d(TAG+".checkAccessWithWizard", "Called with AppID="+AppID+", premiumContentDescription="+premiumContentDescription);
        if (premiumContentDescription==null) premiumContentDescription="";
        
        if (premiumContentDescription=="") {
            mPromptUser=false;
        } else {
            mPromptUser=true;
            mActivity=activity;
            mPremiumContentDescription=premiumContentDescription;
        }
        
        //The checker currently uses AsyncTask and needs to be run on the UI thread
        final Activity finalActivity = activity;
        final String finalAppID = AppID;
        final boolean finalAnalyticsEnabled = userAnalytics;
        Runnable runChecker=new Runnable() {
            @Override
            public void run() {
                if (LOGD) Log.d(TAG+".checkAccessWithWizard", "About to instantiate AppKeyChecker");
                AppKeyChecker akChecker = new AppKeyChecker(finalActivity, finalAppID, finalAnalyticsEnabled);
                if (LOGD) Log.d(TAG+".checkAccessWithWizard", "About to call checkAccess");
                akChecker.checkAccess(new AppKeyCallback(akChecker)); 
            }
        };
        if (LOGD) Log.d(TAG+".checkAccessWithWizard", "Running runnable to call SDK on UI thread");
        activity.runOnUiThread(runChecker);
    }

    class AppKeyCallback implements AppKeyCheckerCallback {
        private AppKeyChecker mAppKeyChecker;
        
        public AppKeyCallback(AppKeyChecker appKeyChecker) {
            mAppKeyChecker=appKeyChecker;
        }

        @Override
        public void allow() { 
            if (LOGD) Log.d("AppKeyPlugin.AppKeyCallback", "allow() Called");
            UnityPlayer.UnitySendMessage("AppKeyManager", "allow", "");
        }
    
        @Override
        public void dontAllow(int reason) { 
            String strReason=""; 
            switch (reason) {
                case (AppKeyCheckerCallback.REASON_APPKEY_NOT_INSTALLED):
                    strReason="NOT_INSTALLED";
                    break;
                case (AppKeyCheckerCallback.REASON_APPKEY_NOT_RUNNING):
                    strReason="NOT_RUNNING";
                    break;
                case (AppKeyCheckerCallback.REASON_APPKEY_INACTIVE):
                    strReason="INACTIVE";
                    break;
            }
            if (LOGD) Log.d("AppKeyPlugin.AppKeyCallback", "dontAllow() Called with reason="+strReason+", mWizard="+mPromptUser);
            UnityPlayer.UnitySendMessage("AppKeyManager", "dontAllow", strReason);

            if (mPromptUser) {
                final int finalReason=reason;
                Runnable runWizard=new Runnable() {
                    @Override
                    public void run() {
                        if (LOGD) Log.d(TAG+".checkAccessWithWizard", "AppKeyCallback about to call AppKeyWizard");
                        mAppKeyChecker.promptUser(mActivity, finalReason, mPremiumContentDescription);
                    }
                };
                if (LOGD) Log.d(TAG+".checkAccessWithWizard", "AppKeyCallback about to prompt user on UI thread");
                mActivity.runOnUiThread(runWizard);
            }
        }
    }
}
