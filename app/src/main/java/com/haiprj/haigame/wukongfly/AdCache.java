package com.haiprj.haigame.wukongfly;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;

public class AdCache {

    private static AdCache instance;

    private InterstitialAd splashInterstitial;
    private InterstitialAd gameInterstitial;
    private RewardedAd continueReward;
    private AdCache() {
    }

    public static AdCache getInstance() {
        if (instance == null) instance = new AdCache();
        return instance;
    }

    public InterstitialAd getSplashInterstitial() {
        return splashInterstitial;
    }

    public void setSplashInterstitial(InterstitialAd splashInterstitial) {
        this.splashInterstitial = splashInterstitial;
    }

    public InterstitialAd getGameInterstitial() {
        return gameInterstitial;
    }

    public void setGameInterstitial(InterstitialAd gameInterstitial) {
        this.gameInterstitial = gameInterstitial;
    }

    public RewardedAd getContinueReward() {
        return continueReward;
    }

    public void setContinueReward(RewardedAd continueReward) {
        this.continueReward = continueReward;
    }
}
