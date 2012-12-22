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
    private boolean mWizard;
    private String mPremiumContentDescription;
    private String mPremiumAppURL;

    @SuppressWarnings("unused")
    private static AppKeyPlugin INSTANCE() {
        if (mINSTANCE == null) {
            mINSTANCE=new AppKeyPlugin();
        }
        return mINSTANCE;
    }
    
    public void checkAccess(Activity activity, String AppID) {
        if (LOGD) Log.d(TAG+".checkAccess", "Called with AppID="+AppID);
        checkAccessWithWizard(activity,AppID,"","");
    }
    
    public void checkAccessWithWizard(Activity activity, String AppID, String premiumContentDescription, String premiumAppURL) {
        if (LOGD) Log.d(TAG+".checkAccessWithWizard", "Called with AppID="+AppID+", premiumContentDescription="+premiumContentDescription+", premiumAppURL="+premiumAppURL);
        if (premiumContentDescription==null) premiumContentDescription="";
        if (premiumAppURL==null) premiumAppURL="";
        
        if (premiumContentDescription=="") {
            mWizard=false;
        } else {
            mWizard=true;
            mActivity=activity;
            mPremiumContentDescription=premiumContentDescription;
            mPremiumAppURL=premiumAppURL;
        }
        
        //The checker currently uses AsyncTask and needs to be run on the UI thread
        final Activity finalActivity = activity;
        final String finalAppID = AppID;
        Runnable runChecker=new Runnable() {
            @Override
            public void run() {
                if (LOGD) Log.d(TAG+".checkAccessWithWizard", "About to instantiate AppKeyChecker");
                AppKeyChecker akChecker = new AppKeyChecker(finalActivity, finalAppID);
                if (LOGD) Log.d(TAG+".checkAccessWithWizard", "About to call checkAccess");
                akChecker.checkAccess(new AppKeyCallback()); 
            }
        };
        if (LOGD) Log.d(TAG+".checkAccessWithWizard", "Running runnable to call SDK on UI thread");
        activity.runOnUiThread(runChecker);
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
            if (LOGD) Log.d("AppKeyPlugin.AppKeyCallback", "dontAllow() Called with reason="+strReason+", mWizard="+mWizard);
            UnityPlayer.UnitySendMessage("AppKeyManager", "dontAllow", strReason);

            if (mWizard) {
                final int finalReason=reason;
                Runnable runWizard=new Runnable() {
                    @Override
                    public void run() {
                        if (LOGD) Log.d(TAG+".checkAccessWithWizard", "AppKeyCallback about to call AppKeyWizard");
                        new AppKeyWizard(mActivity, finalReason, mPremiumContentDescription, mPremiumAppURL);
                    }
                };
                if (LOGD) Log.d(TAG+".checkAccessWithWizard", "AppKeyCallback about to run Wizard on UI thread");
                mActivity.runOnUiThread(runWizard);
            }
        }
    }
}
