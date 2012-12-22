package com.appkey.plugin;

import com.appkey.sdk.AppKeyCheckerCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log; 

/*
 * AppKeyWizard class
 * 
 * A generic class to help speed integration with AppKey.  The AppKey model rewards developers
 * for creating awareness of AppKey and providing the incentive to install it.  The incentive
 * is typically premium features generally available through a paid version of the application
 * or in-app purchase.
 * 
 * This class is optional.  The interprocess communication is performed by the SDK code
 * contained within the AppKeySDK JAR file.  This class simply provides an example of how
 * to message the user for the various states returned by the SDK.  You may use this class
 * as is, modified, or not at all and still participate in AppKey.  The best implementation
 * would be your own UI that is consistent with your application.
 */
public class AppKeyWizard {
    private Activity mActivity; 
    private String TAG="AppKeyWizard";
    private boolean LOGD=true;

    /**
     * Pop up an AlertDialog informing the user about AppKey, and guiding them through the installation process.
     * @param activity An activity
     * @param reason AppKeyCheckerCallback reason code
     * @param premiumContentDescription Text to describe the premium features AppKey users will receive in the calling App.  Will default to [PREMIUM FEATURES] if empty.
     * @param premiumAppUri Uri of a premium version of this app in your favorite appstore.  Use null if there is no premium version.
     */
    public AppKeyWizard(Activity activity, int reason, String premiumContentDescription, String premiumAppUrl) {
        if (LOGD) Log.d(TAG+".constructor", "Called with reason="+reason+", premiumContentDescription="+premiumContentDescription+", premiumAppUrl="+premiumAppUrl);
        mActivity=activity;
        if (premiumContentDescription==null) premiumContentDescription="";
        if (premiumContentDescription=="") {
            premiumContentDescription="[PREMIUM FEATURES]";
        }
        Uri premiumAppUri=null;
        if ((premiumAppUrl!=null)&&(premiumAppUrl.length()>0)) premiumAppUri=Uri.parse(premiumAppUrl);
        switch (reason) {
            case (AppKeyCheckerCallback.REASON_APPKEY_NOT_INSTALLED):
                Intent intentInstallAppKey = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.appkey.widget"));
                Intent intentInstallPremiumApp = null;
                if (premiumAppUri!=null) {
                    if (LOGD) Log.d(TAG+".constructor","premiumAppUri!=null");
                    intentInstallPremiumApp = new Intent(Intent.ACTION_VIEW, premiumAppUri);
                    promptUser(
                            AppKeyWizardText.notInstalled_Title,
                            String.format(AppKeyWizardText.notInstalled_PremiumApp_Message, premiumContentDescription), 
                            AppKeyWizardText.notInstalled_PremiumApp_PositiveButton, intentInstallAppKey, 
                            AppKeyWizardText.notInstalled_NegativeButton, 
                            AppKeyWizardText.notInstalled_PremiumApp_UpgradeButton, intentInstallPremiumApp);
                } else {
                    if (LOGD) Log.d(TAG+".constructor","premiumAppUri==null");
                    promptUser(
                            AppKeyWizardText.notInstalled_Title,
                            String.format(AppKeyWizardText.notInstalled_Message, premiumContentDescription), 
                            AppKeyWizardText.notInstalled_PositiveButton, intentInstallAppKey, 
                            AppKeyWizardText.notInstalled_NegativeButton, 
                            AppKeyWizardText.notInstalled_PremiumApp_UpgradeButton, intentInstallPremiumApp);
                }
                break;
            case (AppKeyCheckerCallback.REASON_APPKEY_NOT_RUNNING): 
                /*
                 * instr_redirect is a server controlled redirect to various YouTube videos that instruct the user to install
                 * the AppKey widget on their phone.  The procedure varies widely due to android fragmentation, so this approach
                 * enables us to tailor the instructions to new phones as we learn of them.
                 */
                String instructions="http://m.appkey.com/instr_redirect?model="+Build.MODEL+"&release="+Build.VERSION.RELEASE+"&oem="+Build.MANUFACTURER;
                Intent intentInstructions=new Intent(Intent.ACTION_VIEW,Uri.parse(instructions));

                promptUser(
                        AppKeyWizardText.notInstalled_Title, 
                        String.format(AppKeyWizardText.notRunning_Message, premiumContentDescription),
                        AppKeyWizardText.notRunning_PositiveButton, intentInstructions, 
                        AppKeyWizardText.notRunning_NegativeButton,
                        null,null);
               break;
            case (AppKeyCheckerCallback.REASON_APPKEY_INACTIVE):
                Intent intentHome=new Intent(Intent.ACTION_MAIN);
                intentHome.addCategory(Intent.CATEGORY_HOME);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                promptUser(
                        AppKeyWizardText.timeout_Title, 
                        String.format(AppKeyWizardText.timeout_Message, premiumContentDescription), 
                        AppKeyWizardText.timeout_PositiveButton, intentHome, 
                        AppKeyWizardText.timeout_NegativeButton,
                        null,null);
                break; 
        }
    }
    
    private void promptUser(String title, String message, String positiveButton, final Intent positiveIntent, String negativeButton, String upgradeButton, final Intent premiumAppIntent) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle(title);
        //TODO: Add the Icon once Android allows resources to be included in JAR libraries - dialog.setIcon(R.drawable.appkey_squarekey_green);
        dialog.setMessage(message);
        dialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mActivity.startActivity(positiveIntent);
                } catch (ActivityNotFoundException anfe) {
                    Log.e("AppKey promptUser","startActivity failed for Intent: "+positiveIntent.toString());
                }
            }
        });
        dialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Method intentionally left blank.  This button is solely intended to dismiss the dialog.
            }
        });
        if (premiumAppIntent!=null) {
            dialog.setNeutralButton(upgradeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        mActivity.startActivity(premiumAppIntent);
                    } catch (ActivityNotFoundException anfe) {
                        Log.e("AppKey promptUser","startActivity failed for Intent: "+premiumAppIntent.toString());
                    }
                }
            });
        }
        dialog.show();
    }

}
