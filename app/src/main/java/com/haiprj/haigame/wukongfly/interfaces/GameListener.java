package com.haiprj.haigame.wukongfly.interfaces;

import com.haiprj.haigame.wukongfly.gamemodel.PlayerManager;

public interface GameListener {

    void onWin(PlayerManager playerManager);
    void onOver(PlayerManager playerManager);
}
