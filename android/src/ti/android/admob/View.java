package ti.android.admob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.view.TiUIView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class AdmobView extends TiUIView {
	
	private static final String TAG = "AdmobView";
	
	PublisherAdView adView;
    InterstitialAd intAd;
    View nativeAd;

    LayoutInflater myInflater;
    
    int prop_top;
    int prop_left;
    int prop_right;
    
    String prop_color_bg;
    String prop_color_bg_top;
    String prop_color_border;
    String prop_color_text;
    String prop_color_link;
    String prop_color_url;
    
    FrameLayout frameLayout;
    
	private int contentad_headline;
	private int contentad_image;
	private int contentad_body;
	private int contentad_call_to_action;
	private int contentad_logo;
	private int contentad_advertiser;
	
    private int appinstall_headline;
    private int appinstall_body;
    private int appinstall_call_to_action;
    private int appinstall_app_icon;
    private int appinstall_price;
    private int appinstall_stars;
    private int appinstall_store;
    
    private MediaView mediaView;
    private ImageView mainImageView;
    
    private int appinstall_media;
    private int appinstall_image;
    
    private int ad_app_install;
    private int ad_content;
    
    public AdmobView(TiViewProxy proxy) {
        super(proxy);
        Log.d(TAG, "Creating AdMob AdView");
        Log.d(TAG, ("AdmobModule.PUBLISHER_ID: " + AdmobModule.PUBLISHER_ID));
        Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.AD_UNIT_ID));

        myInflater = LayoutInflater.from((Context)this.proxy.getActivity());
        
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize((Context)this.proxy.getActivity(), AdmobModule.AD_UNIT_ID);
    }
    
    private void createAdView(String type, AdSize SIZE) {
        Log.d(TAG, "createAdView() " + type);
        this.adView = new PublisherAdView((Context) this.proxy.getActivity());
        
        if(AdmobModule.AD_SIZES != null){
        	if(SIZE != null){
        		AdmobModule.AD_SIZES.add(SIZE);
        	}
        	this.adView.setAdSizes(AdmobModule.AD_SIZES.toArray(new AdSize[0]));
        }else{
        	this.adView.setAdSizes(SIZE);
        }
        
        Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.AD_UNIT_ID));
        this.adView.setAdUnitId(AdmobModule.AD_UNIT_ID);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR).addTestDevice(AdmobModule.TEST_DEVICE_ID).build();
        this.adView.setAdListener(new AdListener(){

            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                if(AdmobView.this.proxy != null){
                	Log.d (TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
                	AdmobView.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
                }
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.e(TAG, ("onAdFailedToLoad(): " + getErrorReason(errorCode)));
                if(AdmobView.this.proxy != null){
                	AdmobView.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
                }
            }
        });
        this.adView.loadAd(adRequest);
        this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
        this.setNativeView((android.view.View)this.adView);
    }
    
    /**
     * Populates a {@link NativeAppInstallAdView} object with data from a given
     * {@link NativeAppInstallAd}.
     *
     * @param nativeAppInstallAd the object containing the ad's assets
     * @param adView             the view to be populated
     */
    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd, NativeAppInstallAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });
        
        try{
	        appinstall_headline = TiRHelper.getResource("id.appinstall_headline");
	        appinstall_body = TiRHelper.getResource("id.appinstall_body");
	        appinstall_call_to_action = TiRHelper.getResource("id.appinstall_call_to_action");
	        appinstall_app_icon = TiRHelper.getResource("id.appinstall_app_icon");
	        appinstall_price = TiRHelper.getResource("id.appinstall_price");
	        appinstall_stars = TiRHelper.getResource("id.appinstall_stars");
	        appinstall_store = TiRHelper.getResource("id.appinstall_store");
        } catch (ResourceNotFoundException e) {
			Log.e(TAG, "XML resources could not be found!!!", Log.DEBUG_MODE);
		}

        adView.setHeadlineView(adView.findViewById(appinstall_headline));
        adView.setBodyView(adView.findViewById(appinstall_body));
        adView.setCallToActionView(adView.findViewById(appinstall_call_to_action));
        adView.setIconView(adView.findViewById(appinstall_app_icon));
        adView.setPriceView(adView.findViewById(appinstall_price));
        adView.setStarRatingView(adView.findViewById(appinstall_stars));
        adView.setStoreView(adView.findViewById(appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
    	((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
    	((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
    	((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
    	((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());
        
        try {
        	appinstall_media = TiRHelper.getResource("id.appinstall_media");
        	appinstall_image = TiRHelper.getResource("id.appinstall_image");
        } catch (ResourceNotFoundException e) {
			Log.e(TAG, "Media and ImageView XML resources could not be found!!!", Log.DEBUG_MODE);
		}
        
        mediaView = (MediaView) adView.findViewById(appinstall_media);
        mainImageView = (ImageView) adView.findViewById(appinstall_image);
        
        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);
            
            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAppInstallAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());
        }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }
    
    /**
     * Populates a {@link NativeContentAdView} object with data from a given
     * {@link NativeContentAd}.
     *
     * @param nativeContentAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateContentAdView(NativeContentAd nativeContentAd, NativeContentAdView adView) {
    	
    	try{
	    	contentad_headline = TiRHelper.getResource("id.contentad_headline");
	    	contentad_image = TiRHelper.getResource("id.contentad_image");
	    	contentad_body = TiRHelper.getResource("id.contentad_body");
	    	contentad_call_to_action = TiRHelper.getResource("id.contentad_call_to_action");
	    	contentad_logo = TiRHelper.getResource("id.contentad_logo");
	    	contentad_advertiser = TiRHelper.getResource("id.contentad_advertiser");
    	} catch (ResourceNotFoundException e) {
			Log.e(TAG, "XML resources could not be found!!!", Log.DEBUG_MODE);
		}

        adView.setHeadlineView(adView.findViewById(contentad_headline));
        adView.setImageView(adView.findViewById(contentad_image));
        adView.setBodyView(adView.findViewById(contentad_body));
        adView.setCallToActionView(adView.findViewById(contentad_call_to_action));
        adView.setLogoView(adView.findViewById(contentad_logo));
        adView.setAdvertiserView(adView.findViewById(contentad_advertiser));
    	
        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }
    
    private void createNativeAdView() {
        Log.d(TAG, "createNativeAdView()");
        
        AdLoader.Builder builder = new AdLoader.Builder((Context)this.proxy.getActivity(), AdmobModule.AD_UNIT_ID);
        
        frameLayout = new FrameLayout((Context)this.proxy.getActivity());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        
        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
            	
            	Log.d(TAG, "onAppInstallAdLoaded");
            	
        		try {
        			ad_app_install = TiRHelper.getResource("layout.ad_app_install");
				} catch (ResourceNotFoundException e) {
					e.printStackTrace();
				}
        		Log.d(TAG, "ad_content: " + ad_content);

            	NativeAppInstallAdView nativeAd = (NativeAppInstallAdView) myInflater.inflate(ad_app_install, null);
                populateAppInstallAdView(ad, nativeAd);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAd);
            }
        });
        
        builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
            public void onContentAdLoaded(NativeContentAd ad) {
            	
            	Log.d(TAG, "onContentAdLoaded()");

        		try {
					ad_content = TiRHelper.getResource("layout.ad_content");
				} catch (ResourceNotFoundException e) {
					e.printStackTrace();
				}
        		Log.d(TAG, "ad_content: " + ad_content);
              
                NativeContentAdView nativeAd = (NativeContentAdView) myInflater.inflate(ad_content, null);
                populateContentAdView(ad, nativeAd);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAd);
            }
        });
        
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
		NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
		
		builder.withNativeAdOptions(adOptions);
		
		AdLoader adLoader = builder.withAdListener(new AdListener() {
			
			public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                if(AdmobView.this.proxy != null){
                	AdmobView.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
                }
            }
			
		    public void onAdFailedToLoad(int errorCode) {
		    	 Log.e(TAG, ("onAdFailedToLoad(): " + errorCode));
		    	 if(AdmobView.this.proxy != null){
		    		 AdmobView.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
		    	 }
		    }
		}).build();
		
		AdRequest AR = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(AdmobModule.TEST_DEVICE_ID).build();
		adLoader.loadAd(AR);
		AdmobView.this.setNativeView((android.view.View) frameLayout);
    }

    private void createInterstitialAdView() {

    	Log.d(TAG, "createInterstitialAdView()");
        
    	this.intAd = new InterstitialAd((Context)this.proxy.getActivity());
        this.intAd.setAdUnitId(AdmobModule.AD_UNIT_ID);
        this.intAd.setAdListener(new AdListener(){
        	
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                if(AdmobView.this.proxy != null){
	                AdmobView.this.proxy.fireEvent("ad_received", (Object)new KrollDict());
	                if (AdmobView.this.intAd.isLoaded()) {
	                    //View.this.intAd.show();
	                	AdmobView.this.proxy.fireEvent("ad_ready_to_be_shown", (Object)new KrollDict());
	                	Log.d(TAG, "Interstitial are ready to be shown.");
	                } else {
	                    Log.d(TAG, "Interstitial ad was not ready to be shown.");
	                }
            	}
            }
            
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad (%s)", AdmobView.this.getErrorReason(errorCode));
                Log.e(TAG, message);
                if(AdmobView.this.proxy != null){
                	AdmobView.this.proxy.fireEvent("ad_not_received", (Object)new KrollDict());
                }
            }

            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                Log.d(TAG, "Ad Closed");
                if(AdmobView.this.proxy != null){
                	AdmobView.this.proxy.fireEvent("ad_closed", (Object)new KrollDict());
                }
            }
        });
        
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdmobModule.TEST_DEVICE_ID).build();
        this.intAd.loadAd(adRequest);
    }
    
	public void showAdNow() {
		this.proxy.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				if(intAd != null){
					if (!intAd.isLoaded()) {
						Log.e(TAG, "Invalid interstitial ads call: No loaded interstitial ads in store yet!");
						if(AdmobView.this.proxy != null){
							AdmobView.this.proxy.fireEvent("ad_not_ready_yet", (Object)new KrollDict());
						}
					} else {
						if(AdmobView.this.proxy != null){
							AdmobView.this.proxy.fireEvent("ad_being_showed", (Object)new KrollDict());
							intAd.show();
						}
					}
				}
			}
		});
	}
	
	 public void destroy() {
		this.proxy.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				
				Log.d(TAG, "destroy");
				
				if(adView != null){
					adView.destroy();
				}
				
				if(intAd != null){
					intAd = null;
				}
				
				if(AdmobView.this.proxy != null){
	        		AdmobView.this.proxy.fireEvent("ad_destroyed", (Object)new KrollDict());
	        	}
			}
		});
    }
   
    public void processProperties(KrollDict d) {
        super.processProperties(d);
        Log.d(TAG, "process properties");
        
        if (d.containsKey((Object)"adSizes")) {
            Log.d(TAG, ("has adSizes"));
            
            Object[] adSizes = (Object[]) d.get("adSizes");
            
            AdmobModule.AD_SIZES = new ArrayList<AdSize>();
            
            for (int i = 0; i < adSizes.length; i++)
            {
            	@SuppressWarnings("unchecked")
				Map<String,Integer> hm = (Map<String,Integer>) adSizes[i];

            	// You now have a HashMap!
            	Log.d (TAG, "" + hm);

            	AdmobModule.AD_SIZES.add(new AdSize(hm.get("width"), hm.get("height")));
            }
        }
        if (d.containsKey((Object)"adUnitId")) {
            Log.d(TAG, ("has adUnitId: " + d.getString("adUnitId")));
            AdmobModule.AD_UNIT_ID = d.getString("adUnitId");
        }
        if (d.containsKey((Object)"testDeviceId")) {
            Log.d(TAG, ("has testDeviceId: " + d.getString("testDeviceId")));
            AdmobModule.TEST_DEVICE_ID = d.getString("testDeviceId");
        }
        if (d.containsKey((Object)"publisherId")) {
            Log.d(TAG, ("has publisherId: " + d.getString("publisherId")));
            AdmobModule.PUBLISHER_ID = d.getString("publisherId");
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
                this.createAdView(adType, AdSize.BANNER);
            } else if (adType.equals("RECTANGLE")) {
                this.createAdView(adType, AdSize.MEDIUM_RECTANGLE);
            } else if (adType.equals("FULLBANNER")) {
            	this.createAdView(adType, AdSize.FULL_BANNER);
            } else if (adType.equals("LEADERBOARD")) {
            	this.createAdView(adType, AdSize.LEADERBOARD);
            } else if (adType.equals("SMART")) {
                this.createAdView(adType, AdSize.SMART_BANNER);
            } else if (adType.equals("INTERSTITIALAD")) {
                this.createInterstitialAdView();
            } else if (adType.equals("NATIVE")) {
                this.createNativeAdView();
            } else if (adType.equals("FLUID")) {
            	this.createAdView(adType, AdSize.FLUID);
            } else {
            	this.createAdView("Not defined", AdSize.SMART_BANNER);
            }
        } else {
        	this.createAdView("Not defined", AdSize.SMART_BANNER);
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
