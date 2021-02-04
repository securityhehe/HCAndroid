package com.hc.load.view;

import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class MyViewStubProxy {
    private ViewStub mViewStub;
    private ViewDataBinding mViewDataBinding;
    private View mRoot;
    private ViewStub.OnInflateListener mOnInflateListener;
    private ViewDataBinding mContainingBinding;

    //TODO 2 设置代理监听器
    private ViewStub.OnInflateListener mProxyListener = new ViewStub.OnInflateListener() {
        @Override
        public void onInflate(ViewStub stub, View inflated) {
            mRoot = inflated;//TODO 一旦Vs实例化，此监听器就会回调，并获取到mRoot
            mViewDataBinding = DataBindingUtil.bind(inflated);
            mViewStub = null;

            //TODO 3 传入自定义监听器
            if (mOnInflateListener != null) {
                mOnInflateListener.onInflate(stub, inflated);
                mOnInflateListener = null;
            }
        }
    };

    //TODO 1 将VS装进来 && 2 设置代理监听器
    public MyViewStubProxy(@NonNull ViewStub viewStub) {
        mViewStub = viewStub;
        mViewStub.setOnInflateListener(mProxyListener);
    }

    public void setContainingBinding(@NonNull ViewDataBinding containingBinding) {
        mContainingBinding = containingBinding;
    }

    public boolean isInflated() {
        return mRoot != null;
    }

    public View getRoot() {
        return mRoot;
    }

    @Nullable
    public ViewDataBinding getBinding() {
        return mViewDataBinding;
    }

    @Nullable
    public ViewStub getViewStub() {
        return mViewStub;
    }

    /**
     * TODO 传入自定义监听器
     * @param listener
     */
    public void setOnInflateListener(@Nullable ViewStub.OnInflateListener listener) {
        if (mViewStub != null) {
            mOnInflateListener = listener;
        }
    }
}

