package com.haiprj.haigame.wukongfly.mvp.presenter;

import com.haiprj.haigame.wukongfly.mvp.model.DataModel;
import com.haiprj.haigame.wukongfly.mvp.model.DataResult;
import com.haiprj.haigame.wukongfly.mvp.view.ViewResult;

public class DataPresenter implements DataResult {

    protected ViewResult viewResult;
    protected DataModel dataModel;
    public DataPresenter(ViewResult viewResult) {
        this.viewResult = viewResult;
        dataModel = new DataModel(this);
    }

    @Override
    public void onDataResultSuccess(String key, Object... objects) {
        viewResult.onViewAvailable(key, objects);
    }

    @Override
    public void onDataResultFailed(String mess) {
        viewResult.onViewNotAvailable(mess);
    }


}
