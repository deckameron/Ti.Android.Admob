package ti.android.admob;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

public class AdaptativeBannerView extends TiUIView {

    private static final String TAG = "AdaptativeBannerAd";

    private AdView adaptativeAdView;

    private String keyword;
    private String contentUrl;

    public AdaptativeBannerView(TiViewProxy proxy) {
        super(proxy);
        Log.d(TAG, "Creating AdaptativeBanner AdView");
    }

    private void createAdaptativeAdView() {

        Log.d(TAG, "createAdaptativeAdView()");
        adaptativeAdView = new AdView(proxy.getActivity());

        Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.BANNER_AD_UNIT_ID));
        adaptativeAdView.setAdUnitId(AdmobModule.BANNER_AD_UNIT_ID);

        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        int adWidth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = proxy.getActivity().getWindowManager().getCurrentWindowMetrics();
            Rect bounds = windowMetrics.getBounds();

            float adWidthPixels = adaptativeAdView.getWidth();

            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = bounds.width();
            }

            float density = proxy.getActivity().getResources().getDisplayMetrics().density;
            adWidth = (int) (adWidthPixels / density);
            Log.d(TAG, "VERSION_CODES.R");
            Log.d(TAG, "ADAPTATIVE adWidth = " + adWidth);

        } else {
            Display display = proxy.getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;

            adWidth = (int) (widthPixels / density);
            Log.d(TAG, "ADAPTATIVE adWidth = " + adWidth);
        }

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(proxy.getActivity(), adWidth);
        Log.d(TAG, "adSize: " + adSize);

        // Step 4 - Set the adaptive ad size on the ad view.
        // adaptativeAdView.setAdSize(new AdSize(adWidth, 65));
        adaptativeAdView.setAdSize(adSize);

        // Step 5 - Set the adaptive event listeners.
        adaptativeAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked()");
                if (proxy != null) {
                    if (proxy.hasListeners(AdmobModule.AD_CLICKED)) {
                        proxy.fireEvent(AdmobModule.AD_CLICKED, new KrollDict());
                    }
                } else {
                    Log.d(TAG, "Will not fire AdmobModule.AD_CLICKED event! Proxy is null.");
                }
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened()");
                if (proxy != null) {
                    if (proxy.hasListeners(AdmobModule.AD_OPENED)) {
                        proxy.fireEvent(AdmobModule.AD_OPENED, new KrollDict());
                    }
                } else {
                    Log.d(TAG, "Will not fire AdmobModule.AD_OPENED event! Proxy is null.");
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded()");
                if (proxy != null) {
                    // Log.d(TAG, "onAdLoaded() " + adView.getWidth() + ", " + adView.getHeight());
                    if (proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
                        proxy.fireEvent(AdmobModule.AD_RECEIVED, new KrollDict());
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, ("onAdFailedToLoad(): " + AdmobModule.getErrorReason(loadAdError.getCode())));
                if (proxy != null) {
                    if (proxy.hasListeners(AdmobModule.AD_FAILED_TO_LOAD)) {
                        KrollDict errorCallback = new KrollDict();
                        errorCallback.put("cause", loadAdError.getCause());
                        errorCallback.put("code", loadAdError.getCode());
                        errorCallback.put("message", loadAdError.getMessage());
                        errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode()));
                        errorCallback.put("responseInfo", loadAdError.getResponseInfo().toString());
                        proxy.fireEvent(AdmobModule.AD_FAILED_TO_LOAD, errorCallback);
                    }
                }
            }
        });

        // Step 6 - Convert to Titanium Native View.
        setNativeView(adaptativeAdView);

        // Step 7 - Build and request the Ad.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        if (keyword != null) {
            adRequestBuilder.addKeyword(keyword);
        }

        if (contentUrl != null) {
            adRequestBuilder.setContentUrl(contentUrl);
        }

        Bundle bundle = AdmobModule.createAdRequestProperties();
        if (bundle.size() > 0) {
            Log.d(TAG, "extras.size() > 0 -- set ad properties");
            Log.d(TAG, bundle.toString());
            adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
        } else {
            Log.d(TAG, "extras.size() = 0 -- no NPA detected");
        }

        AdRequest adRequest = adRequestBuilder.build();
        Log.d(TAG, "Is test device? " + adRequest.isTestDevice(proxy.getActivity()));

        // Step 8 - Start loading the ad in the background.
        adaptativeAdView.loadAd(adRequest);
    }

    @Override
    public void processProperties(KrollDict d) {
        super.processProperties(d);
        Log.d(TAG, "Process properties");

        if(d.containsKeyAndNotNull("adUnitId")){
            AdmobModule.BANNER_AD_UNIT_ID = d.getString("adUnitId");
        }

        if (d.containsKeyAndNotNull("contentUrl")) {
            Log.d(TAG, ("has CONTENT_URL: " + d.getString("contentUrl")));
            contentUrl = d.getString("contentUrl");
        }

        if (d.containsKeyAndNotNull("keyword")) {
            Log.d(TAG, ("has KEYWORD: " + d.getString("keyword")));
            keyword = d.getString("keyword");
        }

        // Create the Banner
        createAdaptativeAdView();
    };

    public void pause()
    {
        Log.d(TAG, "pause");
        adaptativeAdView.pause();
    }

    public void resume()
    {
        Log.d(TAG, "resume");
        adaptativeAdView.resume();
    }

    public void destroy()
    {
        Log.d(TAG, "destroy");
        adaptativeAdView.destroy();
    }
}
