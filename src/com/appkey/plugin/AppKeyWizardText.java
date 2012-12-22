package com.appkey.plugin;

/**
 * This class stores english string resources for the AppKeyWizard when distributed in a JAR file.  Unfortunately there is
 * no way to distribute the wonderful resource approach in a JAR file, so this was created as a way to hardcode them
 * as nicely as possible.
 * @author jimvitek
 */
public class AppKeyWizardText {
    public static String notInstalled_Title="Upgrade for free! Just get AppKey";
    public static String notInstalled_Message="AppKey will unlock %s totally for free! Plus, by having AppKey you’ll get great stuff on other apps as well. Learn more now. It only takes a minute.";
    public static String notInstalled_PositiveButton="Learn about AppKey";
    public static String notInstalled_NegativeButton="Close";
    
    public static String notInstalled_PremiumApp_UpgradeButton="Buy Now";
    public static String notInstalled_PremiumApp_Message="Upgrade to get %s. Buy now, or install the AppKey widget on your homescreen, and get it totally free! Learn more now. It only takes a minute.";
    public static String notInstalled_PremiumApp_PositiveButton="Go to AppKey in Google Play";

    public static String notRunning_Title="You are almost there";
    public static String notRunning_Message="It appears you have already installed AppKey. To get the %s, you need to put the AppKey widget on your home screen.  Please go do that now, or touch 'instructions' for a helpful how-to video.";
    public static String notRunning_PositiveButton="Show me instructions";
    public static String notRunning_NegativeButton="Close";
    
    public static String timeout_Title="AppKey needs a poke";
    public static String timeout_Message="Occasionally, if you don’t give him enough attention, your AppKey buddy can get bored and fall asleep. Go to your home screen and poke the red key to turn it green and wake him up to unlock %s.";
    public static String timeout_PositiveButton="Home screen";
    public static String timeout_NegativeButton="Close";
}
