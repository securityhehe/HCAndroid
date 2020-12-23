package com.liveness.dflivenesslibrary.liveness.presenter;

import android.content.Context;

import com.dfsdk.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.DFProductResult;


public abstract class DFResultProcessBasePresenter {

    private boolean mReturnImage;
    protected DFResultProcessCallback mResultProcessCallback;

    public DFResultProcessBasePresenter() {
    }

    public DFResultProcessBasePresenter(boolean returnImage, DFResultProcessCallback resultProcessCallback) {
        this.mReturnImage = returnImage;
        mResultProcessCallback = resultProcessCallback;
    }

    public void checkResultDealParameter() {

    }

    public abstract void dealLivenessResult(byte[] livenessEncryptResult, DFLivenessSDK.DFLivenessImageResult[] imageResult);

    protected DFProductResult getDetectResult(byte[] livenessEncryptResult, DFLivenessSDK.DFLivenessImageResult[] imageResult) {
        DFProductResult result = new DFProductResult();
        if (mReturnImage) {
            result.setLivenessImageResults(imageResult);
        }
        if (livenessEncryptResult != null) {
            result.setLivenessEncryptResult(livenessEncryptResult);
        }
        return result;
    }

    protected void showProgressDialog() {
        if (mResultProcessCallback != null) {
            mResultProcessCallback.showProgressDialog();
        }
    }

    protected void hideProgressDialog() {
        if (mResultProcessCallback != null) {
            mResultProcessCallback.hideProgressDialog();
        }
    }

    public interface DFResultProcessCallback {
        void returnDFProductResult(DFProductResult productResult);

        void showProgressDialog();

        void hideProgressDialog();

        Context getActivityContext();
    }
}
