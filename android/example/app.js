var admob = require("ti.android.admob");

//===================================
//TRADITIONAL ADVIEW
var bannerAd = admob.createView({
	top: 0,
	adSizeType: 'BANNER', //RECTANGLE, FULLBANNER, LEADERBOARD, SMART
	publisherId: 'ca-app-pub-xxxxxxxxxxxxx/xxxxxxx', //USE YOUR PUBLISHER ID HERE
	testing: true
});	
window.add(bannerAd);	

bannerAd.addEventListener('ad_received', function(e) {
	Titanium.API.info("Banner Ad received");
});

bannerAd.addEventListener('ad_not_received', function(e) {
	Titanium.API.info("Banner Ad failed");
});

//===================================
//NATIVE ADVIEW
var nativeAd = admob.createView({
	adSizeType: 'NATIVE',
	height : 132,
	width : 360,
	publisherId: 'ca-app-pub-xxxxxxxxxxxxx/xxxxxxx', //USE YOUR PUBLISHER ID HERE
	testing: false
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
	publisherId: 'ca-app-pub-xxxxxxxxxxxxx/xxxxxxx', //USE YOUR PUBLISHER ID HERE
	testing: true
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
	Titanium.API.warn("Interstital ad close successfully. RIP!");
});
