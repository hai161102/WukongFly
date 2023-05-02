package com.haiprj.haigame.wukongfly.mvp.model;

public interface DataResult {
    void onDataResultSuccess(String key, Object... objects);
    void onDataResultFailed(String mess);
}
