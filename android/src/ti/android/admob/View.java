package ti.android.admob;

import android.content.Context;

import ti.android.admob.AdmobModule;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

public class View extends TiUIView {
	
	private static final String TAG = "AdmobView";
    
	AdView adView;
    InterstitialAd intAd;
    NativeExpressAdView nativeAd;
    
    int prop_top;
    int prop_left;
    int prop_right;
    
    String prop_color_bg;
    String prop_color_bg_top;
    String prop_color_border;
    String prop_color_text;
    String prop_color_link;
    String prop_color_url;

    public View(TiViewProxy proxy) {
        super(proxy);
        Log.d(TAG, "Creating AdMob AdView");
        Log.d(TAG, ("AdmobModule.PUBLISHER_ID: " + AdmobModule.PUBLISHER_ID));
    }
    
    private void createAdView() {
        Log.d(TAG, "createAdView()");
        this.adView = new AdView((Context) this.proxy.getActivity());
        this.adView.setAdSize(AdSize.BANNER);
        this.adView.setAdUnitId(AdmobModule.PUBLISHER_ID);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("15F2A943429FE915B6C67AB490A81794").build();
        this.adView.setAdListener(new AdListener(){

            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                View.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, ("onAdFailedToLoad(): " + errorCode));
                View.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
            }
        });
        this.adView.loadAd(adRequest);
        this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
        this.setNativeView((android.view.View)this.adView);
    }
    
    private void createFullBannerAdView() {
        Log.d(TAG, "createAdView()");
        this.adView = new AdView((Context)this.proxy.getActivity());
        this.adView.setAdSize(AdSize.FULL_BANNER);
        this.adView.setAdUnitId(AdmobModule.PUBLISHER_ID);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("15F2A943429FE915B6C67AB490A81794").build();
        this.adView.setAdListener(new AdListener(){

            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                View.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, ("onAdFailedToLoad(): " + errorCode));
                View.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
            }
        });
        this.adView.loadAd(adRequest);
        this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
        this.setNativeView((android.view.View)this.adView);
    }

    private void createLeaderBoardAdView() {
        Log.d(TAG, "createAdView()");
        this.adView = new AdView((Context)this.proxy.getActivity());
        this.adView.setAdSize(AdSize.LEADERBOARD);
        this.adView.setAdUnitId(AdmobModule.PUBLISHER_ID);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("15F2A943429FE915B6C67AB490A81794").build();
        this.adView.setAdListener(new AdListener(){

            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                View.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, ("onAdFailedToLoad(): " + errorCode));
                View.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
            }
        });
        this.adView.loadAd(adRequest);
        this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
        this.setNativeView((android.view.View)this.adView);
    }

    private void createSmartBannerAdView() {
        Log.d(TAG, "createAdView()");
        this.adView = new AdView((Context)this.proxy.getActivity());
        this.adView.setAdSize(AdSize.SMART_BANNER);
        this.adView.setAdUnitId(AdmobModule.PUBLISHER_ID);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("15F2A943429FE915B6C67AB490A81794").build();
        this.adView.setAdListener(new AdListener(){

            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                View.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, ("onAdFailedToLoad(): " + errorCode));
                View.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
            }
        });
        this.adView.loadAd(adRequest);
        this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
        this.setNativeView((android.view.View)this.adView);
    }

    private void createRectangleBannerAdView() {
        Log.d(TAG, "createAdView()");
        this.adView = new AdView((Context)this.proxy.getActivity());
        this.adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        this.adView.setAdUnitId(AdmobModule.PUBLISHER_ID);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("15F2A943429FE915B6C67AB490A81794").build();
        this.adView.setAdListener(new AdListener(){

            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                View.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, ("onAdFailedToLoad(): " + errorCode));
                View.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
            }
        });
        this.adView.loadAd(adRequest);
        this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
        this.setNativeView((android.view.View)this.adView);
    }
    
    private void createNativeAdView() {
        Log.d(TAG, "createNativeAdView()");
        this.nativeAd = new NativeExpressAdView((Context)this.proxy.getActivity());
        this.nativeAd.setAdSize(new AdSize(AdmobModule.AD_WIDTH, AdmobModule.AD_HEIGHT));
        this.nativeAd.setAdUnitId(AdmobModule.PUBLISHER_ID);
        
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adRequestBuilder.addTestDevice("15F2A943429FE915B6C67AB490A81794");
        
        AdRequest adRequest = adRequestBuilder.build();
        this.nativeAd.setAdListener(new AdListener(){

            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                View.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, ("onAdFailedToLoad(): " + errorCode));
                View.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
            }
        });
        this.nativeAd.loadAd(adRequest);
        this.nativeAd.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
        this.setNativeView((android.view.View)this.nativeAd);
    }

    private void createInterstitialAdView() {
        Log.d(TAG, "createInterstitialAdView()");
        this.intAd = new InterstitialAd((Context)this.proxy.getActivity());
        this.intAd.setAdUnitId(AdmobModule.PUBLISHER_ID);
        this.intAd.setAdListener(new AdListener(){
        	
        	
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                View.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
                if (View.this.intAd.isLoaded()) {
                    //View.this.intAd.show();
                	View.this.proxy.fireEvent("ad_ready_to_be_shown", (Object)new KrollDict());
                	Log.d(TAG, "Interstitial are ready to be shown.");
                } else {
                    Log.d(TAG, "Interstitial ad was not ready to be shown.");
                }
            }
            
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad (%s)", View.this.getErrorReason(errorCode));
                Log.d(TAG, message);
                View.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
            }

            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                Log.d(TAG, "Ad Closed");
                View.this.proxy.fireEvent("ad_closed", (Object)new KrollDict());
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        this.intAd.loadAd(adRequest);
    }
    
	public void showAdNow() {
		this.proxy.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				if (!intAd.isLoaded()) {
					Log.e(TAG, "Invalid interstitial ads call: No loaded interstitial ads in store yet!");
					View.this.proxy.fireEvent("ad_not_ready_yet", (Object)new KrollDict());
				} else {
					View.this.proxy.fireEvent("ad_being_showed", (Object)new KrollDict());
					intAd.show();
				}
			}
		});
	}
	
	 public void destroy() {
		this.proxy.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				if(adView != null){
					Log.d(TAG, "destroy");
		        	adView.destroy();
		        	intAd = null;
		        	View.this.proxy.fireEvent("ad_destroyed", (Object)new KrollDict());
				}
				Log.d(TAG, "adview is NULL");
			}
		});
    }
   
    public void processProperties(KrollDict d) {
        super.processProperties(d);
        Log.d(TAG, "process properties");
        
        if (d.containsKey((Object)"height")) {
            Log.d(TAG, ("has height: " + d.getString("height")));
            AdmobModule.AD_HEIGHT = d.getInt("height");
        }
        if (d.containsKey((Object)"width")) {
            Log.d(TAG, ("has height: " + d.getString("width")));
            AdmobModule.AD_WIDTH = d.getInt("width");
        }
        
        if (d.containsKey((Object)"publisherId")) {
            Log.d(TAG, ("has publisherId: " + d.getString("publisherId")));
            AdmobModule.PUBLISHER_ID = d.getString("publisherId");
        }
        if (d.containsKey((Object)"testing")) {
            Log.d(TAG, ("has testing param: " + d.getBoolean("testing")));
            AdmobModule.TESTING = d.getBoolean("testing");
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_BG)) {
            Log.d(TAG, ("has PROPERTY_COLOR_BG: " + d.getString(AdmobModule.PROPERTY_COLOR_BG)));
            this.prop_color_bg = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG));
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_BG_TOP)) {
            Log.d(TAG, ("has PROPERTY_COLOR_BG_TOP: " + d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP)));
            this.prop_color_bg_top = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP));
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_BORDER)) {
            Log.d(TAG, ("has PROPERTY_COLOR_BORDER: " + d.getString(AdmobModule.PROPERTY_COLOR_BORDER)));
            this.prop_color_border = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BORDER));
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_TEXT)) {
            Log.d(TAG, ("has PROPERTY_COLOR_TEXT: " + d.getString(AdmobModule.PROPERTY_COLOR_TEXT)));
            this.prop_color_text = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_TEXT));
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_LINK)) {
            Log.d(TAG, ("has PROPERTY_COLOR_LINK: " + d.getString(AdmobModule.PROPERTY_COLOR_LINK)));
            this.prop_color_link = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_LINK));
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_URL)) {
            Log.d(TAG, ("has PROPERTY_COLOR_URL: " + d.getString(AdmobModule.PROPERTY_COLOR_URL)));
            this.prop_color_url = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_URL));
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED)) {
            Log.d(TAG, ("has PROPERTY_COLOR_TEXT_DEPRECATED: " + d.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED)));
            this.prop_color_text = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED));
        }
        if (d.containsKey((Object)AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED)) {
            Log.d(TAG, ("has PROPERTY_COLOR_LINK_DEPRECATED: " + d.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED)));
            this.prop_color_link = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED));
        }
        if (d.containsKey((Object)AdmobModule.AD_SIZE_TYPE)) {
            Log.d(TAG, ("has AD_SIZE_TYPE: " + d.getString(AdmobModule.AD_SIZE_TYPE)));
            String adType = d.getString(AdmobModule.AD_SIZE_TYPE);
            if (adType.equals("BANNER")) {
                this.createAdView();
            } else if (adType.equals("RECTANGLE")) {
                this.createRectangleBannerAdView();
            } else if (adType.equals("FULLBANNER")) {
                this.createFullBannerAdView();
            } else if (adType.equals("LEADERBOARD")) {
                this.createLeaderBoardAdView();
            } else if (adType.equals("SMART")) {
                this.createSmartBannerAdView();
            } else if (adType.equals("INTERSTITIALAD")) {
                this.createInterstitialAdView();
            } else if (adType.equals("NATIVE")) {
                this.createNativeAdView();
            } else {
                this.createAdView();
            }
        } else {
            this.createAdView();
        }
    }

    private String convertColorProp(String color) {
        if ((color = color.replace("#", "")).equals("white")) {
            color = "FFFFFF";
        }
        if (color.equals("red")) {
            color = "FF0000";
        }
        if (color.equals("blue")) {
            color = "0000FF";
        }
        if (color.equals("green")) {
            color = "008000";
        }
        if (color.equals("yellow")) {
            color = "FFFF00";
        }
        if (color.equals("black")) {
            color = "000000";
        }
        return color;
    }

    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case 0: {
                errorReason = "Internal error";
                break;
            }
            case 1: {
                errorReason = "Invalid request";
                break;
            }
            case 2: {
                errorReason = "Network Error";
                break;
            }
            case 3: {
                errorReason = "No fill";
            }
        }
        return errorReason;
    }

}
