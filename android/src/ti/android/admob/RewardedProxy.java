package ti.android.admob;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;

@Kroll.proxy(creatableInModule = AdmobModule.class)
public class RewardedProxy extends KrollProxy implements OnUserEarnedRewardListener{

    private final String TAG = "RewardedAd";
    private RewardedAd _rewardedAd;

    public RewardedProxy() {
        super();
        Log.d(TAG, "createRewarded()");

//        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                Log.d(TAG, "Initialized sucessfully!");
//            }
//        });
    }

    // Handle creation options
    @Override
    public void handleCreationDict(KrollDict options) {
        Log.d(TAG, "handleCreationDict...");
        super.handleCreationDict(options);
        if (options.containsKeyAndNotNull("adUnitId")) {
            AdmobModule.REWARDED_AD_UNIT_ID = options.getString("adUnitId");
            Log.d(TAG, "adUnitId: " + AdmobModule.REWARDED_AD_UNIT_ID);
        }

        load();
    }

    @Kroll.method
    public void load(){

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        Bundle bundle = AdmobModule.createAdRequestProperties();
        if (bundle.size() > 0) {
            Log.d(TAG, "extras.size() > 0 -- set ad properties");
            Log.d(TAG, bundle.toString());
            adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, bundle);
        }

        AdRequest adRequest = adRequestBuilder.build();

        RewardedAd.load(getActivity(), AdmobModule.REWARDED_AD_UNIT_ID,
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        _rewardedAd = null;

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

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        _rewardedAd = rewardedAd;
                        setRewardedEvents();
                        Log.d(TAG, "onAdLoaded");

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
                });
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        Log.i(TAG, "onUserEarnedReward");
        if (hasListeners(AdmobModule.AD_REWARDED)) {
            KrollDict rewardedCallback = new KrollDict();
            rewardedCallback.put("amount", rewardItem.getAmount());
            rewardedCallback.put("type", rewardItem.getType());
            fireEvent(AdmobModule.AD_REWARDED, rewardedCallback);
        }
    }

    private void setRewardedEvents(){
        _rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.");
                _rewardedAd = null;
                if (hasListeners(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT)) {
                    fireEvent(AdmobModule.AD_SHOWED_FULLSCREEN_CONTENT, new KrollDict());
                }
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
                _rewardedAd = null;
                if (hasListeners(AdmobModule.AD_CLOSED)) {
                    fireEvent(AdmobModule.AD_CLOSED, new KrollDict());
                }
            }
        });
    }

    @Kroll.method
    public void show() {
        if (_rewardedAd != null) {
            _rewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d("TAG", "The user earned the reward.");
                    if (hasListeners(AdmobModule.AD_REWARDED)) {
                        KrollDict errorCallback = new KrollDict();
                        errorCallback.put("amount", rewardItem.getAmount());
                        errorCallback.put("type", rewardItem.getType());
                        fireEvent(AdmobModule.AD_REWARDED, errorCallback);
                    }
                }
            });
        } else {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
        }
    }
}
