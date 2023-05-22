package ti.android.admob;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

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

        load();
    }

    @Kroll.method
    public void load(){
        RewardedInterstitialAd.load(getActivity(), AdmobModule.REWARDED_INTERSTITIAL_AD_UNIT_ID,
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
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
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "onAdFailedToLoad");
                        rewardedInterstitialAd = null;

                        KrollDict rewardedError = new KrollDict();
                        rewardedError.put("cause", loadAdError.getCause());
                        rewardedError.put("code", loadAdError.getCode());
                        rewardedError.put("reason", AdmobModule.getErrorReason(loadAdError.getCode()));
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
        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            /** Called when the ad failed to show full screen content. */
            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.i(TAG, "onAdFailedToShowFullScreenContent");
                if (hasListeners(AdmobModule.AD_FAILED_TO_SHOW)) {
                    KrollDict rewardedError = new KrollDict();
                    rewardedError.put("cause", adError.getCause());
                    rewardedError.put("code", adError.getCode());
                    rewardedError.put("message", adError.getMessage());
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
        rewardedInterstitialAd.show(/* Activity */ getActivity(),/*
    OnUserEarnedRewardListener */ (OnUserEarnedRewardListener) getActivity());
    }
}
