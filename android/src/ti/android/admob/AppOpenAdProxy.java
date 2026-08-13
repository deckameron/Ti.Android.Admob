package ti.android.admob;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd;
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;


@Kroll.proxy(creatableInModule = AdmobModule.class)
public class AppOpenAdProxy extends KrollProxy {

    private static final String TAG = "AppOpenAd";

    private long _numHours = 0;

    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;

    public AppOpenAdProxy() {
        super();
        org.appcelerator.kroll.common.Log.d(TAG, "createAppOpenAd()");
    }

    @Override
    public void handleCreationDict(KrollDict options) {

        org.appcelerator.kroll.common.Log.d(TAG, "handleCreationDict...");
        super.handleCreationDict(options);

        if (options.containsKeyAndNotNull("adUnitId")) {
            AdmobModule.APP_OPEN_AD_UNIT_ID = options.getString("adUnitId");
            Log.d(TAG, "adUnitId: " + AdmobModule.APP_OPEN_AD_UNIT_ID);
        }

        if (options.containsKeyAndNotNull("refreshTimeInHours")){
            _numHours = Long.valueOf(options.getInt("refreshTimeInHours"));
            Log.d(TAG, "refreshTimeInHours: " + String.valueOf(_numHours));
        }

        if (options.containsKey(AdmobModule.EXTRA_BUNDLE)) {
            AdmobModule.extras = AdmobModule.mapToBundle(options.getKrollDict(AdmobModule.EXTRA_BUNDLE));
            Log.d(TAG, "Has extras");
        }

        load();
    }

    @Kroll.method
    public void load(){

        if(isLoadingAd){
            Log.d(TAG, "Ad is loading. No need to rush!");
            return;
        }

        isLoadingAd = true;

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder(AdmobModule.APP_OPEN_AD_UNIT_ID);

        Bundle bundle = AdmobModule.createAdRequestProperties();
        if (!bundle.isEmpty()) {
            Log.d(TAG, "Has extras -- Setting Ad properties");
            adRequestBuilder.setGoogleExtrasBundle(bundle);
        }

        AdRequest adRequest = adRequestBuilder.build();

        AppOpenAd.load(
                adRequest,
                new AdLoadCallback<AppOpenAd>() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        // Called when an app open ad has loaded.
                        Log.d(TAG, "Ad was loaded.");
                        appOpenAd = ad;
                        isLoadingAd = false;

                        setAppOpenEvents();

                        KrollDict sCallback = new KrollDict();

                        if (hasListeners(AdmobModule.AD_LOADED)) {
                            fireEvent(AdmobModule.AD_LOADED, sCallback);
                        }

                        // DEPRECATED
                        if (hasListeners(AdmobModule.AD_RECEIVED)) {
                            org.appcelerator.kroll.common.Log.w(TAG, "AD_RECEIVED has been deprecated and should be replaced by AD_LOADED");
                            fireEvent(AdmobModule.AD_RECEIVED, sCallback);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Called when an app open ad has failed to load.
                        org.appcelerator.kroll.common.Log.d(TAG, "onAdFailedToLoad");
                        Log.d(TAG, loadAdError.getMessage());
                        isLoadingAd = false;

                        KrollDict errorCallback = new KrollDict();
                        errorCallback.put("code", loadAdError.getCode());
                        errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode().getValue()));
                        errorCallback.put("message", loadAdError.getMessage());

                        if (hasListeners(AdmobModule.AD_FAILED_TO_LOAD)) {
                            fireEvent(AdmobModule.AD_FAILED_TO_LOAD, errorCallback);
                        }

                        //DEPRECATED
                        if (hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
                            org.appcelerator.kroll.common.Log.w(TAG, "AD_NOT_RECEIVED has been deprecated and should be replaced by AD_FAILED_TO_LOAD");
                            fireEvent(AdmobModule.AD_NOT_RECEIVED, errorCallback);
                        }
                    }
                });
    }

    private void setAppOpenEvents(){
        appOpenAd.setAdEventCallback(
                new AppOpenAdEventCallback(){

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                // Set the reference to null so isAdAvailable() returns false.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                appOpenAd = null;
                isShowingAd = false;

                if (hasListeners(AdmobModule.AD_CLOSED)) {
                    fireEvent(AdmobModule.AD_CLOSED, new KrollDict());
                }

                // load();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError adError) {
                // Called when fullscreen content failed to show.
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null;
                isShowingAd = false;

                org.appcelerator.kroll.common.Log.d(TAG, "Ad failed to show.");
                if (hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
                    KrollDict rewardedError = new KrollDict();
                    rewardedError.put("message", adError.toString());
                    fireEvent(AdmobModule.AD_FAILED_TO_SHOW, rewardedError);
                }

                // load();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
                if (hasListeners(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT)) {
                    fireEvent(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT, new KrollDict());
                }
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
                if (hasListeners(AdmobModule.AD_CLICKED)) {
                    fireEvent(AdmobModule.AD_CLICKED, new KrollDict());
                }
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }
        });
    }

    @Kroll.method
    public void show(){

        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.w(TAG, "The App Open Ad is already showing.");
            return;
        }

        // If the app open ad is still loading, you can not show the ad.
        if (isLoadingAd){
            Log.w(TAG, "The App Open Ad is still loading. Hang on!");
            return;
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            Log.w(TAG, "The App Open Ad is not available. Did you call load() method?");
            if (hasListeners(AdmobModule.AD_NOT_READY)) {
                KrollDict rewardedError = new KrollDict();
                rewardedError.put("message", "Ad not ready yet! You should run the method load() first and wait for the callbacks");
                fireEvent(AdmobModule.AD_NOT_READY, rewardedError);
            }
        } else {
            isShowingAd = true;
            appOpenAd.show(TiApplication.getAppCurrentActivity());
        }
    }

    /** Check if ad exists and can be shown. */
    private boolean isAdAvailable() {
        return appOpenAd != null;
    }
}
