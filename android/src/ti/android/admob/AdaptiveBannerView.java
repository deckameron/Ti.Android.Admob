package ti.android.admob;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowMetrics;

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
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

public class AdaptiveBannerView extends TiUIView {

    private static final String TAG = "AdaptiveBannerAd";

    private AdView adaptiveAdView;

    private int maxHeight = 50;
    public String adaptiveType;
    private String keyword;
    private String contentUrl;
    private String collapsible;

    public AdaptiveBannerView(TiViewProxy proxy) {
        super(proxy);
        Log.d(TAG, "Creating AdaptiveBanner AdView");
    }

    private void createAdaptiveAdView() {
        Log.d(TAG, "createAdaptiveAdView()");
        adaptiveAdView = new AdView(proxy.getActivity());

        Log.d(TAG, ("AdmobModule.AD_UNIT_ID: " + AdmobModule.BANNER_AD_UNIT_ID));

        if (keyword != null || contentUrl != null) {
            Log.w(TAG, "The 'keyword' and 'contentUrl' properties are no longer supported by " +
                    "the GMA Next-Gen SDK's BannerAdRequest and will be ignored.");
        }

        int adWidth = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // API 30+ (Usando WindowMetrics)
            WindowMetrics windowMetrics = proxy.getActivity().getWindowManager().getCurrentWindowMetrics();
            Rect bounds = windowMetrics.getBounds();
            float density = proxy.getActivity().getResources().getDisplayMetrics().density;

            // Largura do anúncio em dp
            adWidth = (int) (bounds.width() / density);
            Log.d(TAG, "VERSION_CODES.R - ADAPTIVE adWidth = " + adWidth);
        } else {
            // API < 30 (Usando Resources)
            DisplayMetrics displayMetrics = proxy.getActivity().getResources().getDisplayMetrics();
            float widthPixels = displayMetrics.widthPixels;
            float density = displayMetrics.density;

            // Largura do anúncio em dp
            adWidth = (int) (widthPixels / density);
            Log.d(TAG, "VERSION_CODES.<R - ADAPTIVE adWidth = " + adWidth);
        }

        // Determinar o tamanho adaptativo do anúncio
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(proxy.getActivity(), adWidth);

        if (adaptiveType.equals(AdmobModule.ADAPTIVE_INLINE)) {
            Log.w(TAG, "maxHeight: " + maxHeight);
            adSize = AdSize.getInlineAdaptiveBannerAdSize(adWidth, maxHeight);
        }

        Log.d(TAG, "adSize: " + adSize);

        // Step 5 - Convert to Titanium Native View.
        setNativeView(adaptiveAdView);

        // Step 6 - Build and request the Ad.
        BannerAdRequest.Builder adRequestBuilder =
                new BannerAdRequest.Builder(AdmobModule.BANNER_AD_UNIT_ID, adSize);

        Bundle bundle = AdmobModule.createAdRequestProperties();
        if (collapsible != null && !collapsible.isEmpty()) {
            bundle.putString("collapsible", collapsible);
        }

        if (!bundle.isEmpty()) {
            Log.d(TAG, "Has extras -- Setting Ad properties");
            adRequestBuilder.setGoogleExtrasBundle(bundle);
        }

        BannerAdRequest adRequest = adRequestBuilder.build();

        // Step 7 - Start loading the ad in the background.
        adaptiveAdView.loadAd(adRequest, new AdLoadCallback<BannerAd>() {
            @Override
            public void onAdLoaded(@NonNull BannerAd ad) {
                Log.d(TAG, "onAdLoaded()");

                // Step 8 - Set the adaptive event callbacks.
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
                        } else {
                            Log.d(TAG, "Will not fire AdmobModule.AD_OPENED event! Proxy is null.");
                        }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
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
                    errorCallback.put("message", loadAdError.getMessage());
                    errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode().getValue()));

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
    public void release() {
        super.release();
        Log.d(TAG, "release called, destroying AdView");
        if (adaptiveAdView != null) {
            adaptiveAdView.destroy();
            adaptiveAdView = null;
        }
    }

    @Override
    public void processProperties(KrollDict d) {
        super.processProperties(d);
        Log.d(TAG, "Process properties");

        if(d.containsKeyAndNotNull("adUnitId")){
            AdmobModule.BANNER_AD_UNIT_ID = d.getString("adUnitId");
        }

        if (d.containsKey(AdmobModule.EXTRA_BUNDLE)) {
            Log.d(TAG, "Has extras");
            AdmobModule.extras = AdmobModule.mapToBundle(d.getKrollDict(AdmobModule.EXTRA_BUNDLE));
        }

        if (d.containsKeyAndNotNull("adaptiveType")) {
            Log.d(TAG, ("has adaptiveType: " + d.getString("adaptiveType")));
            adaptiveType = d.getString("adaptiveType");
        }

        if (d.containsKeyAndNotNull("maxHeight")) {
            Log.d(TAG, ("has maxHeight: " + String.valueOf(d.getInt("maxHeight"))));
            maxHeight = d.getInt("maxHeight");
        }

        if (d.containsKeyAndNotNull("keyword")) {
            Log.d(TAG, ("has keyword: " + d.getString("keyword")));
            keyword = d.getString("keyword");
        }

        if (d.containsKeyAndNotNull("contentUrl")) {
            Log.d(TAG, ("has CONTENT_URL: " + d.getString("contentUrl")));
            contentUrl = d.getString("contentUrl");
        }

        if (d.containsKeyAndNotNull("collapsible")) {
            Log.d(TAG, ("is COLLAPSIBLE: " + d.getString("collapsible")));
            collapsible = d.getString("collapsible");

            if (collapsible.equals(AdmobModule.COLLAPSIBLE_BOTTOM)){
                Log.w(TAG, "The bottom of the expanded ad aligns to the bottom of the collapsed ad. The ad is placed at the bottom of the screen.");
            } else if (collapsible.equals(AdmobModule.COLLAPSIBLE_TOP)){
                Log.w(TAG, "The top of the expanded ad aligns to the top of the collapsed ad. The ad is placed at the top of the screen.");
            } else {
                Log.e(TAG, "The only supported values for collapsible are 'bottom' and 'top'.");
                collapsible = null;
            }
        }

        // Create the Banner
        createAdaptiveAdView();
    };

    public void pause()
    {
        Log.d(TAG, "pause() is a no-op under the GMA Next-Gen SDK.");
    }

    public void resume()
    {
        Log.d(TAG, "resume() is a no-op under the GMA Next-Gen SDK.");
    }

    public void destroy()
    {
        Log.d(TAG, "destroy");

        if (adaptiveAdView != null) {
            // Remove the AdView from its parent to ensure it is properly garbage collected
            if (adaptiveAdView.getParent() != null && adaptiveAdView.getParent() instanceof ViewGroup) {
                ((ViewGroup) adaptiveAdView.getParent()).removeView(adaptiveAdView);
            }

            adaptiveAdView.destroy(); // Libera os recursos do AdView
            adaptiveAdView = null;   // Remove referência para evitar vazamento
        }
    }
}
