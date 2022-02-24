package ti.android.admob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.util.TiConvert;

import ti.modules.titanium.ui.ButtonProxy;
import ti.modules.titanium.ui.ImageViewProxy;
import ti.modules.titanium.ui.LabelProxy;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;


@SuppressWarnings("deprecation")
public class AdmobView extends TiUIView implements OnUserEarnedRewardListener {

	private static final String TAG = "AdmobView";

	private AdView adaptativeAdView;
	private AdView appOpenAdView;
	private AdView adView;
	private InterstitialAd _interstitialAd;
	private RewardedAd _rewardedAd;
	private RewardedInterstitialAd rewardedInterstitialAd;

	NativeAd tempNativeAd;

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

	Bundle extras;

	private TiViewProxy contentad_media_proxy;
	private TiViewProxy master_view_proxy;
	private LabelProxy contentad_headline_proxy;
	private TiViewProxy contentad_stars_proxy;
	private LabelProxy contentad_store_proxy;
	private ImageViewProxy contentad_image_proxy;
	private LabelProxy contentad_price_proxy;
	private LabelProxy contentad_body_proxy;
	private ButtonProxy contentad_call_to_action_proxy;
	private ImageViewProxy contentad_logo_proxy;
	private LabelProxy contentad_advertiser_proxy;

	private RatingBar ratingBar;

	private String keyword;
	private String contentUrl;
	private String adType;

	static TiApplication appContext = TiApplication.getInstance();

	public AdmobView(TiViewProxy proxy) {

		super(proxy);

		Log.d(TAG, "Creating AdMob AdView");
		Log.d(TAG, ("AdmobModule.PUBLISHER_ID: " + AdmobModule.PUBLISHER_ID));
		Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.AD_UNIT_ID));

		myInflater = LayoutInflater.from((Context) this.proxy.getActivity());

		// Initialize the Mobile Ads SDK.
		// MobileAds.initialize(this.proxy.getActivity());
	}

	private void createAppOpenAdView(){
		Log.d(TAG, "createAppOpenAdView()");
		this.appOpenAdView = new AdView((Context) this.proxy.getActivity());

		Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.APP_OPEN_AD_UNIT_ID));
		this.appOpenAdView.setAdUnitId(AdmobModule.APP_OPEN_AD_UNIT_ID);

		AdRequest adRequest = new AdRequest.Builder().build();

		AppOpenAd.load(
			appContext,
			AdmobModule.APP_OPEN_AD_UNIT_ID,
			adRequest,
			AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
			new AppOpenAd.AppOpenAdLoadCallback() {
				private AppOpenAd appOpenAd;

				/**
				 * Called when an app open ad has loaded.
				 *
				 * @param ad the loaded app open ad.
				 */
				@Override
				public void onAdLoaded(AppOpenAd ad) {
					this.appOpenAd = ad;
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_RECEIVED, (Object) new KrollDict());
					}
				}

				/**
				 * Called when an app open ad has failed to load.
				 *
				 * @param loadAdError the error.
				 */
				@Override
				public void onAdFailedToLoad(LoadAdError loadAdError) {
					// Handle the error.
					String message = String.format("onAdFailedToLoad (%s)", loadAdError.toString());
					Log.d(TAG, message);

					if (AdmobView.this.proxy != null) {
						if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
							KrollDict errorCallback = new KrollDict();
							errorCallback.put("message", loadAdError.getMessage());
							errorCallback.put("code", loadAdError.getCode());
							errorCallback.put("cause", loadAdError.getCause());
							AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) errorCallback);
						}
					}
				}
			}
		);
	}

	private void createAdaptativeAdView() {

		Log.d(TAG, "createAdaptativeAdView()");
		this.adaptativeAdView = new AdView((Context) this.proxy.getActivity());

		Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.BANNER_AD_UNIT_ID));
		this.adaptativeAdView.setAdUnitId(AdmobModule.BANNER_AD_UNIT_ID);

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

		Bundle bundle = createAdRequestProperties();
		if (bundle.size() > 0) {
			Log.d(TAG, "extras.size() > 0 -- set ad properties");
			Log.d(TAG, bundle.toString());
			adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
		} else {
			Log.d(TAG, "extras.size() = 0 -- no NPA detected");
		}

		AdRequest adRequest = adRequestBuilder.build();

		// Step 2 - Determine the screen width (less decorations) to use for the ad
		// width.
		Display display = this.proxy.getActivity().getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float widthPixels = outMetrics.widthPixels;
		float density = outMetrics.density;

		int adWidth = (int) (widthPixels / density);
		
		// Step 3 - Get adaptive ad size and return for setting on the ad view.
		AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize((Context) this.proxy.getActivity(), adWidth);

		// Step 4 - Set the adaptive ad size on the ad view.
		this.adaptativeAdView.setAdSize(adSize);
		
		this.adaptativeAdView.setAdListener(new AdListener() {
			@Override
			public void onAdClicked() {
				Log.d(TAG, "onAdClicked()");
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLICKED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLICKED, (Object) new KrollDict());
					}
				} else {
					Log.d(TAG, "Will not fire AdmobModule.AD_CLICKED event! Proxy is null.");
				}
			}

			@Override
			public void onAdOpened() {
				Log.d(TAG, "onAdOpened()");
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_OPENED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_OPENED, (Object) new KrollDict());
					}
				} else {
                    Log.d(TAG, "Will not fire AdmobModule.AD_OPENED event! Proxy is null.");
                }
			}

			@Override
			public void onAdLoaded() {
				Log.d(TAG, "onAdLoaded()");
				if (AdmobView.this.proxy != null) {
					//Log.d(TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_RECEIVED, (Object) new KrollDict());
					}
				}
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				Log.d(TAG, ("onAdFailedToLoad(): " + getErrorReason(loadAdError.getCode())));
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
						KrollDict errorCallback = new KrollDict();
						errorCallback.put("cause", loadAdError.getCause());
						errorCallback.put("code", loadAdError.getCode());
						errorCallback.put("message", loadAdError.getMessage());
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) errorCallback);
					}
				}
			}
		});

		// Step 5 - Start loading the ad in the background.
		this.adaptativeAdView.loadAd(adRequest);
		this.setNativeView((View) this.adaptativeAdView);
	}

	//
	private void createAdView(String type, AdSize SIZE) {

		Log.d(TAG, "createAdView() " + type);
		this.adView = new AdView(this.proxy.getActivity());

		this.adView.setAdSize(SIZE);

		Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.AD_UNIT_ID));
		this.adView.setAdUnitId(AdmobModule.AD_UNIT_ID);

		AdManagerAdRequest.Builder AdRequestBuilder = new AdManagerAdRequest.Builder();

		Bundle bundle = createAdRequestProperties();
		if (bundle.size() > 0) {
			Log.d(TAG, "extras.size() > 0 -- set ad properties");
			AdRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
			// AdRequestBuilder.addNetworkExtras(new AdMobExtras(bundle));
		}

		if (keyword != null) {
			AdRequestBuilder.addKeyword(keyword);
		}

		if (contentUrl != null) {
			AdRequestBuilder.setContentUrl(contentUrl);
		}

		AdManagerAdRequest adRequest = AdRequestBuilder.build();

		this.adView.setAdListener(new AdListener() {
			@Override
			public void onAdClicked() {
				Log.d(TAG, "onAdClicked()");
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLICKED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLICKED, (Object) new KrollDict());
					}
				} else {
					Log.d(TAG, "Will not fire AdmobModule.AD_CLICKED event! Proxy is null.");
				}
			}

			@Override
			public void onAdOpened() {
				Log.d(TAG, "onAdOpened()");
				if (AdmobView.this.proxy != null) {
					//Log.d(TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_OPENED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_OPENED, (Object) new KrollDict());
					}
				}
			}

			@Override
			public void onAdClosed() {
				// Code to be executed when the user is about to return
				// to the app after tapping on an ad.
				Log.d(TAG, "onAdClosed()");
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLOSED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLOSED, (Object) new KrollDict());
					}
				}
			}

			@Override
			public void onAdLoaded() {
				Log.d(TAG, "onAdLoaded()");
				if (AdmobView.this.proxy != null) {
					Log.d(TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_RECEIVED, (Object) new KrollDict());
					}
				}
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				Log.d(TAG, ("onAdFailedToLoad(): " + getErrorReason(loadAdError.getCode())));
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
						KrollDict errorCallback = new KrollDict();
						errorCallback.put("cause", loadAdError.getCause());
						errorCallback.put("code", loadAdError.getCode());
						errorCallback.put("message", loadAdError.getMessage());
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) errorCallback);
					}
				}
			}
		});

		this.adView.loadAd(adRequest);
		this.adView.setPadding(this.prop_left, this.prop_top, this.prop_right, 0);
		this.setNativeView((View) this.adView);
	}

	// NATIVE ADS
	private void createNativeAds() {

		AdLoader.Builder builder = new AdLoader.Builder(this.proxy.getActivity(), AdmobModule.NATIVE_AD_UNIT_ID);

		frameLayout = new FrameLayout(this.proxy.getActivity());
		frameLayout.setLayoutParams(
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {

			@Override
			public void onNativeAdLoaded(NativeAd unifiedNativeAd) {
				// Show the ad.
				Log.d(TAG, "onContentAdLoaded()");

				if (master_view_proxy != null) {

					master_view = master_view_proxy.getOrCreateView().getOuterView();

					if (tempNativeAd != null) {
						tempNativeAd.destroy();
					}

					tempNativeAd = unifiedNativeAd;

					NativeAdView nativeAd = new NativeAdView(appContext);

					nativeAd.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));

					// nativeAd.setMinimumHeight(50);
					nativeAd.setBackgroundColor(native_ads_background_color);

					if (contentad_call_to_action_proxy != null) {
						contentad_call_to_action = (Button) contentad_call_to_action_proxy.getOrCreateView()
								.getOuterView();
					}

					if (contentad_headline_proxy != null) {
						contentad_headline = (TextView) contentad_headline_proxy.getOrCreateView().getOuterView();
					}

					if (contentad_body_proxy != null) {
						contentad_body = (TextView) contentad_body_proxy.getOrCreateView().getOuterView();
					}

					if (contentad_store_proxy != null) {
						contentad_store = (TextView) contentad_store_proxy.getOrCreateView().getOuterView();
					}

					if (contentad_price_proxy != null) {
						contentad_price = (TextView) contentad_price_proxy.getOrCreateView().getOuterView();
					}

					if (contentad_advertiser_proxy != null) {
						contentad_advertiser = (TextView) contentad_advertiser_proxy.getOrCreateView().getOuterView();
					}

					if (contentad_logo_proxy != null) {
						contentad_logo = (ViewGroup) contentad_logo_proxy.getOrCreateView().getOuterView();
						try {
							contentad_logo_view = (ImageView) contentad_logo.getChildAt(0);
						} catch (ClassCastException exc) {
							contentad_logo_view = (ImageView) ((ViewGroup) contentad_logo.getChildAt(0)).getChildAt(0);
						}
					}

					if (contentad_stars_proxy != null) {
						contentad_stars = (ViewGroup) contentad_stars_proxy.getOrCreateView().getOuterView();
						contentad_stars_view = (RatingBar) contentad_stars.getChildAt(0);
					}

					if (contentad_media_proxy != null) {
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

		VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

		NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

		builder.withNativeAdOptions(adOptions);

		AdLoader adLoader = builder.withAdListener(new AdListener() {

			@Override
			public void onAdOpened() {
				Log.d(TAG, "onAdOpened()");
				if (AdmobView.this.proxy != null) {
					//Log.d(TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_OPENED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_OPENED, (Object) new KrollDict());
					}
				}
			}

			@Override
			public void onAdClicked(){
				Log.d(TAG, "onAdClicked()");
				if (AdmobView.this.proxy != null) {
					//Log.d(TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLICKED)) {
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLICKED, (Object) new KrollDict());
					}
				}
			}

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
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				Log.d(TAG, ("onAdFailedToLoad(): " + getErrorReason(loadAdError.getCode())));
				if (AdmobView.this.proxy != null) {
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
						KrollDict errorCallback = new KrollDict();
						errorCallback.put("cause", loadAdError.getCause());
						errorCallback.put("code", loadAdError.getCode());
						errorCallback.put("message", loadAdError.getMessage());
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) errorCallback);
					}
				}
			}
		}).build();

		AdManagerAdRequest.Builder AdRequestBuilder = new AdManagerAdRequest.Builder();

		Bundle bundle = createAdRequestProperties();
		if (bundle.size() > 0) {
			Log.d(TAG, "extras.size() > 0 -- set ad properties");
			AdRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
			// AdRequestBuilder.addNetworkExtras(new AdMobExtras(bundle));
		}

		if (keyword != null) {
			AdRequestBuilder.addKeyword(keyword);
		}

		if (contentUrl != null) {
			AdRequestBuilder.setContentUrl(contentUrl);
		}

		AdManagerAdRequest AR = AdRequestBuilder.build();

		// AdRequest AR = new AdRequest.Builder()
		// .addKeyword(keyword)
		// .setContentUrl(contentUrl)
		// .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		// .addTestDevice(AdmobModule.TEST_DEVICE_ID)
		// .build();

		adLoader.loadAd(AR);

		AdmobView.this.setNativeView(frameLayout);
	}

	/**
	 * Populates a {@link NativeAdView} object with data from a given
	 * {@link NativeAd}.
	 *
	 * @param nativeAd
	 *            the object containing the ad's assets
	 * @param adView
	 *            the view to be populated
	 */
	private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
		// Set the media view. Media content will be automatically populated in the
		// media view once
		// adView.setNativeAd() is called.
		adView.setMediaView(contentad_media_view);

		// Set other ad assets.
		if (contentad_headline != null) {
			adView.setHeadlineView(contentad_headline);
		}
		if (contentad_body != null) {
			adView.setBodyView(contentad_body);
		}
		if (contentad_call_to_action != null) {
			adView.setCallToActionView(contentad_call_to_action);
		}
		if (contentad_logo_view != null) {
			adView.setIconView(contentad_logo_view);
		}
		if (contentad_price != null) {
			adView.setPriceView(contentad_price);
		}
		if (contentad_stars_view != null) {
			adView.setStarRatingView(contentad_stars_view);
		}
		if (contentad_store != null) {
			adView.setStoreView(contentad_store);
		}
		if (contentad_advertiser != null) {
			adView.setAdvertiserView(contentad_advertiser);
		}

		// The headline is guaranteed to be in every UnifiedNativeAd.
		((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

		// These assets aren't guaranteed to be in every UnifiedNativeAd, so it's
		// important to
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
			((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
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
			((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
			adView.getStarRatingView().setVisibility(View.VISIBLE);
		}

		if (nativeAd.getAdvertiser() == null) {
			adView.getAdvertiserView().setVisibility(View.GONE);
		} else {
			((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
			adView.getAdvertiserView().setVisibility(View.VISIBLE);
		}

		// This method tells the Google Mobile Ads SDK that you have finished populating
		// your
		// native ad view with this native ad. The SDK will populate the adView's
		// MediaView
		// with the media content from this native ad.
		adView.setNativeAd(nativeAd);

		// Get the video controller for the ad. One will always be provided, even if the
		// ad doesn't
		// have a video asset.
//		VideoController vc = nativeAd.getVideoController();
//
//		// Updates the UI to say whether or not this ad has a video asset.
//		if (vc.hasVideoContent()) {
//			// Create a new VideoLifecycleCallbacks object and pass it to the
//			// VideoController. The
//			// VideoController will call methods on this object when events occur in the
//			// video
//			// lifecycle.
//			vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//				@Override
//				public void onVideoEnd() {
//					// Publishers should allow native ads to complete video playback before
//					// refreshing or replacing them with another ad in the same UI location.
//					super.onVideoEnd();
//				}
//			});
//		}
	}

	// REWARDED INTERSTITAL AD
	private void createRewardedInterstitialView(){
		Log.d(TAG, "createRewardedInterstitialView()");
		requestRewardedInterstitialAd();
	}

	public void requestRewardedInterstitialAd(){
		RewardedInterstitialAd.load(appContext, AdmobModule.REWARDED_INTERSTITIAL_AD_UNIT_ID,
				new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
					@Override
					public void onAdLoaded(RewardedInterstitialAd ad) {
						rewardedInterstitialAd = ad;
						Log.e(TAG, "onAdLoaded");
						setRewardedInterstitialEvents();
						if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_LOADED)) {
							AdmobView.this.proxy.fireEvent(AdmobModule.AD_LOADED, (Object) new KrollDict());
						}
					}
					@Override
					public void onAdFailedToLoad(LoadAdError loadAdError) {
						Log.e(TAG, "onAdFailedToLoad");
						rewardedInterstitialAd = null;
						KrollDict rewardedError = new KrollDict();
						rewardedError.put("cause", loadAdError.getCause());
						rewardedError.put("code", loadAdError.getCode());
						rewardedError.put("message", loadAdError.getMessage());
						if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
							AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) rewardedError);
						}
					}
				});
	}

	private void setRewardedInterstitialEvents(){
		rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
			/** Called when the ad failed to show full screen content. */
			@Override
			public void onAdFailedToShowFullScreenContent(AdError adError) {
				Log.i(TAG, "onAdFailedToShowFullScreenContent");
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
					KrollDict rewardedError = new KrollDict();
					rewardedError.put("cause", adError.getCause());
					rewardedError.put("code", adError.getCode());
					rewardedError.put("message", adError.getMessage());
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_FAILED_TO_SHOW, (Object) rewardedError);
				}
			}

			/** Called when ad showed the full screen content. */
			@Override
			public void onAdShowedFullScreenContent() {
				Log.i(TAG, "onAdShowedFullScreenContent");
				rewardedInterstitialAd = null;
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT)) {
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT, (Object) new KrollDict());
				}
			}

			/** Called when full screen content is dismissed. */
			@Override
			public void onAdDismissedFullScreenContent() {
				Log.i(TAG, "onAdDismissedFullScreenContent");
				rewardedInterstitialAd = null;
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLOSED)) {
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLOSED, (Object) new KrollDict());
				}
			}
		});
	}

	@Override
	public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
		Log.i(TAG, "onUserEarnedReward");
		if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_REWARDED)) {
			KrollDict errorCallback = new KrollDict();
			errorCallback.put("amount", rewardItem.getAmount());
			errorCallback.put("type", rewardItem.getType());
			AdmobView.this.proxy.fireEvent(AdmobModule.AD_REWARDED, (Object) errorCallback);
		}
	}

	public void showRewardedInterstitialAd(){
		rewardedInterstitialAd.show(/* Activity */ this.proxy.getActivity(),/*
    OnUserEarnedRewardListener */ (OnUserEarnedRewardListener) this.proxy.getActivity());
	}

	// REWARDED AD
	private void createRewardedAdView() {
		Log.d(TAG, "createRewardedAdView()");
		requestNewRewardedAd();
	}

	public void requestNewRewardedAd(){

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

		Bundle bundle = createAdRequestProperties();
		if (bundle.size() > 0) {
			Log.d(TAG, "extras.size() > 0 -- set ad properties");
			Log.d(TAG, bundle.toString());
			adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
		}

		AdRequest adRequest = adRequestBuilder.build();

		RewardedAd.load(appContext, AdmobModule.REWARDED_AD_UNIT_ID,
				adRequest, new RewardedAdLoadCallback(){
					@Override
					public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
						// Handle the error.
						Log.d(TAG, loadAdError.getMessage());
						_rewardedAd = null;
						KrollDict rewardedError = new KrollDict();
						rewardedError.put("cause", loadAdError.getCause());
						rewardedError.put("code", loadAdError.getCode());
						rewardedError.put("message", loadAdError.getMessage());
						if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
							AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) rewardedError);
						}
					}

					@Override
					public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
						_rewardedAd = rewardedAd;
						setRewardedEvents();
						Log.d(TAG, "onAdLoaded");
						if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_LOADED)) {
							AdmobView.this.proxy.fireEvent(AdmobModule.AD_LOADED, (Object) new KrollDict());
						}
					}
				});
	}

	private void setRewardedEvents(){
		_rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
			@Override
			public void onAdShowedFullScreenContent() {
				// Called when ad is shown.
				Log.d(TAG, "Ad was shown.");
				_rewardedAd = null;
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT)) {
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT, (Object) new KrollDict());
				}
			}

			@Override
			public void onAdFailedToShowFullScreenContent(AdError adError) {
				// Called when ad fails to show.
				Log.d(TAG, "Ad failed to show.");
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
					KrollDict rewardedError = new KrollDict();
					rewardedError.put("cause", adError.getCause());
					rewardedError.put("code", adError.getCode());
					rewardedError.put("message", adError.getMessage());
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_FAILED_TO_SHOW, (Object) rewardedError);
				}
			}

			@Override
			public void onAdDismissedFullScreenContent() {
				// Called when ad is dismissed.
				// Don't forget to set the ad reference to null so you
				// don't show the ad a second time.
				Log.d(TAG, "Ad was dismissed.");
				_rewardedAd = null;
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLOSED)) {
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLOSED, (Object) new KrollDict());
				}
			}
		});
	}

	public void showRewardedAd() {
		if (_rewardedAd != null) {
			_rewardedAd.show(this.proxy.getActivity(), new OnUserEarnedRewardListener() {
				@Override
				public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
					// Handle the reward.
					Log.d("TAG", "The user earned the reward.");
					if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_REWARDED)) {
						KrollDict errorCallback = new KrollDict();
						errorCallback.put("amount", rewardItem.getAmount());
						errorCallback.put("type", rewardItem.getType());
						AdmobView.this.proxy.fireEvent(AdmobModule.AD_REWARDED, (Object) errorCallback);
					}
				}
			});
		} else {
			Log.d("TAG", "The rewarded ad wasn't ready yet.");
		}
	}

	// INTERSTITAL AD
	private void createInterstitialAdView() {
		Log.d(TAG, "createInterstitialAdView()");
		requestNewInterstitialAd();
	}

	public void requestNewInterstitialAd(){

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

		Bundle bundle = createAdRequestProperties();
		if (bundle.size() > 0) {
			Log.d(TAG, "extras.size() > 0 -- set ad properties");
			Log.d(TAG, bundle.toString());
			adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
		}

		AdRequest adRequest = adRequestBuilder.build();

		Log.d(TAG, AdmobModule.INTERSTITIAL_AD_UNIT_ID);

		InterstitialAd.load(appContext, AdmobModule.INTERSTITIAL_AD_UNIT_ID, adRequest, new InterstitialAdLoadCallback() {
			@Override
			public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
				// The mInterstitialAd reference will be null until
				// an ad is loaded.
				_interstitialAd = interstitialAd;
				setInterstitialEvents();
				Log.i(TAG, "onAdLoaded");
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_LOADED)) {
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_LOADED, (Object) new KrollDict());
				}
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				// Handle the error
				_interstitialAd = null;
				Log.i(TAG, loadAdError.getMessage());
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
					KrollDict errorCallback = new KrollDict();
					errorCallback.put("cause", loadAdError.getCause());
					errorCallback.put("code", loadAdError.getCode());
					errorCallback.put("message", loadAdError.getMessage());
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, (Object) errorCallback);
				}
			}
		});
	}

	private void setInterstitialEvents() {
		_interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
			@Override
			public void onAdShowedFullScreenContent() {
				// Called when ad is shown.
				Log.d(TAG, "Ad was shown.");
				_interstitialAd = null;
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT)) {
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT, (Object) new KrollDict());
				}
			}

			@Override
			public void onAdFailedToShowFullScreenContent(AdError adError) {
				// Called when ad fails to show.
				Log.d(TAG, "Ad failed to show.");
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
					KrollDict rewardedError = new KrollDict();
					rewardedError.put("cause", adError.getCause());
					rewardedError.put("code", adError.getCode());
					rewardedError.put("message", adError.getMessage());
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_FAILED_TO_SHOW, (Object) rewardedError);
				}
			}

			@Override
			public void onAdDismissedFullScreenContent() {
				// Called when ad is dismissed.
				// Don't forget to set the ad reference to null so you
				// don't show the ad a second time.
				Log.d(TAG, "Ad was dismissed.");
				_interstitialAd = null;
				if (AdmobView.this.proxy.hasListeners(AdmobModule.AD_CLOSED)) {
					AdmobView.this.proxy.fireEvent(AdmobModule.AD_CLOSED, (Object) new KrollDict());
				}
			}
		});
	}

	public void showInterstitialAd() {
		if (_interstitialAd != null) {
			_interstitialAd.show(this.proxy.getActivity());
		} else {
			Log.d("TAG", "The interstitial ad wasn't ready yet.");
		}
	}

	private void createRatingView() {

		Log.d(TAG, "createRatingView()");

		LinearLayout layout = new LinearLayout((Context) proxy.getActivity());
		layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		ratingBar = new RatingBar((Context) proxy.getActivity());
		ratingBar.setNumStars(5);
		ratingBar.setRating(0);

		layout.addView(ratingBar);

		this.setNativeView((View) layout);
	}

	private void createMediaView() {
		Log.d(TAG, "createMediaView()");

		MediaView mediaView = new MediaView((Context) proxy.getActivity());
		this.setNativeView((View) mediaView);
	}

	public void destroy() {
		this.proxy.getActivity().runOnUiThread(new Runnable() {
			public void run() {

				Log.d(TAG, "destroy");

				if (adView != null) {
					adView.destroy();
				}

				if (_interstitialAd != null) {
					_interstitialAd = null;
				}

				if (_rewardedAd != null) {
					_rewardedAd = null;
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

			if (view_type.equals(AdmobModule.TYPE_STARS)) {

				Log.d(TAG, ("view_type = " + view_type) + " createRatingView()");

				createRatingView();
			} else if (view_type.equals(AdmobModule.TYPE_MEDIA)) {

				Log.d(TAG, ("view_type = " + view_type) + " createMediaView()");

				createMediaView();
			} else if (view_type.equals(AdmobModule.TYPE_ADS)) {

				Log.d(TAG, ("view_type = " + view_type) + " searching p");

				if (d.containsKey(AdmobModule.EXTRA_BUNDLE)) {
					Log.d(TAG, "Has extras");
					extras = mapToBundle(d.getKrollDict("extras"));
				}

				if (d.containsKey(AdmobModule.MASTER_VIEW)) {
					Object view = d.get(AdmobModule.MASTER_VIEW);
					if (view != null && view instanceof TiViewProxy) {
						if (view instanceof TiWindowProxy) {
							throw new IllegalStateException("[ERROR] Cannot use window as AdmobView view");
						}
						Log.d(TAG, "[SUCESS] type for master_view is TiViewProxy");
						master_view_proxy = (TiViewProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for master_view");
					}
				}

				if (d.containsKey(AdmobModule.IMAGE_VIEW)) {
					Object view = d.get(AdmobModule.IMAGE_VIEW);
					if (view != null && view instanceof ImageViewProxy) {
						Log.d(TAG, "[SUCESS] type for contentad_image is ImageViewProxy");
						contentad_image_proxy = (ImageViewProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for imageView");
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

					this.createAdView(adType, null);
				}

				if (d.containsKey((Object) AdmobModule.NATIVE_ADS_BACKGROUND_COLOR)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG: " + d.getString(AdmobModule.NATIVE_ADS_BACKGROUND_COLOR)));
					this.native_ads_background_color = TiColorHelper
							.parseColor(d.getString(AdmobModule.NATIVE_ADS_BACKGROUND_COLOR));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_BG)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG: " + d.getString(AdmobModule.PROPERTY_COLOR_BG)));
					this.prop_color_bg = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_BG_TOP)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG_TOP: " + d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP)));
					this.prop_color_bg_top = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_BORDER)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BORDER: " + d.getString(AdmobModule.PROPERTY_COLOR_BORDER)));
					this.prop_color_border = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BORDER));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_TEXT)) {
					Log.d(TAG, ("has PROPERTY_COLOR_TEXT: " + d.getString(AdmobModule.PROPERTY_COLOR_TEXT)));
					this.prop_color_text = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_TEXT));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_LINK)) {
					Log.d(TAG, ("has PROPERTY_COLOR_LINK: " + d.getString(AdmobModule.PROPERTY_COLOR_LINK)));
					this.prop_color_link = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_LINK));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_URL)) {
					Log.d(TAG, ("has PROPERTY_COLOR_URL: " + d.getString(AdmobModule.PROPERTY_COLOR_URL)));
					this.prop_color_url = this.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_URL));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED)) {
					Log.d(TAG, ("has PROPERTY_COLOR_TEXT_DEPRECATED: "
							+ d.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED)));
					this.prop_color_text = this
							.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_TEXT_DEPRECATED));
				}

				if (d.containsKey((Object) AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED)) {
					Log.d(TAG, ("has PROPERTY_COLOR_LINK_DEPRECATED: "
							+ d.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED)));
					this.prop_color_link = this
							.convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_LINK_DEPRECATED));
				}

				if (d.containsKey((Object) AdmobModule.CONTENT_URL)) {
					Log.d(TAG, ("has CONTENT_URL: " + d.getString(AdmobModule.CONTENT_URL)));
					contentUrl = d.getString(AdmobModule.CONTENT_URL);
				}

				if (d.containsKey((Object) AdmobModule.KEYWORD)) {
					Log.d(TAG, ("has CONTENT_URL: " + d.getString(AdmobModule.KEYWORD)));
					keyword = d.getString(AdmobModule.KEYWORD);
				}

				if (d.containsKey(AdmobModule.AD_SIZE_TYPE)) {

					Log.d(TAG, ("has AD_SIZE_TYPE: " + d.getString(AdmobModule.AD_SIZE_TYPE)));

					adType = d.getString(AdmobModule.AD_SIZE_TYPE);

					if (adType.equals(AdmobModule.BANNER)) {
						Log.w(TAG, ("BANNER ADS are deprecated! You show use ADAPTATIVE_BANNER instead"));
						AdmobModule.BANNER_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						this.createAdView(adType, AdSize.BANNER);
					} else if(adType.equals(AdmobModule.ADAPTATIVE_BANNER)) {
						AdmobModule.BANNER_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						createAdaptativeAdView();
					} else if(adType.equals(AdmobModule.OPEN_APP)) {
						AdmobModule.APP_OPEN_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						createAppOpenAdView();
					} else if (adType.equals(AdmobModule.REWARDED)) {
						AdmobModule.REWARDED_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						this.createRewardedAdView();
					} else if (adType.equals(AdmobModule.REWARDED_INTERSTITIAL)) {
						AdmobModule.REWARDED_INTERSTITIAL_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						this.createRewardedInterstitialView();
					} else if (adType.equals(AdmobModule.MEDIUM_RECTANGLE)) {
						this.createAdView(adType, AdSize.MEDIUM_RECTANGLE);
					} else if (adType.equals(AdmobModule.FULL_BANNER)) {
						this.createAdView(adType, AdSize.FULL_BANNER);
					} else if (adType.equals(AdmobModule.LEADERBOARD)) {
						this.createAdView(adType, AdSize.LEADERBOARD);
					} else if (adType.equals(AdmobModule.SMART_BANNER)) {
						this.createAdView(adType, AdSize.SMART_BANNER);
					} else if (adType.equals(AdmobModule.INTERSTITIAL)) {
						AdmobModule.INTERSTITIAL_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						this.createInterstitialAdView();
					} else if (adType.equals(AdmobModule.NATIVE_ADS)) {
						AdmobModule.NATIVE_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						this.createNativeAds();
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
					Log.w(TAG, "No ad_size_type defined.");
				}
			} else {
				Log.d(TAG, "viewType exists but is not Media, Ads or Stars");
			}
		} else {
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

	private Bundle createAdRequestProperties() {
		Bundle bundle = new Bundle();
		if (prop_color_bg != null) {
			Log.d(TAG, "color_bg: " + prop_color_bg);
			bundle.putString("color_bg", prop_color_bg);
		}
		if (prop_color_bg_top != null)
			bundle.putString("color_bg_top", prop_color_bg_top);
		if (prop_color_border != null)
			bundle.putString("color_border", prop_color_border);
		if (prop_color_text != null)
			bundle.putString("color_text", prop_color_text);
		if (prop_color_link != null)
			bundle.putString("color_link", prop_color_link);
		if (prop_color_url != null)
			bundle.putString("color_url", prop_color_url);
		if (extras != null)
			bundle.putAll(extras);

		return bundle;
	}

	private Bundle mapToBundle(Map<String, Object> map) {
		if (map == null) {
			return new Bundle();
		}

		Bundle bundle = new Bundle(map.size());

		for (String key : map.keySet()) {
			Object val = map.get(key);
			if (val == null) {
				bundle.putString(key, null);
			} else if (val instanceof TiBlob) {
				bundle.putByteArray(key, ((TiBlob) val).getBytes());
			} else if (val instanceof TiBaseFile) {
				try {
					bundle.putByteArray(key, ((TiBaseFile) val).read().getBytes());
				} catch (IOException e) {
					Log.e(TAG, "Unable to put '" + key + "' value into bundle: " + e.getLocalizedMessage(), e);
				}
			} else {
				bundle.putString(key, TiConvert.toString(val));
			}
		}

		return bundle;
	}

	/**
	 * Populates a {@link UnifiedNativeAdView} object with data from a given
	 * {@link UnifiedNativeAd}.
	 *
	 * @param nativeAd
	 *            the object containing the ad's assets
	 * @param adView
	 *            the view to be populated
	 */

	/*
	 * ca-app-pub-3940256099942544/6300978111 Interstitial
	 * ca-app-pub-3940256099942544/1033173712 Interstitial Video
	 * ca-app-pub-3940256099942544/8691691433 Rewarded Video
	 * ca-app-pub-3940256099942544/5224354917 Native Advanced
	 * ca-app-pub-3940256099942544/2247696110 Native Advanced Video
	 * ca-app-pub-3940256099942544/1044960115 Banner
	 * ca-app-pub-3940256099942544/3419835294 Open App
	 */
}