package ti.android.admob;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

@Kroll.proxy(creatableInModule = AdmobModule.class)
public class InterstitialProxy extends KrollProxy {

    private final String TAG = "InterstitialAd";
    private InterstitialAd _interstitialAd;

    public InterstitialProxy() {
        super();
        Log.d(TAG, "createInterstitial()");
    }

    // Handle creation options
    @Override
    public void handleCreationDict(KrollDict options) {

        Log.d(TAG, "handleCreationDict...");
        super.handleCreationDict(options);

        if (options.containsKeyAndNotNull("adUnitId")) {
            AdmobModule.INTERSTITIAL_AD_UNIT_ID = options.getString("adUnitId");
            Log.d(TAG, "adUnitId: " + AdmobModule.INTERSTITIAL_AD_UNIT_ID);
        }

        if (options.containsKeyAndNotNull(AdmobModule.EXTRA_BUNDLE)) {
            AdmobModule.extras = AdmobModule.mapToBundle(options.getKrollDict(AdmobModule.EXTRA_BUNDLE));
            android.util.Log.d(TAG, "Has extras");
        }

        load();
    }

    @Kroll.method
    public void load(){

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder(AdmobModule.INTERSTITIAL_AD_UNIT_ID);

        Bundle bundle = AdmobModule.createAdRequestProperties();
        if (!bundle.isEmpty()) {
            android.util.Log.d(TAG, "Has extras -- Setting Ad properties");
            adRequestBuilder.setGoogleExtrasBundle(bundle);
        }

        AdRequest adRequest = adRequestBuilder.build();

        InterstitialAd.load(adRequest, new AdLoadCallback<InterstitialAd>() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The _interstitialAd reference will be null until
                // an ad is loaded.
                _interstitialAd = interstitialAd;
                setInterstitialEvents();
                Log.i(TAG, "onAdLoaded");

                KrollDict sCallback = new KrollDict();

                if (hasListeners(AdmobModule.AD_LOADED)) {
                    fireEvent(AdmobModule.AD_LOADED, sCallback);
                }

                // DEPRECATED
                if (hasListeners(AdmobModule.AD_RECEIVED)) {
                    Log.w(TAG, "AD_RECEIVED has been deprecated and should be replaced by AD_LOADED");
                    fireEvent(AdmobModule.AD_RECEIVED, sCallback);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                _interstitialAd = null;
                Log.d(TAG, "onAdFailedToLoad");
                Log.i(TAG, loadAdError.getMessage());

                KrollDict errorCallback = new KrollDict();
                errorCallback.put("code", loadAdError.getCode());
                errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode().getValue()));
                errorCallback.put("message", loadAdError.getMessage());

                if (hasListeners(AdmobModule.AD_FAILED_TO_LOAD)) {
                    fireEvent(AdmobModule.AD_FAILED_TO_LOAD, errorCallback);
                }

                //DEPRECATED
                if (hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
                    Log.w(TAG, "AD_NOT_RECEIVED has been deprecated and should be replaced by AD_FAILED_TO_LOAD");
                    fireEvent(AdmobModule.AD_NOT_RECEIVED, errorCallback);
                }
            }
        });
    }

    private void setInterstitialEvents() {
        _interstitialAd.setAdEventCallback(new InterstitialAdEventCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.");
                _interstitialAd = null;
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

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError adError) {
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show.");
                if (hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
                    KrollDict rewardedError = new KrollDict();
                    rewardedError.put("message", adError.toString());
                    fireEvent(AdmobModule.AD_FAILED_TO_SHOW, rewardedError);
                }
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                Log.d(TAG, "Ad was dismissed.");
                _interstitialAd = null;
                if (hasListeners(AdmobModule.AD_CLOSED)) {
                    fireEvent(AdmobModule.AD_CLOSED, new KrollDict());
                }
            }
        });
    }

    @Kroll.method
    public void show() {
        if (_interstitialAd != null) {
            Log.d(TAG, "Showing ad now...");
            _interstitialAd.show(getActivity());
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
    }
}
