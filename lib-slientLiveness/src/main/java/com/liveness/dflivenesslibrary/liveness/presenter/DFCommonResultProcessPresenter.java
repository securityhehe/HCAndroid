package com.liveness.dflivenesslibrary.liveness.presenter;

import com.dfsdk.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.DFProductResult;


public class DFCommonResultProcessPresenter extends DFResultProcessBasePresenter {

    public DFCommonResultProcessPresenter(boolean returnImage, DFResultProcessCallback resultProcessCallback) {
        super(returnImage, resultProcessCallback);
    }

    @Override
    public void dealLivenessResult(byte[] livenessEncryptResult, DFLivenessSDK.DFLivenessImageResult[] imageResult) {
        DFProductResult detectResult = getDetectResult(livenessEncryptResult, imageResult);
        if (mResultProcessCallback != null) {
            mResultProcessCallback.returnDFProductResult(detectResult);
        }
    }
}
