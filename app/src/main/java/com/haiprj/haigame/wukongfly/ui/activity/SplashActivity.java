package com.haiprj.haigame.wukongfly.ui.activity;

import android.annotation.SuppressLint;

import com.haiprj.haigame.wukongfly.AdCache;
import com.haiprj.haigame.wukongfly.BuildConfig;
import com.haiprj.haigame.wukongfly.R;
import com.haiprj.haigame.wukongfly.base.utils.GameSharePreference;
import com.haiprj.haigame.wukongfly.base.view.BaseActivity;
import com.haiprj.haigame.wukongfly.databinding.ActivitySplashBinding;
import com.haiprj.haigame.wukongfly.ui.widget.SplashSurface;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.haiprj.android_app_lib.interfaces.AdCallback;
import com.haiprj.android_app_lib.my_admobs.AdmobManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    @Override
    protected void initView() {
        GameSharePreference.getInstance().init(this);
        loadInter();
        binding.gameSurface.startThread();
    }

    private void loadInter() {
        AdmobManager.getInstance().loadInterAds(this, BuildConfig.inter_splash, new AdCallback() {
            @Override
            public void onAdFailedToLoad(com.google.android.gms.ads.LoadAdError i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void interCallback(InterstitialAd interstitialAd) {
                super.interCallback(interstitialAd);
                AdCache.getInstance().setSplashInterstitial(interstitialAd);

            }
        });
    }

    @Override
    protected void addEvent() {
        binding.gameSurface.setLineChangeListener(new SplashSurface.LineChangeListener() {
            @Override
            public void onChange(float current, float max) {
                if (current *100/ max >= 70) startMain();
            }

            @Override
            public void onMax() {
                binding.gameSurface.stopThread();
                finish();
            }
        });
    }

    private void startMain() {
        showInter();

    }

    private void showInter() {
        if (AdCache.getInstance().getSplashInterstitial() == null) {
            goToMain();
            return;
        }
        AdmobManager.getInstance().showInterstitial(this, AdCache.getInstance().getSplashInterstitial(), new AdCallback() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AdCache.getInstance().setSplashInterstitial(null);
                goToMain();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

    }

    private void goToMain() {
        MainActivity.start(SplashActivity.this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }
}
