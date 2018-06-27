# Ti.Android.Admob ![](http://gitt.io/badge.svg)
Appcelerator Android module wrapping Admob functionalities

### Add this to your tiapp.xml
```xml
<property name="run-on-main-thread" type="bool">true</property>
<android xmlns:android="http://schemas.android.com/apk/res/android">
	<manifest android:installLocation="auto"
            android:versionCode="1" android:versionName="1.0">
	    
	    <!-- Important part-->
		<application>
			<meta-data android:name="com.google.android.gms.version" android:value="9683000"/>
			<activity 
			android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
			android:name="com.google.android.gms.ads.AdActivity"/>
		</application>
		<!-- Important part-->
		
	</manifest>
</android>
```

### Usage
```javascript
var admob = require("ti.android.admob");

//===================================
//TRADITIONAL ADVIEW
var adView = admob.createView({
	top: 0,
	adSizeType: 'BANNER', //RECTANGLE, FULLBANNER, LEADERBOARD, SMART, FLUID
	publisherId : "ca-app-pub-xxxxxxxxxxxxx~xxxxxxx", //USE YOUR PUBLISHER ID HERE
        testDeviceId : "G9CCEHKYF95FFR8152FX50D059DC8336", //USE YOUR DEVICE ID HERE
	adUnitId: 'ca-app-pub-xxxxxxxxxxxxx/xxxxxxx', //USE YOUR AD_UNIT ID HERE
});	
window.add(adView);	

adView.addEventListener('ad_received', function(e) {
	Titanium.API.info("Ad received");
});

adView.addEventListener('ad_not_received', function(e) {
	Titanium.API.info("Ad failed");
});

//===================================
//MULTIPLE ADSIZES
var multipleAds = admob.createView({
	top: 0,
	adSizes: [
		{width: 320, height: 100},
		{width: 320, height: 50},
		{width: 320, height: 240}
	],
	publisherId : "ca-app-pub-xxxxxxxxxxxxx~xxxxxxx", //USE YOUR PUBLISHER ID HERE
        testDeviceId : "G9CCEHKYF95FFR8152FX50D059DC8336", //USE YOUR DEVICE ID HERE
	adUnitId: 'ca-app-pub-xxxxxxxxxxxxx/xxxxxxx', //USE YOUR AD_UNIT ID HERE
});	
window.add(bannerAd);	

multipleAds.addEventListener('ad_received', function(e) {
	Titanium.API.info("Ad received");
});

multipleAds.addEventListener('ad_not_received', function(e) {
	Titanium.API.info("Ad failed");
});

//===================================
//NATIVE ADVIEW
var nativeAd = admob.createView({
	adSizeType: 'NATIVE',
	height : 132,
	width : 360,
        publisherId : "ca-app-pub-xxxxxxxxxxxxx~xxxxxxx", //USE YOUR PUBLISHER ID HERE
        testDeviceId : "G9CCEHKYF95FFR8152FX50D059DC8336", //USE YOUR DEVICE ID HERE
	adUnitId: 'ca-app-pub-xxxxxxxxxxxxx/xxxxxxx', //USE YOUR AD_UNIT ID HERE
});		
window.add(nativeAd);

nativeAd.addEventListener('ad_received', function(e) {
	Titanium.API.info("Native Ad received");
});

nativeAd.addEventListener('ad_not_received', function(e) {
	Titanium.API.info("Native Ad failed");
});

//===================================
//INTERSTITIAL ADVIEW
var interstitialAd = admob.createView({
	top: 0,
	adSizeType: 'INTERSTITIALAD',
	publisherId : "ca-app-pub-xxxxxxxxxxxxx~xxxxxxx", //USE YOUR PUBLISHER ID HERE
        testDeviceId : "G9CCEHKYF95FFR8152FX50D059DC8336", //USE YOUR DEVICE ID HERE
	adUnitId: 'ca-app-pub-xxxxxxxxxxxxx/xxxxxxx', //USE YOUR AD_UNIT ID HERE
});	
window.add(bannerAd);	

interstitialAd.addEventListener('ad_received', function(e) {
	Titanium.API.warn("Interstital Ad Received");
});

interstitialAd.addEventListener('ad_not_received', function(e) {
	Titanium.API.error("Interstital Ad failed");
});

interstitialAd.addEventListener('ad_ready_to_be_shown', function(e) {
	Titanium.API.warn("Interstital Ad is READY!");
	interstitialAd.showAdNow();
});

interstitialAd.addEventListener('ad_not_ready_yet', function(e) {
	Titanium.API.warn("Interstital Ad is not ready yet!");
});

interstitialAd.addEventListener('ad_being_shown', function(e) {
	Titanium.API.warn("Interstital Ad being shown right now!");
});

interstitialAd.addEventListener('ad_closed', function(e) {
	Titanium.API.warn("Interstital ad closed successfully. RIP!");
});
```
## DOING
- Support GDPR consent requests

## TODO
- Support Titanium Views when creating native ads
