package ti.android.admob;

import android.util.Log;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;

import java.util.Date;

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

        load();
    }

    @Kroll.method
    public void load(){

        if(isLoadingAd){
            Log.d(TAG, "Ad is loading. No need to rush!");
            return;
        }

        isLoadingAd = true;

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        AdRequest adRequest = adRequestBuilder.build();
        AppOpenAd.load(
                TiApplication.getAppCurrentActivity(),
                AdmobModule.APP_OPEN_AD_UNIT_ID,
                adRequest,
                // AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
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
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Called when an app open ad has failed to load.
                        org.appcelerator.kroll.common.Log.d(TAG, "onAdFailedToLoad");
                        Log.d(TAG, loadAdError.getMessage());
                        isLoadingAd = false;

                        KrollDict errorCallback = new KrollDict();
                        errorCallback.put("cause", loadAdError.getCause());
                        errorCallback.put("code", loadAdError.getCode());
                        errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode()));
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
        appOpenAd.setFullScreenContentCallback(
                new FullScreenContentCallback(){

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
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null;
                isShowingAd = false;

                org.appcelerator.kroll.common.Log.d(TAG, "Ad failed to show.");
                if (hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
                    KrollDict rewardedError = new KrollDict();
                    rewardedError.put("cause", adError.getCause());
                    rewardedError.put("code", adError.getCode());
                    rewardedError.put("message", adError.getMessage());
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
            // load();
            return;
        }

        if(isAdAvailable()){
            isShowingAd = true;
            appOpenAd.show(TiApplication.getAppCurrentActivity());
        }
    }

    /** Check if ad exists and can be shown. */
    private boolean isAdAvailable() {
        return appOpenAd != null;
    }
}
