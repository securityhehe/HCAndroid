package com.liveness.dflivenesslibrary.liveness.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.dfsdk.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.DFProductResult;
import com.liveness.dflivenesslibrary.net.DFNetworkUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class DFAntiHackProcessPresenter extends DFResultProcessBasePresenter {

    protected DFProductResult mProductResult;

    protected ExecutorService mThreadPool;

    private Future<DFNetworkUtil.DFNetResult> mLivenessFuture;

    public DFAntiHackProcessPresenter(boolean returnImage, DFResultProcessCallback resultProcessCallback) {
        super(returnImage, resultProcessCallback);
    }

    private boolean checkStringLength(String value, int checkLength) {
        boolean checkResult = false;
        if (!TextUtils.isEmpty(value) && value.length() == checkLength) {
            checkResult = true;
        }
        return checkResult;
    }

    @Override
    public void checkResultDealParameter() {
        super.checkResultDealParameter();
        if (!checkStringLength(DFNetworkUtil.API_ID, 32)
                || !checkStringLength(DFNetworkUtil.API_SECRET, 32)) {
            throw new IllegalArgumentException("Invalid API_ID or API_SECRET");
        }
    }

    @Override
    public void dealLivenessResult(byte[] livenessEncryptResult, DFLivenessSDK.DFLivenessImageResult[] imageResult) {
        showProgressDialog();
        mProductResult = getDetectResult(livenessEncryptResult, imageResult);
        initThreadPool();
        antiHack();
        getNetworkResult();
    }

    private void initThreadPool() {
        if (mThreadPool == null) {
            mThreadPool = Executors.newFixedThreadPool(3);
        }
    }

    private void antiHack() {
        mLivenessFuture = mThreadPool.submit(new Callable<DFNetworkUtil.DFNetResult>() {
            @Override
            public DFNetworkUtil.DFNetResult call() throws Exception {
                return networkProcess();
            }
        });
    }

    public DFNetworkUtil.DFNetResult networkProcess() {
        return DFNetworkUtil.doAntiHack(mProductResult.getLivenessEncryptResult());
    }

    protected void getNetworkResult() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final DFNetworkUtil.DFNetResult result = mLivenessFuture.get();
                    mProductResult.setAntiHackPass(result.mNetworkResultStatus);
                    int networkErrorMsgID = result.mNetworkErrorMsgID;
                    if (networkErrorMsgID > 0) {
                        mProductResult.setErrorMessage(getString(networkErrorMsgID));
                    }
                    hideProgressDialog();
                    returnDetectResult();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getString(int stringResId) {
        String value = "";
        if (mResultProcessCallback != null) {
            Context activityContext = mResultProcessCallback.getActivityContext();
            if (activityContext != null) {
                value = activityContext.getString(stringResId);
            }
        }
        return value;
    }

    protected void returnDetectResult() {
        if (mResultProcessCallback != null) {
            mResultProcessCallback.returnDFProductResult(mProductResult);
        }
    }
}
