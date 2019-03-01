package ti.android.admob;

import java.util.ArrayList;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.util.TiColorHelper;

import ti.modules.titanium.ui.ButtonProxy;
import ti.modules.titanium.ui.ImageViewProxy;
import ti.modules.titanium.ui.LabelProxy;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class AdmobView extends TiUIView implements RewardedVideoAdListener{

	private static final String TAG = "AdmobView";

	private PublisherAdView adView;
	private InterstitialAd intAd;
	View nativeAd;
	UnifiedNativeAd unifiedNativeAd;
	UnifiedNativeAd tempNativeAd;

	LayoutInflater myInflater;

	int prop_top;
	int prop_left;
	int prop_right;

	int native_ads_background_color;
	String prop_color_bg;
	String prop_color_bg_top;
	String prop_color_border;
	String prop_color_text;
	String prop_color_link;
	String prop_color_url;

	FrameLayout frameLayout;
	
	private ViewGroup contentad_stars;
	private RatingBar contentad_stars_view;
	
	private ViewGroup contentad_logo;
	private ImageView contentad_logo_view;
	
	private TextView contentad_headline;
	private TextView contentad_store;
	private TextView contentad_price;
	private TextView contentad_body;
	private TextView contentad_advertiser;
	private Button contentad_call_to_action;
	private View master_view;
	private MediaView contentad_media_view;


	private TiViewProxy contentad_media_proxy;
	private TiViewProxy master_view_proxy;
	private LabelProxy contentad_headline_proxy;
	private TiViewProxy contentad_stars_proxy;
	private LabelProxy contentad_store_proxy;
	private LabelProxy contentad_price_proxy;
	private LabelProxy contentad_body_proxy;
	private ButtonProxy contentad_call_to_action_proxy;
	private ImageViewProxy contentad_logo_proxy;
	private LabelProxy contentad_advertiser_proxy;

	private RatingBar ratingBar;
	
	private String keyword;
	private String contentUrl;
	private String adType;
	
	private RewardedVideoAd rewardedVideoAd;

	static TiApplication appContext = TiApplication.getInstance();

	public AdmobView(TiViewProxy proxy) {
		super(proxy);
		Log.d(TAG, "Creating AdMob AdView");
		Log.d(TAG, ("AdmobModule.PUBLISHER_ID: " + AdmobModule.PUBLISHER_ID));
		Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.AD_UNIT_ID));

		myInflater = LayoutInflater.from((Context) this.proxy.getActivity());

		// Initialize the Mobile Ads SDK.
		MobileAds.initialize((Context) this.proxy.getActivity(), AdmobModule.AD_UNIT_ID);
	}

	private void createAdView(String type, AdSize SIZE) {
		Log.d(TAG, "createAdView() " + type);
		this.adView = new PublisherAdView((Context) this.proxy.getActivity());

		if (AdmobModule.AD_SIZES != null) {
			if (SIZE != null) {
				AdmobModule.AD_SIZES.add(SIZE);
			}
			this.adView.setAdSizes(AdmobModule.AD_SIZES.toArray(new AdSize[0]));
		} else {
			this.adView.setAdSizes(SIZE);
		}

		Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.AD_UNIT_ID));
		this.adView.setAdUnitId(AdmobModule.AD_UNIT_ID);
		
		PublisherAdRequest.Builder AdRequestBuilder = new PublisherAdRequest.Builder();
		
		AdRequestBuilder.addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR).addTestDevice(AdmobModule.TEST_DEVICE_ID);
		
		if(keyword != null){
			AdRequestBuilder.addKeyword(keyword);
		}
		
		if(contentUrl != null){
			AdRequestBuilder.setContentUrl(contentUrl);
		}
		
		PublisherAdRequest adRequest = AdRequestBuilder.build();
				
		this.adView.setAdListener(new AdListener() {

			public void onAdLoaded() {
				Log.d(TAG, "onAdLoaded()");
				if (AdmobView.this.proxy != null) {
					Log.d(TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_RECEIVED, (Object) new KrollDict());
					}
				}
			}

			public void onAdFailedToLoad(int errorCode) {
				Log.d(TAG, ("onAdFailedToLoad(): " + getErrorReason(errorCode)));
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) new KrollDict());
					}
				}
			}
		});
		this.adView.loadAd(adRequest);
		this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
		this.setNativeView((View) this.adView);
	}

	private void createUnifiedNativeAds(){
		
		AdLoader.Builder builder = new AdLoader.Builder((Context) this.proxy.getActivity(), AdmobModule.AD_UNIT_ID);
		
		frameLayout = new FrameLayout((Context) this.proxy.getActivity());
		frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		
		builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
	        
			@Override
	        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
	            // Show the ad.
	        	Log.d(TAG, "onContentAdLoaded()");
	        	
	        	if(master_view_proxy != null){
	        	
		        	master_view = (View) master_view_proxy.getOrCreateView().getOuterView();
		        	
		        	if (tempNativeAd != null) {
	                    tempNativeAd.destroy();
	                }
		        	
		        	tempNativeAd = unifiedNativeAd;
		        	
		        	UnifiedNativeAdView nativeAd = new UnifiedNativeAdView(appContext);
		        	
	            	nativeAd.setLayoutParams(new FrameLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
	            	
					//nativeAd.setMinimumHeight(50);
					nativeAd.setBackgroundColor(native_ads_background_color);
	
					if(contentad_call_to_action_proxy != null){
						contentad_call_to_action = (Button) contentad_call_to_action_proxy.getOrCreateView().getOuterView();
					}
					
					if(contentad_headline_proxy != null){
						contentad_headline = (TextView) contentad_headline_proxy.getOrCreateView().getOuterView();
					}
					
					if(contentad_body_proxy != null){
						contentad_body = (TextView) contentad_body_proxy.getOrCreateView().getOuterView();
					}
					
					if(contentad_store_proxy != null){
						contentad_store = (TextView) contentad_store_proxy.getOrCreateView().getOuterView();
					}
					
					if(contentad_price_proxy != null){
						contentad_price = (TextView) contentad_price_proxy.getOrCreateView().getOuterView();
					}
					
					if(contentad_advertiser_proxy != null){
						contentad_advertiser = (TextView) contentad_advertiser_proxy.getOrCreateView().getOuterView();
					}
	
					if(contentad_logo_proxy != null){
						contentad_logo = (ViewGroup) contentad_logo_proxy.getOrCreateView().getOuterView();
						try{
							contentad_logo_view = (ImageView) contentad_logo.getChildAt(0);
						} catch (ClassCastException exc) {
							contentad_logo_view = (ImageView) ((ViewGroup) contentad_logo.getChildAt(0)).getChildAt(0);
						}
					}
					
					if(contentad_stars_proxy != null){
						contentad_stars = (ViewGroup) contentad_stars_proxy.getOrCreateView().getOuterView();
						contentad_stars_view = (RatingBar) contentad_stars.getChildAt(0);
					}
					
					if(contentad_media_proxy != null){
						contentad_media_view = (MediaView) contentad_media_proxy.getOrCreateView().getOuterView();
					}
	
					// Remove from parent (if exists)
					ViewGroup parent = (ViewGroup) master_view.getParent();
					if (parent != null) {
					    parent.removeView(master_view);
					}
					// Add to another parent
					nativeAd.addView(master_view);
	            	
					populateUnifiedNativeAdView(unifiedNativeAd, nativeAd);
	                frameLayout.removeAllViews();
	                frameLayout.addView(nativeAd);
	        	} else {
	        		Log.e(TAG, "No master_view defined!");
	        	}
	        }
	    });
	    
		VideoOptions videoOptions = new VideoOptions.Builder()
		        .setStartMuted(true)
		        .build();
		
		NativeAdOptions adOptions = new NativeAdOptions.Builder()
		        .setVideoOptions(videoOptions)
		        .build();
		
		builder.withNativeAdOptions(adOptions);
		
		AdLoader adLoader = builder.withAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				Log.d(TAG, "onAdLoaded()");
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_RECEIVED, (Object) new KrollDict());
					}
				}
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				Log.d(TAG, ("onAdFailedToLoad(): " + getErrorReason(errorCode)));
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) new KrollDict());
					}
				}
			}
		}).build();
		
		PublisherAdRequest.Builder AdRequestBuilder = new PublisherAdRequest.Builder();
		
		AdRequestBuilder.addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR)
						.addTestDevice(AdmobModule.TEST_DEVICE_ID);
		
		if(keyword != null){
			AdRequestBuilder.addKeyword(keyword);
		}
		
		if(contentUrl != null){
			AdRequestBuilder.setContentUrl(contentUrl);
		}
		
		PublisherAdRequest AR = AdRequestBuilder.build();
		
		//AdRequest AR = new AdRequest.Builder()
		//		.addKeyword(keyword)
		//		.setContentUrl(contentUrl)
		//		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		//		.addTestDevice(AdmobModule.TEST_DEVICE_ID)
		//		.build();
		
		adLoader.loadAd(AR);
		AdmobView.this.setNativeView((View) frameLayout);
	}

	/**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        adView.setMediaView(contentad_media_view);

        // Set other ad assets.
        if(contentad_headline != null){
        	adView.setHeadlineView(contentad_headline);
        }
        if(contentad_body != null){
        	adView.setBodyView(contentad_body);
        }
        if(contentad_call_to_action != null){
        	adView.setCallToActionView(contentad_call_to_action);
        }
        if(contentad_logo_view != null){
        	adView.setIconView(contentad_logo_view);
        }
        if(contentad_price != null){
        	adView.setPriceView(contentad_price);
        }
        if(contentad_stars_view != null){
        	adView.setStarRatingView(contentad_stars_view);
        }
        if(contentad_store != null){
        	adView.setStoreView(contentad_store);
        }
        if(contentad_advertiser != null){
        	adView.setAdvertiserView(contentad_advertiser);
        }
        

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
          adView.getBodyView().setVisibility(View.GONE);
        } else {
          adView.getBodyView().setVisibility(View.VISIBLE);
          ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
          adView.getCallToActionView().setVisibility(View.GONE);
        } else {
          adView.getCallToActionView().setVisibility(View.VISIBLE);
          ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }
    }
	
	private void createRewardedAdView(){
		Log.d(TAG, "createRewardedAdView()");
		
		this.rewardedVideoAd = MobileAds.getRewardedVideoAdInstance((Context) this.proxy.getActivity());
		this.rewardedVideoAd.setRewardedVideoAdListener(AdmobView.this);
	}

	private void createInterstitialAdView() {

		Log.d(TAG, "createInterstitialAdView()");

		this.intAd = new InterstitialAd((Context) this.proxy.getActivity());
		this.intAd.setAdUnitId(AdmobModule.AD_UNIT_ID);
		
		this.intAd.setAdListener(new AdListener() {

			@Override
			public void onAdLoaded() {
				Log.d(TAG, "onAdLoaded");
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_RECEIVED, (Object) new KrollDict());
					}
					if (AdmobView.this.intAd.isLoaded()) {
						// View.this.intAd.show();
						if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_READY_TO_BE_SHOWN)) {
							AdmobView.this.proxy.fireEvent(AdmobModule.AD_READY_TO_BE_SHOWN, (Object) new KrollDict());
						}
						Log.d(TAG, "Interstitial are ready to be shown.");
					} else {
						Log.d(TAG, "Interstitial ad was not ready to be shown.");
					}
				}
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				String message = String.format("onAdFailedToLoad (%s)", AdmobView.this.getErrorReason(errorCode));
				Log.d(TAG, message + getErrorReason(errorCode));
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) new KrollDict());
					}
				}
			}

			@Override
			public void onAdClosed() {
				Log.d(TAG, "onAdClosed");
				Log.d(TAG, "Ad Closed");
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLOSED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLOSED, (Object) new KrollDict());
					}
				}
			}
		});

		AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdmobModule.TEST_DEVICE_ID).build();
		this.intAd.loadAd(adRequest);
	}

	public void showAdNow() {
		
		if(adType.equals(AdmobModule.REWARDED_VIDEO)) {
			if (this.rewardedVideoAd.isLoaded()) {
				this.rewardedVideoAd.show();
			} else {
				Log.w(TAG, "Trying to show a rewarded video ad that has not loaded yet.");
			}
			
		}else if(adType.equals(AdmobModule.INTERSTITIAL)) {
			
			this.proxy.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					if (intAd != null) {
						if (!intAd.isLoaded()) {
							Log.d(TAG, "Invalid interstitial ads call: No loaded interstitial ads in store yet!");
							if (AdmobView.this.proxy != null) {
								if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_READY_YET)) {
									AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_READY_YET, (Object) new KrollDict());
								}
							}
						} else {
							if (AdmobView.this.proxy != null) {
								if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_BEING_SHOWN)) {
									AdmobView.this.proxy.fireEvent(AdmobModule.AD_BEING_SHOWN, (Object) new KrollDict());
								}
								intAd.show();
							}
						}
					}
				}
			});
		}
	}
	
	public void loadRewardedAdVideo(){
		
		AdRequest.Builder AdRequestBuilder = new AdRequest.Builder();
		
		AdRequestBuilder.addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR).addTestDevice(AdmobModule.TEST_DEVICE_ID);
		
		if(keyword != null){
			AdRequestBuilder.addKeyword(keyword);
		}
		
		if(contentUrl != null){
			AdRequestBuilder.setContentUrl(contentUrl);
		}
		
		AdRequest adRequest = AdRequestBuilder.build();
		
		this.rewardedVideoAd.loadAd(AdmobModule.AD_UNIT_ID, adRequest);
	}
	
    private void createRatingView() {
        Log.d(TAG, "createRatingView()");
        
        LinearLayout layout = new LinearLayout((Context) proxy.getActivity());
		layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        
		ratingBar = new RatingBar((Context) proxy.getActivity());
		ratingBar.setNumStars(5);
		ratingBar.setRating(0);
		
		layout.addView(ratingBar);
		
        this.setNativeView((View)layout);
    }
    
    private void createMediaView() {
        Log.d(TAG, "createMediaView()");
        
        MediaView mediaView = new MediaView((Context) proxy.getActivity());
        this.setNativeView((View)mediaView);
    }

	public void destroy() {
		this.proxy.getActivity().runOnUiThread(new Runnable() {
			public void run() {

				Log.d(TAG, "destroy");

				if (adView != null) {
					adView.destroy();
				}

				if (intAd != null) {
					intAd = null;
				}

				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_DESTROYED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_DESTROYED, (Object) new KrollDict());
					}
				}
			}
		});
	}

	@Override
	public void processProperties(KrollDict d) {
		super.processProperties(d);
		Log.d(TAG, "process properties");
		
		if (d.containsKey(AdmobModule.VIEW_TYPE)) {
			
			String view_type = (String) d.get(AdmobModule.VIEW_TYPE);
			
			Log.d(TAG, ("has VIEW_TYPE: " + view_type));
			
			if(view_type.equals(AdmobModule.TYPE_STARS)){
				
				Log.d(TAG, ("view_type = " + view_type) + " createRatingView()");
				
				createRatingView();
			}else if(view_type.equals(AdmobModule.TYPE_MEDIA)){
				
				Log.d(TAG, ("view_type = " + view_type) + " createMediaView()");
				
				createMediaView();
			}else if(view_type.equals(AdmobModule.TYPE_ADS)){
				
				Log.d(TAG, ("view_type = " + view_type) + " searching p");
				
				if (d.containsKey(AdmobModule.MASTER_VIEW)) {
					Object view = d.get(AdmobModule.MASTER_VIEW);
					if (view != null && view instanceof TiViewProxy) {
						if (view instanceof TiWindowProxy){
							throw new IllegalStateException("[ERROR] Cannot use window as AdmobView view");
						}
						Log.d(TAG, "[SUCESS] type for master_view is TiViewProxy");
						master_view_proxy = (TiViewProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for master_view");
					}
				}
				
				if (d.containsKey(AdmobModule.MEDIA_VIEW)) {
					Object view = d.get(AdmobModule.MEDIA_VIEW);
					if (view != null && view instanceof TiViewProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_media is TiViewProxy");
						contentad_media_proxy = (TiViewProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_media");
					}
				}

				if (d.containsKey(AdmobModule.HEADLINE_LABEL)) {
					Object view = d.get(AdmobModule.HEADLINE_LABEL);
					if (view != null && view instanceof LabelProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_headline is LabelProxy");
						contentad_headline_proxy = (LabelProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_headline");
					}
				}

				if (d.containsKey(AdmobModule.BODY_LABEL)) {
					Object view = d.get(AdmobModule.BODY_LABEL);
					if (view != null && view instanceof LabelProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_body is LabelProxy");
						contentad_body_proxy = (LabelProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_body");
					}
				}

				if (d.containsKey(AdmobModule.CALL_TO_ACTION_BUTTON)) {
					Object view = d.get(AdmobModule.CALL_TO_ACTION_BUTTON);
					if (view != null && view instanceof ButtonProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_call_to_action is ButtonProxy");
						contentad_call_to_action_proxy = (ButtonProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_call_to_action");
					}
				}

				if (d.containsKey(AdmobModule.LOGO_OR_ICON_IMAGE_VIEW)) {
					Object view = d.get(AdmobModule.LOGO_OR_ICON_IMAGE_VIEW);
					if (view != null && view instanceof ImageViewProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_logo is ImageViewProxy");
						contentad_logo_proxy = (ImageViewProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_logo");
					}
				}

				if (d.containsKey(AdmobModule.ADVERTISER_LABEL)) {
					Object view = d.get(AdmobModule.ADVERTISER_LABEL);
					if (view != null && view instanceof LabelProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_advertiser is LabelProxy");
						contentad_advertiser_proxy = (LabelProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_advertiser");
					}
				}
				
				if (d.containsKey(AdmobModule.PRICE_LABEL)) {
					Object view = d.get(AdmobModule.PRICE_LABEL);
					if (view != null && view instanceof LabelProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_price_view is LabelProxy");
						contentad_price_proxy = (LabelProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_price_view");
					}
				}
				
				if (d.containsKey(AdmobModule.STORE_LABEL)) {
					Object view = d.get(AdmobModule.STORE_LABEL);
					if (view != null && view instanceof LabelProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_store_view is LabelProxy");
						contentad_store_proxy = (LabelProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_store_view");
					}
				}
				
				if (d.containsKey(AdmobModule.STARS_VIEW)) {
					Object view = d.get(AdmobModule.STARS_VIEW);
					if (view != null && view instanceof TiViewProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_rating_view is TiViewProxy");
						contentad_stars_proxy = (TiViewProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for contentad_rating_view");
					}
				}

				if (d.containsKey(AdmobModule.AD_SIZES_LABEL)) {
					Log.d(TAG, ("has adSizes"));

					Object[] adSizes = (Object[]) d.get(AdmobModule.AD_SIZES_LABEL);

					AdmobModule.AD_SIZES = new ArrayList<AdSize>();

					for (int i = 0; i < adSizes.length; i++) {
						@SuppressWarnings("unchecked")
						Map<String, Integer> hm = (Map<String, Integer>) adSizes[i];

						// You now have a HashMap!
						Log.d(TAG, "" + hm);

						AdmobModule.AD_SIZES.add(new AdSize(hm.get("width"), hm.get("height")));
					}
				}
				
				if (d.containsKey((Object) AdmobModule.NATIVE_ADS_BACKGROUND_COLOR)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG: " + d.getString(AdmobModule.NATIVE_ADS_BACKGROUND_COLOR)));
					this.native_ads_background_color = TiColorHelper.parseColor(d.getString(AdmobModule.NATIVE_ADS_BACKGROUND_COLOR));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_BG)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG: " + d
							.getString(AdmobModule.PROPERTY_COLOR_BG)));
					this.prop_color_bg = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_BG));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_BG_TOP)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG_TOP: " + d
							.getString(AdmobModule.PROPERTY_COLOR_BG_TOP)));
					this.prop_color_bg_top = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_BG_TOP));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_BORDER)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BORDER: " + d
							.getString(AdmobModule.PROPERTY_COLOR_BORDER)));
					this.prop_color_border = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_BORDER));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_TEXT)) {
					Log.d(TAG, ("has PROPERTY_COLOR_TEXT: " + d
							.getString(AdmobModule.PROPERTY_COLOR_TEXT)));
					this.prop_color_text = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_TEXT));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_LINK)) {
					Log.d(TAG, ("has PROPERTY_COLOR_LINK: " + d
							.getString(AdmobModule.PROPERTY_COLOR_LINK)));
					this.prop_color_link = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_LINK));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_URL)) {
					Log.d(TAG, ("has PROPERTY_COLOR_URL: " + d
							.getString(AdmobModule.PROPERTY_COLOR_URL)));
					this.prop_color_url = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_URL));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED)) {
					Log.d(TAG, ("has PROPERTY_COLOR_TEXT_DEPRECATED: " + d
							.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED)));
					this.prop_color_text = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED));
				}
				
				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED)) {
					Log.d(TAG, ("has PROPERTY_COLOR_LINK_DEPRECATED: " + d
							.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED)));
					this.prop_color_link = this.convertColorProp(d
							.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED));
				}
				
				if (d.containsKey((Object) AdmobModule.CONTENT_URL)) {
					Log.d(TAG, ("has CONTENT_URL: " + d.getString(AdmobModule.CONTENT_URL)));
					contentUrl = (String) d.getString(AdmobModule.CONTENT_URL);
				}
				
				if (d.containsKey((Object) AdmobModule.KEYWORD)) {
					Log.d(TAG, ("has CONTENT_URL: " + d.getString(AdmobModule.KEYWORD)));
					keyword = (String) d.getString(AdmobModule.KEYWORD);
				}
				
				if (d.containsKey(AdmobModule.AD_SIZE_TYPE)) {
					
					Log.d(TAG, ("has AD_SIZE_TYPE: " + d.getString(AdmobModule.AD_SIZE_TYPE)));
					
					adType = d.getString(AdmobModule.AD_SIZE_TYPE);
					
					if (adType.equals(AdmobModule.BANNER)) {
						this.createAdView(adType, AdSize.BANNER);
					} else if (adType.equals(AdmobModule.REWARDED_VIDEO)) {
						this.createRewardedAdView();
					} else if (adType.equals(AdmobModule.MEDIUM_RECTANGLE)) {
						this.createAdView(adType, AdSize.MEDIUM_RECTANGLE);
					} else if (adType.equals(AdmobModule.FULL_BANNER)) {
						this.createAdView(adType, AdSize.FULL_BANNER);
					} else if (adType.equals(AdmobModule.LEADERBOARD)) {
						this.createAdView(adType, AdSize.LEADERBOARD);
					} else if (adType.equals(AdmobModule.SMART_BANNER)) {
						this.createAdView(adType, AdSize.SMART_BANNER);
					} else if (adType.equals(AdmobModule.INTERSTITIAL)) {
						this.createInterstitialAdView();
					} else if(adType.equals(AdmobModule.UNIFIED_NATIVE_ADS)){
						this.createUnifiedNativeAds();
					} else if (adType.equals(AdmobModule.FLUID)) {
						this.createAdView(adType, AdSize.FLUID);
					} else if (adType.equals(AdmobModule.LARGE_BANNER)) {
						this.createAdView(adType, AdSize.LARGE_BANNER);
					} else if (adType.equals(AdmobModule.SEARCH)) {
						this.createAdView(adType, AdSize.SEARCH);
					} else if (adType.equals(AdmobModule.WIDE_SKYSCRAPER)) {
						this.createAdView(adType, AdSize.WIDE_SKYSCRAPER);
					} else {
						this.createAdView("Not defined", AdSize.SMART_BANNER);
					}
				} else {
					Log.d(TAG, "No ad_size_type defined. Can't show ads!");
				}
			}else{
				Log.d(TAG, "viewType exists but is not Media, Ads or Stars");
			}
		}else{
			Log.d(TAG, "No key viewType defined. it shoud be Media, Ads or Stars");
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
	
	//REWARED VIDEOS EVENTS
	@Override
	public void onRewardedVideoAdLoaded() {
		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_RECEIVED, new KrollDict());
		}
	}
 	@Override
	public void onRewardedVideoAdOpened() {
		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_OPENED)) {
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_OPENED, new KrollDict());
		}
	}
 	@Override
	public void onRewardedVideoStarted() {
		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_VIDEO_STARTED)) {
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_VIDEO_STARTED, new KrollDict());
		}
	}
 	@Override
	public void onRewardedVideoAdClosed() {
 		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLOSED)) {
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLOSED, new KrollDict());
		}
	}
 	@Override
	public void onRewarded(RewardItem rewardItem) {
 		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_REWARDED)) {
 			KrollDict rewardReceived = new KrollDict();
			rewardReceived.put("type", rewardItem.getType());
			rewardReceived.put("amount", rewardItem.getAmount());
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_REWARDED, rewardReceived);
		}
	}
 	@Override
	public void onRewardedVideoAdLeftApplication() {
		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_DESTROYED)) {
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_DESTROYED, new KrollDict());
		}
	}
 	@Override
	public void onRewardedVideoAdFailedToLoad(int i) {
		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, new KrollDict());
		}
	}

	@Override
	public void onRewardedVideoCompleted() {
		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_REWARDED)) {
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_VIDEO_ENDED, new KrollDict());
		}
	}

	/*
	Banner	ca-app-pub-3940256099942544/6300978111
	Interstitial	ca-app-pub-3940256099942544/1033173712
	Interstitial Video	ca-app-pub-3940256099942544/8691691433
	Rewarded Video	ca-app-pub-3940256099942544/5224354917
	Native Advanced	ca-app-pub-3940256099942544/2247696110
	Native Advanced Video	ca-app-pub-3940256099942544/1044960115
	*/
}