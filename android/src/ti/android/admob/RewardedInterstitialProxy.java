package ti.android.admob;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.rewarded.OnUserEarnedRewardListener;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardItem;
import com.google.android.libraries.ads.mobile.sdk.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.libraries.ads.mobile.sdk.rewardedinterstitial.RewardedInterstitialAdEventCallback;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

@Kroll.proxy(creatableInModule = AdmobModule.class)
public class RewardedInterstitialProxy extends KrollProxy implements OnUserEarnedRewardListener{

    private final String TAG = "RewardedInterstitialAd";
    private RewardedInterstitialAd rewardedInterstitialAd;


    public RewardedInterstitialProxy() {
        super();
        Log.d(TAG, "createRewardedInterstitial()");
    }

    // Handle creation options
    @Override
    public void handleCreationDict(KrollDict options) {

        Log.d(TAG, "handleCreationDict...");
        super.handleCreationDict(options);

        if (options.containsKeyAndNotNull("adUnitId")) {
            AdmobModule.REWARDED_INTERSTITIAL_AD_UNIT_ID = options.getString("adUnitId");
            Log.d(TAG, "adUnitId: " + AdmobModule.REWARDED_INTERSTITIAL_AD_UNIT_ID);
        }

        if (options.containsKey(AdmobModule.EXTRA_BUNDLE)) {
            AdmobModule.extras = AdmobModule.mapToBundle(options.getKrollDict(AdmobModule.EXTRA_BUNDLE));
            android.util.Log.d(TAG, "Has extras");
        }

        load();
    }

    @Kroll.method
    public void load(){

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder(AdmobModule.REWARDED_INTERSTITIAL_AD_UNIT_ID);

        Bundle bundle = AdmobModule.createAdRequestProperties();
        if (!bundle.isEmpty()) {
            android.util.Log.d(TAG, "Has extras -- Setting Ad properties");
            adRequestBuilder.setGoogleExtrasBundle(bundle);
        }

        RewardedInterstitialAd.load(adRequestBuilder.build(), new AdLoadCallback<RewardedInterstitialAd>() {
            @Override
            public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                rewardedInterstitialAd = ad;
                Log.e(TAG, "onAdLoaded");
                setRewardedInterstitialEvents();

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
                Log.e(TAG, "onAdFailedToLoad");
                rewardedInterstitialAd = null;

                KrollDict rewardedError = new KrollDict();
                rewardedError.put("code", loadAdError.getCode());
                rewardedError.put("reason", AdmobModule.getErrorReason(loadAdError.getCode().getValue()));
                rewardedError.put("message", loadAdError.getMessage());

                if (hasListeners(AdmobModule.AD_FAILED_TO_LOAD)) {
                    fireEvent(AdmobModule.AD_FAILED_TO_LOAD, rewardedError);
                }

                //DEPRECATED
                if (hasListeners(AdmobModule.AD_NOT_RECEIVED)) {
                    Log.w(TAG, "AD_NOT_RECEIVED has been deprecated and should be replaced by AD_FAILED_TO_LOAD");
                    fireEvent(AdmobModule.AD_NOT_RECEIVED, rewardedError);
                }
            }
        });
    }

    private void setRewardedInterstitialEvents(){
        rewardedInterstitialAd.setAdEventCallback(new RewardedInterstitialAdEventCallback() {
            /** Called when the ad failed to show full screen content. */
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError adError) {
                Log.i(TAG, "onAdFailedToShowFullScreenContent");
                if (hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
                    KrollDict rewardedError = new KrollDict();
                    rewardedError.put("message", adError.toString());
                    fireEvent(AdmobModule.AD_FAILED_TO_SHOW, rewardedError);
                }
            }

            /** Called when ad showed the full screen content. */
            @Override
            public void onAdShowedFullScreenContent() {
                Log.i(TAG, "onAdShowedFullScreenContent");
                rewardedInterstitialAd = null;
                if (hasListeners(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT)) {
                    fireEvent(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT, new KrollDict());
                }
            }

            /** Called when full screen content is dismissed. */
            @Override
            public void onAdDismissedFullScreenContent() {
                Log.i(TAG, "onAdDismissedFullScreenContent");
                rewardedInterstitialAd = null;
                if (hasListeners(AdmobModule.AD_CLOSED)) {
                    fireEvent(AdmobModule.AD_CLOSED, new KrollDict());
                }
            }
        });
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        Log.i(TAG, "onUserEarnedReward");
        if (hasListeners(AdmobModule.AD_REWARDED)) {
            KrollDict rewardCallback = new KrollDict();
            rewardCallback.put("amount", rewardItem.getAmount());
            rewardCallback.put("type", rewardItem.getType());
            fireEvent(AdmobModule.AD_REWARDED, rewardCallback);
        }
    }

    @Kroll.method
    public void show(){
        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd.show(/* Activity */ getActivity(),/*
        OnUserEarnedRewardListener */ (OnUserEarnedRewardListener) this);
        } else {
            Log.d(TAG, "The rewarded interstitial ad wasn't ready yet.");
        }
    }
}
