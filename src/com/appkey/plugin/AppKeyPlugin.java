package com.appkey.plugin;

import com.appkey.sdk.AppKeyChecker;
import com.appkey.sdk.AppKeyCheckerCallback;

import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.util.Log;

/**
 * AppKey plugin for Unity apps. Bridges the gap between the Unity-side AppKeyManager and the Java-based AppKey SDK.
 * @author jimvitek
 */
public class AppKeyPlugin {

    private String TAG="AppKeyPlugin";
    private boolean LOGD=false;
    private static AppKeyPlugin mINSTANCE;
    private Activity mActivity;
    private String mAppId="";
    private boolean mAnalyticsEnabled;
    private AppKeyChecker mAppKeyChecker=null;

    @SuppressWarnings("unused")
    private static AppKeyPlugin INSTANCE() {
        if (mINSTANCE == null) {
            mINSTANCE=new AppKeyPlugin(); 
        }
        return mINSTANCE;
    }
    
    /**
     * Initializes the plugin. Call this before calling any other functions on the plugin.
     * @param activity an Activity
     * @param appId The AppKey.com assigned app Id for your app
     * @param analyticsEnabled Whether or not to track analytics for this app. Enables conversion funnel tracking.
     */
    public void init(Activity activity, String appId, boolean analyticsEnabled) {
        mActivity=activity;
        mAppId = appId;
        mAnalyticsEnabled = analyticsEnabled;
        mAppKeyChecker = new AppKeyChecker(activity, appId, analyticsEnabled);
    }
    
    /**
     * Check if user has AppKey fully enabled or not. Result will be communicated back to AppKeyManager.
     */
    public void checkAccess() {
        if (LOGD) Log.d(TAG+".checkAccess", "Called with AppID="+mAppId+", analyticsEnabled="+mAnalyticsEnabled);

        mAppKeyChecker.checkAccess(new AppKeyCallback()); 
    }

    class AppKeyCallback implements AppKeyCheckerCallback {

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
            if (LOGD) Log.d("AppKeyPlugin.AppKeyCallback", "dontAllow() Called with reason="+strReason);
            UnityPlayer.UnitySendMessage("AppKeyManager", "dontAllow", strReason);
        }
    }
    
    /**
     * Prompt user to install and/or active AppKey. Actual prompt varies depending on device state.
     * @param unlockedContentDescription
     */
    public void promptUser(String unlockedContentDescription) {
        if (LOGD) Log.d(TAG+".promptUser", "Called with unlockedContentDescription="+unlockedContentDescription);

        mAppKeyChecker.promptUser(mActivity, unlockedContentDescription); 
    }

    /**
     * Open AppKey application for monetization & cross-promotional purposes.
     */
    public void openAppKey() {
        if (LOGD) Log.d(TAG+".openAppKey", "Called");

        mAppKeyChecker.openAppKey(); 
    }
}
