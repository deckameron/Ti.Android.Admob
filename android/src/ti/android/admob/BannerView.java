package ti.android.admob;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.banner.AdSize;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

public class BannerView extends TiUIView {

    private static final String TAG = "BannerView";

    private AdView bannerAdView;

    private String keyword;
    private String contentUrl;

    int prop_top;
    int prop_left;
    int prop_right;

    public BannerView(TiViewProxy proxy) {
        super(proxy);
        Log.d(TAG, "Creating Banner AdView");
    }

    // ADVIEW
    private void createSmartBannerView(AdSize SIZE) {

        Log.d(TAG, "createBannerView()");
        bannerAdView = new AdView(proxy.getActivity());

        Log.d(TAG, ("AdmobModule.BANNER_AD_UNIT_ID: " + AdmobModule.BANNER_AD_UNIT_ID));

        if (keyword != null || contentUrl != null) {
            Log.w(TAG, "The 'keyword' and 'contentUrl' properties are no longer supported by " +
                    "the GMA Next-Gen SDK's BannerAdRequest and will be ignored.");
        }

        BannerAdRequest.Builder adRequestBuilder =
                new BannerAdRequest.Builder(AdmobModule.BANNER_AD_UNIT_ID, SIZE);

        Bundle bundle = AdmobModule.createAdRequestProperties();
        if (!bundle.isEmpty()) {
            Log.d(TAG, "Has extras -- Setting Ad properties");
            adRequestBuilder.setGoogleExtrasBundle(bundle);
        }

        BannerAdRequest adRequest = adRequestBuilder.build();

        bannerAdView.setPadding(prop_left, prop_top, prop_right, 0);
        setNativeView(bannerAdView);

        bannerAdView.loadAd(adRequest, new AdLoadCallback<BannerAd>() {
            @Override
            public void onAdLoaded(@NonNull BannerAd ad) {
                Log.d(TAG, "onAdLoaded()");

                ad.setAdEventCallback(new BannerAdEventCallback() {
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
                    public void onAdShowedFullScreenContent() {
                        Log.d(TAG, "onAdShowedFullScreenContent()");
                        if (proxy != null) {
                            if (proxy.hasListeners(AdmobModule.AD_OPENED)) {
                                proxy.fireEvent(AdmobModule.AD_OPENED, new KrollDict());
                            }
                        }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                        Log.d(TAG, "onAdDismissedFullScreenContent()");
                        if (proxy != null) {
                            if (proxy.hasListeners(AdmobModule.AD_CLOSED)) {
                                proxy.fireEvent(AdmobModule.AD_CLOSED, new KrollDict());
                            }
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(
                            @NonNull FullScreenContentError fullScreenContentError) {
                        Log.d(TAG, "onAdFailedToShowFullScreenContent(): " + fullScreenContentError);
                        if (proxy != null) {
                            if (proxy.hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
                                KrollDict errorCallback = new KrollDict();
                                errorCallback.put("message", fullScreenContentError.toString());
                                proxy.fireEvent(AdmobModule.AD_FAILED_TO_SHOW, errorCallback);
                            }
                        }
                    }
                });

                if (proxy != null) {

                    KrollDict sCallback = new KrollDict();

                    if (proxy.hasListeners(AdmobModule.AD_LOADED)) {
                        proxy.fireEvent(AdmobModule.AD_LOADED, sCallback);
                    }
                    // DEPRECATED
                    if (proxy.hasListeners(AdmobModule.AD_RECEIVED)) {
                        Log.w(TAG, "AD_RECEIVED has been deprecated and should be replaced by AD_LOADED");
                        proxy.fireEvent(AdmobModule.AD_RECEIVED, sCallback);
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, ("onAdFailedToLoad(): " + AdmobModule.getErrorReason(loadAdError.getCode().getValue())));
                if (proxy != null) {

                    KrollDict errorCallback = new KrollDict();
                    errorCallback.put("code", loadAdError.getCode());
                    errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode().getValue()));
                    errorCallback.put("message", loadAdError.getMessage());

                    if (proxy.hasListeners(AdmobModule.AD_FAILED_TO_LOAD)) {
                        proxy.fireEvent(AdmobModule.AD_FAILED_TO_LOAD, errorCallback);
                    }

                    //DEPRECATED
                    if (proxy.hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
                        Log.w(TAG, "AD_NOT_RECEIVED has been deprecated and should be replaced by AD_FAILED_TO_LOAD");
                        proxy.fireEvent(AdmobModule.AD_NOT_RECEIVED, errorCallback);
                    }
                }
            }
        });
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

        if (d.containsKey(AdmobModule.EXTRA_BUNDLE)) {
            Log.d(TAG, "Has extras");
            AdmobModule.extras = AdmobModule.mapToBundle(d.getKrollDict(AdmobModule.EXTRA_BUNDLE));
        }

        if (d.containsKeyAndNotNull("customAdSize")) {
            Log.d(TAG, ("has customAdSize"));

            KrollDict cas = d.getKrollDict("customAdSize");

            int adWidth;
            int adHeight;

            if (cas.get("width") instanceof String){
                adWidth = Integer.parseInt((String) cas.get("width"));
            } else {
                adWidth = cas.getInt("width");
            }

            if (cas.get("height") instanceof String){
                adHeight = Integer.parseInt((String) cas.get("height"));
            } else {
                adHeight = cas.getInt("height");
            }

            createSmartBannerView(new AdSize(adWidth, adHeight));
        }

        if (d.containsKeyAndNotNull("adSize")) {
            Log.d(TAG, ("has adSize: " + d.getString("adSize")));

            String myAdSizeType = d.getString("adSize");

            switch (myAdSizeType){
                case AdmobModule.BANNER:
                    createSmartBannerView(AdSize.BANNER);
                    break;
                case AdmobModule.LARGE_BANNER:
                    createSmartBannerView(AdSize.LARGE_BANNER);
                    break;
                case AdmobModule.MEDIUM_RECTANGLE:
                    createSmartBannerView(AdSize.MEDIUM_RECTANGLE);
                    break;
                case AdmobModule.FULL_BANNER:
                    createSmartBannerView(AdSize.FULL_BANNER);
                    break;
                case AdmobModule.LEADERBOARD:
                    createSmartBannerView(AdSize.LEADERBOARD);
                    break;
                case AdmobModule.SMART_BANNER:
                    createSmartBannerView(AdSize.BANNER);
                    Log.w(TAG, ("SMART_BANNER has been deprecated and should be replaced by " +
                            "Adaptative Ads now. Create Adaptative Ads using the method " +
                            ".createAdaptiveBanner()."));
                    break;
                default:
                    Log.e(TAG, ("The adSize you chose is invalid! You need to choose one of " +
                            "the options listed: BANNER, LARGE_BANNER, MEDIUM_RECTANGLE, " +
                            "FULL_BANNER, LEADERBOARD or SMART_BANNER."));
                    break;
            }
        }

        if(!d.containsKeyAndNotNull("adSize") && !d.containsKeyAndNotNull("customAdSize")){
            Log.w(TAG, ("You need to determine an AdSize! Showing BANNER as default."));
            createSmartBannerView(AdSize.BANNER);
        }
    };

    public void pause()
    {
        // The GMA Next-Gen SDK's AdView no longer exposes pause()/resume(); banner ad
        // lifecycle (including video assets) is now managed automatically by the SDK.
        Log.d(TAG, "pause() is a no-op under the GMA Next-Gen SDK.");
    }

    public void resume()
    {
        Log.d(TAG, "resume() is a no-op under the GMA Next-Gen SDK.");
    }

    public void destroy()
    {
        Log.d(TAG, "destroy");
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }
    }
}
