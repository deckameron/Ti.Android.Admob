package ti.android.admob;

import java.io.IOException;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.view.TiBorderWrapperView;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.util.TiConvert;

import ti.modules.titanium.ui.ButtonProxy;
import ti.modules.titanium.ui.ImageViewProxy;
import ti.modules.titanium.ui.LabelProxy;

import android.os.Bundle;
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
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaAspectRatio;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.ads.mediation.admob.AdMobAdapter;


@SuppressWarnings("deprecation")
public class NativeAdView extends TiUIView {

	private static final String TAG = "AdmobView";

	NativeAd tempNativeAd;

	LayoutInflater myInflater;

	int native_ads_background_color;

	FrameLayout frameLayout;

	private ViewGroup contentad_stars;
	private RatingBar contentad_stars_view;

	private ViewGroup contentad_logo;
	private ViewGroup image_logo;
	private ImageView contentad_logo_view;
	private ImageView image_logo_view;

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
	private ImageViewProxy content_ad_image_logo;
	private LabelProxy contentad_advertiser_proxy;

	private RatingBar ratingBar;

	private String keyword;
	private String contentUrl;
	private String adType;

	public NativeAdView(TiViewProxy proxy) {
		super(proxy);
		Log.d(TAG, "Creating NativeAdView");
		myInflater = LayoutInflater.from(proxy.getActivity());
	}

	// NATIVE ADS
	private void createNativeAds() {
		AdLoader.Builder builder = new AdLoader.Builder(proxy.getActivity(),
				AdmobModule.NATIVE_AD_UNIT_ID);

		frameLayout = new FrameLayout(proxy.getActivity());
		frameLayout.setLayoutParams(
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));

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

					if (proxy == null) {
						return;
					}
					com.google.android.gms.ads.nativead.NativeAdView nativeAd = new com.google.android.gms.ads.nativead.NativeAdView(proxy.getActivity());

					nativeAd.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));

					nativeAd.setBackgroundColor(native_ads_background_color);

					if (contentad_call_to_action_proxy != null) {
						if (contentad_call_to_action_proxy.getOrCreateView().getOuterView() instanceof TiBorderWrapperView){
							contentad_call_to_action = (Button) contentad_call_to_action_proxy.getOrCreateView().getNativeView();
						} else {
							contentad_call_to_action = (Button) contentad_call_to_action_proxy.getOrCreateView().getOuterView();
						}
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

					if (content_ad_image_logo != null) {
						image_logo = (ViewGroup) content_ad_image_logo.getOrCreateView().getOuterView();
						try {
							image_logo_view = (ImageView) image_logo.getChildAt(0);
						} catch (ClassCastException exc) {
							image_logo_view = (ImageView) ((ViewGroup) image_logo.getChildAt(0)).getChildAt(0);
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

					fireEvent(AdmobModule.AD_LOADED, new KrollDict());
				} else {
					Log.e(TAG, "No master_view defined!");
				}
			}
		});

		VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

		NativeAdOptions adOptions = new NativeAdOptions.Builder()
				.setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
				.setVideoOptions(videoOptions).build();

		builder.withNativeAdOptions(adOptions);

		AdLoader adLoader = builder.withAdListener(new AdListener() {

			@Override
			public void onAdOpened() {
				Log.d(TAG, "onAdOpened()");
				if (proxy != null) {
					if (proxy.hasListeners(AdmobModule.AD_OPENED)) {
						proxy.fireEvent(AdmobModule.AD_OPENED, new KrollDict());
					}
				}
			}

			@Override
			public void onAdClicked(){
				Log.d(TAG, "onAdClicked()");
				if (proxy != null) {
					if (proxy.hasListeners(AdmobModule.AD_CLICKED)) {
						proxy.fireEvent(AdmobModule.AD_CLICKED, new KrollDict());
					}
				}
			}

			@Override
			public void onAdLoaded() {
				Log.d(TAG, "onAdLoaded()");
				if (proxy != null) {
					if (proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
						proxy.fireEvent(AdmobModule.AD_RECEIVED, new KrollDict());
					}
				}
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				Log.d(TAG, ("onAdFailedToLoad(): " + AdmobModule.getErrorReason(loadAdError.getCode())));
				if (proxy != null) {
					if (proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
						KrollDict errorCallback = new KrollDict();
						errorCallback.put("cause", loadAdError.getCause());
						errorCallback.put("code", loadAdError.getCode());
						errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode()));
						errorCallback.put("message", loadAdError.getMessage());
						proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, errorCallback);
					}
				}
			}
		}).build();

		AdManagerAdRequest.Builder AdRequestBuilder = new AdManagerAdRequest.Builder();

		Bundle bundle = AdmobModule.createAdRequestProperties();
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

		adLoader.loadAd(AR);

		setNativeView(frameLayout);
	}

	/**
	 * Populates a {@link com.google.android.gms.ads.nativead.NativeAdView} object with data from a given
	 * {@link NativeAd}.
	 *
	 * @param nativeAd
	 *            the object containing the ad's assets
	 * @param adView
	 *            the view to be populated
	 */
	private void populateUnifiedNativeAdView(NativeAd nativeAd, com.google.android.gms.ads.nativead.NativeAdView adView) {
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
		if (image_logo_view != null) {
			adView.setImageView(image_logo_view);
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

		if (contentad_call_to_action != null) {
			if (nativeAd.getCallToAction() == null) {
				adView.getCallToActionView().setVisibility(View.GONE);
			} else {
				adView.getCallToActionView().setVisibility(View.VISIBLE);
				((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
			}
		}

		if (contentad_logo_view != null) {
			if (nativeAd.getIcon() == null) {
				adView.getIconView().setVisibility(View.GONE);
			} else {
				((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
				adView.getIconView().setVisibility(View.VISIBLE);
			}
		}

		if (image_logo_view != null) {
			image_logo_view.setImageDrawable(nativeAd.getIcon().getDrawable());
		}

		if (contentad_price_proxy != null) {
			if (nativeAd.getPrice() == null) {
				adView.getPriceView().setVisibility(View.GONE);
			} else {
				adView.getPriceView().setVisibility(View.VISIBLE);
				((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
			}
		}

		if (contentad_store_proxy != null) {
			if (nativeAd.getStore() == null) {
				adView.getStoreView().setVisibility(View.GONE);
			} else {
				adView.getStoreView().setVisibility(View.VISIBLE);
				((TextView) adView.getStoreView()).setText(nativeAd.getStore());
			}
		}

		if (contentad_stars_proxy != null) {
			if (nativeAd.getStarRating() == null) {
				adView.getStarRatingView().setVisibility(View.GONE);
			} else {
				((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
				adView.getStarRatingView().setVisibility(View.VISIBLE);
			}
		}

		if (contentad_advertiser_proxy != null) {
			if (nativeAd.getAdvertiser() == null) {
				adView.getAdvertiserView().setVisibility(View.GONE);
			} else {
				((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
				adView.getAdvertiserView().setVisibility(View.VISIBLE);
			}
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

	// OTHER VIEWS
	private void createRatingView() {

		Log.d(TAG, "createRatingView()");

		LinearLayout layout = new LinearLayout(proxy.getActivity());
		layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		ratingBar = new RatingBar(proxy.getActivity());
		ratingBar.setNumStars(5);
		ratingBar.setRating(0);

		layout.addView(ratingBar);

		setNativeView(layout);
	}

	private void createMediaView() {
		Log.d(TAG, "createMediaView()");

		MediaView mediaView = new MediaView(proxy.getActivity());
		setNativeView(mediaView);
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
					AdmobModule.extras = mapToBundle(d.getKrollDict("extras"));
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
				if (d.containsKey(AdmobModule.IMAGE_LOGO)) {
					Object view = d.get(AdmobModule.IMAGE_LOGO);
					if (view != null && view instanceof ImageViewProxy) {
						Log.d(TAG, "[SUCESS] type for content_ad_image_logo is ImageViewProxy");
						content_ad_image_logo = (ImageViewProxy) view;
					} else {
						Log.d(TAG, "[ERROR] Invalid type for content_ad_image_logo");
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

				if (d.containsKey(AdmobModule.NATIVE_ADS_BACKGROUND_COLOR)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG: " + d.getString(AdmobModule.NATIVE_ADS_BACKGROUND_COLOR)));
					native_ads_background_color = TiColorHelper
							.parseColor(d.getString(AdmobModule.NATIVE_ADS_BACKGROUND_COLOR));
				}

				if (d.containsKey(AdmobModule.PROPERTY_COLOR_BG)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG: " + d.getString(AdmobModule.PROPERTY_COLOR_BG)));
					AdmobModule.prop_color_bg = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG));
				}

				if (d.containsKey(AdmobModule.PROPERTY_COLOR_BG_TOP)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BG_TOP: " + d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP)));
					AdmobModule.prop_color_bg_top = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BG_TOP));
				}

				if (d.containsKey(AdmobModule.PROPERTY_COLOR_BORDER)) {
					Log.d(TAG, ("has PROPERTY_COLOR_BORDER: " + d.getString(AdmobModule.PROPERTY_COLOR_BORDER)));
					AdmobModule.prop_color_border = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_BORDER));
				}

				if (d.containsKey(AdmobModule.PROPERTY_COLOR_TEXT)) {
					Log.d(TAG, ("has PROPERTY_COLOR_TEXT: " + d.getString(AdmobModule.PROPERTY_COLOR_TEXT)));
					AdmobModule.prop_color_text = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_TEXT));
				}

				if (d.containsKey(AdmobModule.PROPERTY_COLOR_LINK)) {
					Log.d(TAG, ("has PROPERTY_COLOR_LINK: " + d.getString(AdmobModule.PROPERTY_COLOR_LINK)));
					AdmobModule.prop_color_link = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_LINK));
				}

				if (d.containsKey(AdmobModule.PROPERTY_COLOR_URL)) {
					Log.d(TAG, ("has PROPERTY_COLOR_URL: " + d.getString(AdmobModule.PROPERTY_COLOR_URL)));
					AdmobModule.prop_color_url = convertColorProp(d.getString(AdmobModule.PROPERTY_COLOR_URL));
				}

				if (d.containsKeyAndNotNull("contentUrl")) {
					Log.d(TAG, ("has CONTENT_URL: " + d.getString("contentUrl")));
					contentUrl = d.getString("contentUrl");
				}

				if (d.containsKeyAndNotNull("keyword")) {
					Log.d(TAG, ("has KEYWORD: " + d.getString("keyword")));
					keyword = d.getString("keyword");
				}

				if (d.containsKey(AdmobModule.AD_SIZE_TYPE)) {

					Log.d(TAG, ("has AD_SIZE_TYPE: " + d.getString(AdmobModule.AD_SIZE_TYPE)));

					adType = d.getString(AdmobModule.AD_SIZE_TYPE);

					if (AdmobModule.INIT_READY == false){
						Log.w(TAG, ("Admob is not ready yet!"));
						proxy.fireEvent(AdmobModule.ADMOB_NOT_READY_YET, new KrollDict());
					} else if (adType.equals(AdmobModule.NATIVE_ADS)) {
						if (AdmobModule.AD_UNIT_ID != null) {
							AdmobModule.NATIVE_AD_UNIT_ID = AdmobModule.AD_UNIT_ID;
						}
						createNativeAds();
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
}