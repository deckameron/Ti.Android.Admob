package ti.android.admob;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

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

        load();
    }

    @Kroll.method
    public void load(){

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        AdRequest adRequest = adRequestBuilder.build();

        Log.d(TAG, "Is test device? " + adRequest.isTestDevice(getActivity()));

        InterstitialAd.load(getActivity(), AdmobModule.INTERSTITIAL_AD_UNIT_ID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The _interstitialAd reference will be null until
                // an ad is loaded.
                _interstitialAd = interstitialAd;
                setInterstitialEvents();
                Log.i(TAG, "onAdLoaded");
                if (hasListeners(AdmobModule.AD_LOADED)) {
                    fireEvent(AdmobModule.AD_LOADED, new KrollDict());
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                _interstitialAd = null;
                Log.d(TAG, "onAdFailedToLoad");
                Log.i(TAG, loadAdError.getMessage());
                if (hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
                    KrollDict errorCallback = new KrollDict();
                    errorCallback.put("cause", loadAdError.getCause());
                    errorCallback.put("code", loadAdError.getCode());
                    errorCallback.put("reason", AdmobModule.getErrorReason(loadAdError.getCode()));
                    errorCallback.put("message", loadAdError.getMessage());
                    fireEvent(AdmobModule.AD_NOT_RECEIVED, errorCallback);
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
                if (hasListeners(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT)) {
                    fireEvent(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT, new KrollDict());
                }
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show.");
                if (hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
                    KrollDict rewardedError = new KrollDict();
                    rewardedError.put("cause", adError.getCause());
                    rewardedError.put("code", adError.getCode());
                    rewardedError.put("message", adError.getMessage());
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
