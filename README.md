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

For Ti.Android.Admob [9.1.0](https://github.com/deckameron/Ti.Android.Admob/blob/master/android/dist/ti.android.admob-android-9.1.0.zip)
- [x] Titanium SDK 10.0.0+


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

# STANDARD BANNER VIEWS
### Supported AdView Sizes

|Types                |Description                          |
|----------------|-------------------------------|
|_BANNER_			|Mobile Marketing Association (MMA) banner ad size (320x50 density-independent pixels).                      
|_LARGE_BANNER_    	|Large banner ad size (320x100 density-independent pixels).              
|_~~SMART_BANNER~~_    | DEPRECATED - A dynamically sized banner that is full-width and auto-height.
|_MEDIUM_RECTANGLE_         |Interactive Advertising Bureau (IAB) medium rectangle ad size (300x250 density-independent pixels).
|_FULLBANNER_         |Interactive Advertising Bureau (IAB) full banner ad size (468x60 density-independent pixels).
|_LEADERBOARD_         |Interactive Advertising Bureau (IAB) leaderboard ad size (728x90 density-independent pixels).

```javascript
var adView = Admob.createBanner({
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

adView.addEventListener(Admob.AD_RECEIVED, function(e) {
	Titanium.API.info("Ad received");
});

adView.addEventListener(Admob.AD_NOT_RECEIVED, function(e) {
	Titanium.API.info("Ad failed");
});
```

# INTERSTITIAL AD
```javascript
var interstitialAd = Admob.createInterstitial({
    adUnitId : 'ca-app-pub-3940256099942544/1033173712', //USE YOUR AD_UNIT ID HERE
});

interstitialAd.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.warn("Interstital Ad Received");
    interstitialAd.show();
});

interstitialAd.addEventListener(Admob.AD_NOT_RECEIVED, function(e) {
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
var rewardedInterstitial = Admob.createRewardedInterstitial({
    adUnitId: 'ca-app-pub-3940256099942544/5224354917', //USE YOUR AD_UNIT ID HERE
});

rewardedInterstitial.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.info("Rewarded Ad AD_LOADED");
    rewardedInterstitial.show();
});

rewardedInterstitial.addEventListener(Admob.AD_REWARDED, function(e) {
    Titanium.API.info("Rewarded Ad AD_REWARDED");
    Titanium.API.info("Yay! You can give the user his reward now!");
    Titanium.API.info(JSON.stringify(e));
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
var rewarded = Admob.createRewarded({
    adUnitId: 'ca-app-pub-3940256099942544/5224354917', //USE YOUR AD_UNIT ID HERE
});

rewarded.addEventListener(Admob.AD_LOADED, function(e) {
    Titanium.API.info("Rewarded Ad AD_LOADED");
    rewarded.show();
});

rewarded.addEventListener(Admob.AD_REWARDED, function(e) {
    Titanium.API.info("Rewarded Ad AD_REWARDED");
    Titanium.API.info("Yay! You can give the user his reward now!");
    Titanium.API.info(JSON.stringify(e));
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
var masterView = Titanium.UI.createView({
    width : Titanium.UI.FILL,
    height : Titanium.UI.SIZE,
    layout : "vertical"
});

var topView = Titanium.UI.createView({
    top : 0,
    left : 0,
    right : 0,
    height : Titanium.UI.SIZE,
    layout : "horizontal"
});
masterView.add(topView);

var contentad_logo = Titanium.UI.createImageView({
    elevation : 12,
    height : 50
});
topView.add(contentad_logo);

var contentad_advertiser = Titanium.UI.createLabel({
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

var mediaView = Admob.createNativeAd({
    viewType : Admob.TYPE_MEDIA,
    top : 0,
    left : 0,
    right : 0,
    height : 250
});
masterView.add(mediaView);

var contentad_headline = Titanium.UI.createLabel({
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

var contentad_body = Titanium.UI.createLabel({
    color : "#575757",
    left : 16,
    right : 16,
    textAlign : Titanium.UI.TEXT_ALIGNMENT_LEFT,
    font : {
        fontSize : 16
    }
});
masterView.add(contentad_body);

var contentad_call_to_action = Titanium.UI.createButton({
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

var ratingView = Admob.createNativeAd({
    viewType : Admob.TYPE_STARS,
    left : 0,
    right : 0
});
masterView.add(ratingView);

var contentad_store_view = Titanium.UI.createLabel({
    color : "#D50000",
    top : 8,
    font : {
        fontSize : 16
    }
});
masterView.add(contentad_store_view);

var contentad_price_view = Titanium.UI.createLabel({
    color : "#575757",
    height : Titanium.UI.SIZE,
    width : Titanium.UI.SIZE,
    font : {
        fontSize : 12
    }
});
masterView.add(contentad_advertiser);

var nativeAd = Admob.createNativeAd({
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
# Events

|Events                |Description                          |
|----------------|-------------------------------|
|_AD_RECEIVED_				|   Ad have been successfully received
|_AD_NOT_RECEIVED_    				| 	A error occurred and the ads failed
|_AD_DESTROYED_   | 	Ad had been successfully destroyed and wiped out of memory
|_AD_OPENED_                |   **(BANNER)** Called when an ad opens an overlay that covers the screen. (click)
|_AD_CLICKED_                |   **(BANNER or INTERSTITIAL)** Called when an ad click is validated.
|_AD_LOADED_ 				| 	**(INTERSTITIAL and REWARDED)** Ad is loaded and ready to be displayed
|_AD_CLOSED_	|  	**(REWARDED or INTERSTITIAL)** Ad had been successfully closed  
|_AD_REWARDED_    	|	**(REWARDED)** When the video ended successfully and you can reward you user with his prize
|_AD_FAILED_TO_SHOW_ | 	**(REWARDED or INTERSTITIAL)** When the ad fails to be displayed
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


### `resetConsentForm()`

Shows a consent modal form. Arguments:

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

Admob.requestConsentForm();
```

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
