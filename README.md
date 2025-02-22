# Ti.Android.Admob ![](http://gitt.io/badge.svg)
Allows for the display of AdMob in Titanium Android applications.

Please note that if your androidManifest has screen support set to: **android:anyDensity="false"**, any banner ads will
display too small on high density devices.
It is not clear at this point if this is a bug with AdMob or Titanium.
In any event, you will either need to NOT set your screen support -- or set android:anyDensity="true" and adjust your app layout accordingly

## Getting Started

View the [Using Titanium Modules](http://docs.appcelerator.com/titanium/latest/#!/guide/Using_Titanium_Modules) document
for instructions on getting started with using this module in your application.

## Requirements

For Ti.Android.Admob [11.0.1](https://github.com/deckameron/Ti.Android.Admob/raw/master/android/dist/ti.android.admob-android-11.0.1.zip)
- [x] Titanium SDK 12.5.0+


## Doubleclick for Publishers Developer Docs

<https://developers.google.com/mobile-ads-sdk/>

All AdViews, except Rewarded and RewardedInterstitial, have the parameters **_keyword_** and **_contentUrl_** and can be used with DFP mapping

## Download
You can get it [here](https://github.com/deckameron/Ti.Android.Admob/blob/master/android/dist/)

## How to use it

First you need add this meta-data to your tiapp.xml
```xml
<android>
    <manifest>
        <application
            android:allowBackup="true"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/AppTheme">

            <!-- ========================== -->
            <!-- THIS IS THE IMPORTANT PART -->
            <meta-data
                android:name="com.google.android.gms.ads.APPLICATION_ID"
                android:value="YOUR-APP-ID"/>
            <!-- THIS IS THE IMPORTANT PART -->
            <!-- ========================== -->

            <activity android:name=".MainActivity">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />
                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>
        </application>
    </manifest>
</android>
```

You need to require the module
```javascript
var Admob = require("ti.android.admob");
```

# SETTING A TEST DEVICE (VERY IMPORTANT)
```javascript
// Get your device id from the logs after you compile the project with the module for the fisrt time.
Admob.setTestDeviceId("AC65D99D31C5DA727B986DC35D45C091");
```

# BANNER
### Supported AdView Sizes

|Types                |Description                          |
|----------------|-------------------------------|
|_BANNER_           |Mobile Marketing Association (MMA) banner ad size (320x50 density-independent pixels).                      
|_LARGE_BANNER_     |Large banner ad size (320x100 density-independent pixels).              
|_~~SMART_BANNER~~_    | DEPRECATED - A dynamically sized banner that is full-width and auto-height.
|_MEDIUM_RECTANGLE_         |Interactive Advertising Bureau (IAB) medium rectangle ad size (300x250 density-independent pixels).
|_FULLBANNER_         |Interactive Advertising Bureau (IAB) full banner ad size (468x60 density-independent pixels).
|_LEADERBOARD_         |Interactive Advertising Bureau (IAB) leaderboard ad size (728x90 density-independent pixels).

```javascript
let adView = Admob.createBanner({
    bottom : 0,

    // You can usethe supported adView sizes:
    adSize: Admob.BANNER,
    // OR a custom size, like this:
    // customAdSize: {
    //     height: 50,
    //     width: 300
    // },

    adUnitId: 'ca-app-pub-3940256099942544/6300978111', //USE YOUR AD_UNIT ID HERE

    // DFP mapping
    //keyword : "titanium",
    //contentUrl : "www.myur.com",
});
window.add(adView);

adView.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.info("Ad loaded");
});

adView.addEventListener(Admob.AD_FAILED_TO_LOAD, function(e) {
    Titanium.API.info("Ad failed to load");
});
```

# ADAPTIVE BANNER
```javascript
let adView = Admob.createAdaptiveBanner({  
    adUnitId: 'ca-app-pub-3940256099942544/6300978111', //USE YOUR AD_UNIT
    
    // DFP mapping
    //keyword : "titanium",
    //contentUrl : "www.myur.com",
    
    // Collapsible
    collapsible: Admob.COLLAPSIBLE_BOTTOM, // or Admob.COLLAPSIBLE_TOP

    // Adaptive Type
    adaptiveType: Admob.ADAPTIVE_INLINE, // or Admob.ADAPTIVE_ANCHORED
    maxHeight: 250 // ONLY IF adaptiveType is Admob.ADAPTIVE_INLINE, maxHeight must be set. Default value is 50
});  
window.add(adView);

adView.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.info("Ad loaded");
});

adView.addEventListener(Admob.AD_FAILED_TO_LOAD, function(e) {
    Titanium.API.info("Ad failed to load");
});
```

# APP OPEN AD BANNER
```javascript
/** Key Points **/  
  
// App open ads will time out after four hours. Ads rendered more than four hours after request time will no  
// longer be valid and may not earn revenue.  
  
/** Best Practices **/  
  
// Show your first app open ad after your users have used your app a few times.  
  
// Show app open ads during times when your users would otherwise be waiting for your app to load.  
  
// If you have a loading screen under the app open ad, and your loading screen completes loading before the ad  
// is dismissed, you may want to dismiss your loading screen in the onAdDismissedFullScreenContent() method.  
  
/** Warning **/  
  
// Attempting to load a new ad from the Admob.AD_FAILED_TO_LOAD event is strongly discouraged.  
// If you must load an ad from Admob.AD_FAILED_TO_LOAD, limit ad load retries to avoid continuous failed  
// ad requests in situations such as limited network connectivity.  
  
const reload_max_tries_case_error = 4;  
let reload_max_tries = 0;  
  
let appOpenAd = Admob.createAppOpenAd({  
    adUnitId: "ca-app-pub-3940256099942544/3419835294", //USE YOUR AD_UNIT
});  
  
appOpenAd.addEventListener(Admob.AD_FAILED_TO_SHOW, function (e) {
    Titanium.API.error("======================== AppOpenAd - Failed to show ads ========================");  
    Titanium.API.warn({
        "message": e.message,
        "cause": e.cause,
        "code": e.code
    });
});  
  
appOpenAd.addEventListener(Admob.AD_SHOWED_FULLSCREEN_CONTENT, function () {
    Titanium.API.info("======================== AppOpenAd - showed ads successfully ========================");
});  
  
appOpenAd.addEventListener(Admob.AD_FAILED_TO_LOAD, function (e) {
    Titanium.API.error("======================== AppOpenAd - failed to load ads ========================");
    Titanium.API.warn({
        "message": e.message,
        "reason": e.reason,
        "cause": e.cause,
        "code": e.code
    });

    if (reload_max_tries < reload_max_tries_case_error){
        appOpenAd.load();
    }

    reload_max_tries += 1;  
});  
  
appOpenAd.addEventListener(Admob.AD_LOADED, function (e) {
    Titanium.API.warn("======================== AppOpenAd - Ads Loaded and ready ========================");
    reload_max_tries = 0;
    Titanium.App.Properties.setDouble('appOpenAdLoadTime', (new Date().getTime()));
});  
  
appOpenAd.addEventListener(Admob.AD_CLOSED, function (e) {
    Titanium.API.warn("======================== AppOpenAd ad - CLOSED ========================");
    Titanium.App.Properties.setDouble('lastTimeAppOpenAdWasShown', (new Date().getTime()));
    appOpenAd.load();  
});  
  
appOpenAd.addEventListener(Admob.AD_NOT_READY, function (e) {
    Titanium.API.warn("======================== AppOpenAd ad - AD_NOT_READY ========================");
    Titanium.API.warn(e.message);  
});  
  
Titanium.App.addEventListener('resume', function () {
    let currentTime = (new Date().getTime());
    let loadTime = Titanium.App.Properties.getDouble('appOpenAdLoadTime', currentTime);
    let lastTimeAppOpenAdWasShown = Titanium.App.Properties.getDouble('lastTimeAppOpenAdWasShown', 1);
    
    if ((currentTime - loadTime) < 14400000) { // then less than 4 hours elapsed.
        if ((currentTime - lastTimeAppOpenAdWasShown) > 600000){ // then more than 10 minutes elapsed after the last Ad showed.
            appOpenAd.show();
        } else {
            Titanium.API.warn("You have showned an AppOpenAd less than 10 minutes ago. You should wait!");
        }
    } else {
        Titanium.API.warn("The AppOpenAd was requested more than 4 hours ago and has expired! You should load another one.");
    }
});
```

# INTERSTITIAL
```javascript
let interstitialAd = Admob.createInterstitial({
    adUnitId : 'ca-app-pub-3940256099942544/1033173712', //USE YOUR AD_UNIT ID HERE
});

interstitialAd.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.warn("Interstital Ad Received");
    interstitialAd.show();
});

interstitialAd.addEventListener(Admob.AD_FAILED_TO_LOAD, function(e) {
    Titanium.API.error("Interstital Ad failed");
    console.log(JSON.stringify(e));
});

interstitialAd.addEventListener(Admob.AD_CLOSED, function(e) {
    Titanium.API.warn("Interstital ad close successfully. RIP!");
    interstitialAd.load();
});
```

# REWARDED INTERSTITIAL
```javascript
let rewardedInterstitial = Admob.createRewardedInterstitial({
    adUnitId: 'ca-app-pub-3940256099942544/5224354917', //USE YOUR AD_UNIT ID HERE
});

rewardedInterstitial.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.info("Rewarded Ad AD_LOADED");
    rewardedInterstitial.show();
});

rewardedInterstitial.addEventListener(Admob.AD_REWARDED, function(e) {
    Titanium.API.info("Rewarded Ad AD_REWARDED");
    Titanium.API.info("Yay! You can give the user his reward now!");
    Titanium.API.info({
        "amount": e.amount,
        "type": e.type
    });
    rewardedInterstitial.load();
});

rewardedInterstitial.addEventListener(Admob.AD_OPENED, function(e) {
    Titanium.API.info("Rewarded Ad AD_OPENED");
});

rewardedInterstitial.addEventListener(Admob.AD_FAILED_TO_SHOW, function(e) {
    Titanium.API.info("Rewarded Ad AD_FAILED_TO_SHOW");
});

rewardedInterstitial.addEventListener(Admob.AD_CLOSED, function(e) {
    Titanium.API.info("Rewarded Ad AD_CLOSED");
});
```

# REWARDED
```javascript
let rewarded = Admob.createRewarded({
    adUnitId: 'ca-app-pub-3940256099942544/5224354917', //USE YOUR AD_UNIT ID HERE
});

rewarded.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.info("Rewarded Ad AD_LOADED");
    rewarded.show();
});

rewarded.addEventListener(Admob.AD_REWARDED, function(e) {
    Titanium.API.info("Rewarded Ad AD_REWARDED");
    Titanium.API.info("Yay! You can give the user his reward now!");
    Titanium.API.info({
        "amount": e.amount,
        "type": e.type
    });
    rewarded.load();
});

rewarded.addEventListener(Admob.AD_OPENED, function(e) {
    Titanium.API.info("Rewarded Ad AD_OPENED");
});

rewarded.addEventListener(Admob.AD_FAILED_TO_SHOW, function(e) {
    Titanium.API.info("Rewarded Ad AD_FAILED_TO_SHOW");
});

rewarded.addEventListener(Admob.AD_CLOSED, function(e) {
    Titanium.API.info("Rewarded Ad AD_CLOSED");
});
```

# NATIVE ADS
```javascript
let masterView = Titanium.UI.createView({
    width : Titanium.UI.FILL,
    height : Titanium.UI.SIZE,
    layout : "vertical"
});

let topView = Titanium.UI.createView({
    top : 0,
    left : 0,
    right : 0,
    height : Titanium.UI.SIZE,
    layout : "horizontal"
});
masterView.add(topView);

let contentad_logo = Titanium.UI.createImageView({
    elevation : 12,
    height : 50
});
topView.add(contentad_logo);

let contentad_advertiser = Titanium.UI.createLabel({
    color : "#575757",
    left : 16,
    textAlign : Titanium.UI.TEXT_ALIGNMENT_LEFT,
    verticalAlign : Titanium.UI.TEXT_VERTICAL_ALIGNMENT_CENTER,
    height : 35,
    font : {
        fontSize : 18,
        fontWeight : "bold"
    }
});
topView.add(contentad_advertiser);

let mediaView = Admob.createNativeAd({
    viewType : Admob.TYPE_MEDIA,
    top : 0,
    left : 0,
    right : 0,
    height : 250
});
masterView.add(mediaView);

let contentad_headline = Titanium.UI.createLabel({
    top : 16,
    maxLines : 2,
    color : "#000000",
    left : 16,
    right : 16,
    textAlign : Titanium.UI.TEXT_ALIGNMENT_LEFT,
    font : {
        fontSize : 20,
        fontWeight : "bold"
    }
});
masterView.add(contentad_headline);

let contentad_body = Titanium.UI.createLabel({
    color : "#575757",
    left : 16,
    right : 16,
    textAlign : Titanium.UI.TEXT_ALIGNMENT_LEFT,
    font : {
        fontSize : 16
    }
});
masterView.add(contentad_body);

let contentad_call_to_action = Titanium.UI.createButton({
    top : 16,
    elevation : 8,
    right : 16,
    width : Titanium.UI.SIZE,
    height : 35,
    backgroundColor : "#ff5722",
    font : {
        fontSize : 14,
        fontWeight : "bold"
    }
});
masterView.add(contentad_call_to_action);

let ratingView = Admob.createNativeAd({
    viewType : Admob.TYPE_STARS,
    left : 0,
    right : 0
});
masterView.add(ratingView);

let contentad_store_view = Titanium.UI.createLabel({
    color : "#D50000",
    top : 8,
    font : {
        fontSize : 16
    }
});
masterView.add(contentad_store_view);

let contentad_price_view = Titanium.UI.createLabel({
    color : "#575757",
    height : Titanium.UI.SIZE,
    width : Titanium.UI.SIZE,
    font : {
        fontSize : 12
    }
});
masterView.add(contentad_advertiser);

let nativeAd = Admob.createNativeAd({
    //Standard Widgets
    masterView : masterView,
    headlineLabel : contentad_headline,
    bodyLabel : contentad_body,
    callToActionButton : contentad_call_to_action,
    logoOrIconImageView : contentad_logo,
    advertiserLabel : contentad_advertiser,
    mediaView : mediaView,

    //Store Widgets
    storeLabel : contentad_store_view,
    starsView : ratingView,
    priceLabel : contentad_price_view,

    top : 16,
    left : 16,
    right : 16,
    height : Titanium.UI.SIZE,
    backgroundColor : "#FFFFFF",

    viewType : Admob.TYPE_ADS,
    adSizeType: Admob.NATIVE_ADS,
    adUnitId : "ca-app-pub-3940256099942544/2247696110",
});
window.add(nativeAd);
```
# Ads Video Volume

You call this method do drop the volume to a percentage of the current volume.
```
Admob.setAppVolume("50%"); // Will set the volume to 50% of the current volume.
```
# Events

|Events                |Description                          |
|----------------|-------------------------------|
|_~~AD_RECEIVED~~_ (**DEPRECATED**)             |   Replaced by AD_LOADED
|_AD_LOADED_                |   Ad is successfully loaded and ready to be displayed
|_~~AD_NOT_RECEIVED~~_ (**DEPRECATED**)         |   Replaced by AD_FAILED_TO_LOAD
|_AD_FAILED_TO_LOAD_                    |   A error occurred and the ads failed
|_AD_DESTROYED_   |     Ad had been successfully destroyed and wiped out of memory
|_AD_OPENED_                |   **(BANNER)** Called when an ad opens an overlay that covers the screen. (click)
|_AD_CLICKED_                |   **(BANNER or INTERSTITIAL)** Called when an ad click is validated.
|_AD_CLOSED_    |   **(REWARDED or INTERSTITIAL)** Ad had been successfully closed  
|_AD_REWARDED_      |   **(REWARDED)** When the video ended successfully and you can reward you user with his prize
|_AD_FAILED_TO_SHOW_ |  **(REWARDED or INTERSTITIAL)** When the ad fails to be displayed
|_AD_SHOWED_FULLSCREEN_CONTENT_              |   Called when the ad showed the full screen content


# Obtaining Consent with the User Messaging Platform

## Prerequisites

You must have a [Funding Choices](https://support.google.com/fundingchoices/answer/9180084) account linked to your AdMob account.

To create a Funding Choices account, go to Privacy & messaging in the AdMob UI and select Go to Funding Choices. The Funding Choices account is then created automatically in the background.

For more information, see [How IAB requirements affect EU consent messages](https://support.google.com/fundingchoices/answer/10207733).

## Introduction

Under the Google [EU User Consent Policy](https://www.google.com/about/company/user-consent-policy/), you must make certain disclosures to your users in the European Economic Area (EEA) along with the UK and obtain their consent to use cookies or other local storage, where legally required, and to use personal data (such as AdID) to serve ads. This policy reflects the requirements of the EU ePrivacy Directive and the General Data Protection Regulation (GDPR).

To support publishers in meeting their duties under this policy, Google offers the User Messaging Platform (UMP) SDK, which replaces the previous open source [Consent SDK](https://github.com/googleads/googleads-consent-sdk-android). The UMP SDK has been updated to support the latest IAB standards. We've also simplified the process of setting up consent forms and listing ad providers. All of these configurations can now conveniently be handled in the [Funding Choices UI](https://support.google.com/fundingchoices/answer/9180084).

It is a best practice to load a form every time the user launches your app, even if you determine consent is not required, so that the form is ready to display in case the user wishes to change their consent setting.

This guide walks you through how to install the SDK, implement the IAB solutions, and enable testing features.

### `requestConsentForm()`

Request the latest consent information

It is recommended that you request an update of the consent information at every app launch. This will determine whether or not your user needs to provide consent.

We have to reset TC string if last updated date was more than 13 months ago (https://developers.google.com/admob/ios/privacy/gdpr#troubleshooting), but Google UMP has a bug that generates 3.3 typed IAB TCF v2.0 because this string never gets automatic updated. So I have implemented [bocops workaround](https://github.com/bocops/UMP-workarounds#admob-error-33) in order to address it. Thanks to [Bocops](https://github.com/bocops) and [Astrovics](https://github.com/Astrovic)

#### Debug Geography
You can also call ```requestConsentForm(Admob.DEBUG_GEOGRAPHY_EEA);```

|Constants                |Description                          |
|----------------|-------------------------------|
|DEBUG_GEOGRAPHY_DISABLED             |   Debug geography disabled for debug devices.
|DEBUG_GEOGRAPHY_OTHER             |   Geography appears as in a region with no regulation in force for debug device.
|DEBUG_GEOGRAPHY_EEA             |   Geography appears as in EEA for debug devices.
|DEBUG_GEOGRAPHY_REGULATED_US_STATE             |   Geography appears as in a regulated US State for debug devices.



### `showConsentForm()`

Presents the consent form.

Only call this method if Admod.CONSENT_REQUIRED.


### `resetConsentForm()`

Reset consent state

In testing your app with the UMP SDK, you may find it helpful to reset the state of the SDK so that you can simulate a user's first install experience. The SDK provides the resetConsentForm() method to do this.


### How to use it:
```javascript
Admob.addEventListener(Admob.CONSENT_READY, function (){
    console.log("Admod.CONSENT_READY");
});

Admob.addEventListener(Admob.CONSENT_INFO_UPDATE_FAILURE, function (){
    console.log("Admod.CONSENT_INFO_UPDATE_FAILURE");
});

Admob.addEventListener(Admob.CONSENT_FORM_DISMISSED, function (){
    console.log("Admod.CONSENT_FORM_DISMISSED");
});

Admob.addEventListener(Admob.CONSENT_FORM_LOADED, function (){
    console.log("Admod.CONSENT_FORM_LOADED");
});

Admob.addEventListener(Admob.CONSENT_ERROR, function (e){
    console.log("Admod.CONSENT_ERROR");
    console.log(e.message);
});

Admob.addEventListener(Admob.CONSENT_REQUIRED, function (){  
    console.warn("Admod.CONSENT_REQUIRED");  
    Admob.showConsentForm();  
});
Admob.requestConsentForm();
```

## User Consent and Ad serving

**If consent is denied, or if certain values are not checked in the consent management phase, the ads will not be loaded**.

Why does this happen? If you pay attention to the **ConsentStatus.OBTAINED**  field, you will notice that it says that  **the consent is obtained, but the personalization is not defined**. As you see [here](https://itnext.io/android-admob-consent-with-ump-personalized-or-non-personalized-ads-in-eea-3592e192ec90).

It is up to us developers to check if the user has granted the  [**minimum requirements**](https://support.google.com/admob/answer/9760862?ref_topic=10303737)  to be able to view the ads, and if he has chosen to see personalized or non-personalized ones. 

In order to assist you with this, [Mirko Dimartino](https://mirko-ddd.medium.com/?source=post_page-----3592e192ec90--------------------------------) created a solution that I have implemented in this module.


### `canShowAds()`

If false (and GDPR applies, so if in EEA) you should prompt the user or to accept all, or explain in details (check above) what to check to display at least Non-Personalized Ads, or ask the user to opt for a premium version of the app, otherwise you will earn absolutely nothing.

If true you can check if user granted at least minimum requirements to show Personalized Ads with the following method.


### `canShowPersonalizedAds()`

Finally you know if you can request AdMob Personalized or Non-Personalized Ads, if **Non-Personalized** you have to forward the request using this snippet.


# Mediation Networks

If you want to use different mediation networks (https://developers.google.com/admob/android/choose-networks) you have to add the dependencies to your app build.gradle file. For example:
```
repositories {
    google()
    mavenCentral()
    maven {
        url 'https://artifact.bytedance.com/repository/pangle/'
    }
}

dependencies {
    implementation 'com.google.ads.mediation:facebook:6.13.7.1'
    implementation 'com.google.ads.mediation:inmobi:10.1.2.1'
    implementation 'com.google.ads.mediation:pangle:5.1.0.6.0'
}
```

# Google Test Ads Ids
|Events                |Description                          |
|----------------|-------------------------------|
|[Adaptative Banner](https://developers.google.com/admob/android/banner/adaptive) | ca-app-pub-3940256099942544/9214589741
|[Banner](https://developers.google.com/admob/android/banner#add_adview) | ca-app-pub-3940256099942544/6300978111
|[Interstitial](https://developers.google.com/admob/android/interstitial#create_an_interstitial_ad_object) | ca-app-pub-3940256099942544/1033173712
|[Interstitial Video](https://developers.google.com/admob/android/interstitial#create_an_interstitial_ad_object) | ca-app-pub-3940256099942544/8691691433
|[Rewarded Video](https://developers.google.com/admob/android/rewarded-video#request_rewarded_video_ad) | ca-app-pub-3940256099942544/5224354917
|[Native](https://developers.google.com/admob/android/native-advanced#build_an_adloader) | ca-app-pub-3940256099942544/2247696110
|[AppOpen](https://developers.google.com/admob/android/app-open) | ca-app-pub-3940256099942544/3419835294
