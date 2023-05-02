package com.haiprj.haigame.wukongfly.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.haiprj.android_app_lib.interfaces.AdCallback;
import com.haiprj.android_app_lib.my_admobs.AdmobManager;
import com.haiprj.haigame.wukongfly.AdCache;
import com.haiprj.haigame.wukongfly.BuildConfig;
import com.haiprj.haigame.wukongfly.R;
import com.haiprj.haigame.wukongfly.base.view.BaseActivity;
import com.haiprj.haigame.wukongfly.databinding.ActivityGameBinding;
import com.haiprj.haigame.wukongfly.gamemodel.PlayerManager;
import com.haiprj.haigame.wukongfly.interfaces.GameListener;
import com.haiprj.haigame.wukongfly.ui.dialog.DeathDialog;

import java.util.Objects;

public class GameActivity extends BaseActivity<ActivityGameBinding> {

    Dialog dialog;
    private boolean isStart = true;
    private boolean hasReward;
    private boolean isContinue;
    private Thread thread;

    public static void start(Context context) {
        Intent starter = new Intent(context, GameActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_game);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            binding.playText.setVisibility(View.VISIBLE);
                            thread.interrupt();
                        } catch (InterruptedException ignored) {
                        }
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    protected void addEvent() {

        binding.playText.setOnClickListener(v -> {
            if (isStart){
                binding.gameSurface.startThread();
                isStart = false;
            }
            v.setVisibility(View.GONE);
        });
        binding.gameSurface.setListener(new GameListener() {
            @Override
            public void onWin(PlayerManager playerManager) {
                DeathDialog.getInstance(GameActivity.this, GameActivity.this, (key, objects) -> {
                    if (Objects.equals(key, "OnOke")){
                        finish();
                    }
                }, playerManager).showDialog();

            }

            @Override
            public void onOver(PlayerManager playerManager) {

                DeathDialog.getInstance(GameActivity.this, GameActivity.this, (key, objects) -> {
                    if (Objects.equals(key, "OnOke")){
                        showInter();
                    }
                    if (Objects.equals(key, "replay")){
                        binding.gameSurface.playAgain();
                    }
                    if (Objects.equals(key, "resume")){
                        showReward();
                    }
                }, playerManager).show();
            }
        });
//        GameMedia.getInstance(this).getMediaPlayerAt(MediaEnum.DEATH_SONG).setOnCompletionListener(mediaPlayer -> {
//            binding.gameFrame.gameOver();
//        });
    }

    private void showInter() {
        if (AdCache.getInstance().getGameInterstitial() != null) {
            AdmobManager.getInstance().showInterstitial(this, AdCache.getInstance().getGameInterstitial(), new AdCallback() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    AdCache.getInstance().setGameInterstitial(null);
                    finish();
                }
            });
        }
        else {
            finish();
        }
    }

    public ActivityGameBinding getBinding() {
        return binding;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (AdCache.getInstance().getGameInterstitial() == null) {
            AdmobManager.getInstance().loadInterAds(this, BuildConfig.inter_exit, new AdCallback() {
                @Override
                public void interCallback(InterstitialAd interstitialAd) {
                    super.interCallback(interstitialAd);
                    AdCache.getInstance().setGameInterstitial(interstitialAd);
                }
            });
        }
        loadReward();
    }

    private void loadReward() {
        if (AdCache.getInstance().getContinueReward() == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, BuildConfig.reward_continue,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    if (!hasReward) {
                                        finish();
                                    }
                                    else {
                                        binding.gameSurface.resumePlay();
                                        AdCache.getInstance().setContinueReward(null);
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);

                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                }
                            });
                            AdCache.getInstance().setContinueReward(ad);
                        }
                    });
        }
    }

    private void showReward() {
        if (AdCache.getInstance().getContinueReward() != null) {
            AdCache.getInstance().getContinueReward().show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    Log.d("HandleReward", "onUserEarnedReward: Amount: " + rewardAmount + " Type: " + rewardType);
                    hasReward = rewardType.equals("PlayContinue");
                }
            });
        }
    }

    @Override
    public void finish() {
        binding.gameSurface.clearAll();
        binding.layout.removeAllViews();
        super.finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
