package com.hc.uicomponent.base;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import androidx.annotation.IntDef;
import com.hc.uicomponent.R;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PlaceholderLayout extends FrameLayout {
    private Context mContext;

    @PageState
    private int mPageState;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PageState.DEFAULT,
            PageState.SUCCESS,
            PageState.EMPTY,
            PageState.ERROR,
            PageState.NO_NETWORK,
            PageState.LOADING,
            PageState.NO_NOTICE,
            PageState.NO_ANNO,

    })
    public @interface PageState {
        int DEFAULT = -1;
        int SUCCESS = 0;
        int EMPTY = 1;
        int ERROR = 2;
        int NO_NETWORK = 3;
        int LOADING = 4;
        int NO_NOTICE = 5;
        int NO_ANNO = 6;//公告
    }

    private SparseArray<IStateView> mStatePageArray = new SparseArray();

    /**
     * 点击重试 - 回调接口
     */
    private OnReloadListener listener;

    private static int backgroundColor = Color.parseColor("#ffffff");

    public PlaceholderLayout(Context context) {
        super(context);
        init(context);
    }

    public PlaceholderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlaceholderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setBackgroundColor(Color.parseColor("#ffffff"));
        buildDefaultStatusPage();
    }

    private void buildDefaultStatusPage() {
        IStateView DEFAULT = new IStateView(R.layout.page_default, null, listener);
        IStateView SUCCESS = new IStateView(R.layout.page_success ,null, listener);
        IStateView EMPTY = new IStateView(R.layout.page_empty, null, listener);
        IStateView ERROR = new IStateView(R.layout.page_error, null, listener);
        IStateView NO_NETWORK = new IStateView(R.layout.page_no_network, null, listener);
        IStateView LOADING = new IStateView(R.layout.page_loading, null, listener);
        IStateView NO_NOTICE = new NoticeStateView(R.layout.page_notice,"你好，，。。。这是通知",listener);
        IStateView NO_ANNO = new IStateView(R.layout.page_anno, null, listener);
        mStatePageArray.put(PageState.DEFAULT, DEFAULT);
        mStatePageArray.put(PageState.SUCCESS, SUCCESS);
        mStatePageArray.put(PageState.EMPTY, EMPTY);
        mStatePageArray.put(PageState.ERROR, ERROR);
        mStatePageArray.put(PageState.NO_NETWORK, NO_NETWORK);
        mStatePageArray.put(PageState.LOADING, LOADING);
        mStatePageArray.put(PageState.NO_NOTICE, NO_NOTICE);
        mStatePageArray.put(PageState.NO_ANNO, NO_ANNO);
    }

    private void setViewClick(View... clickView) {
        for (int i = 0; i < clickView.length; i++) {
            clickView[i].setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReload(v);
                }
            });
        }
    }

    public <T> void setStatus(@PageState int status, IStateView stateView) {
        setMatchParent();
        this.removeAllViews();
        if (stateView != null) {
            mStatePageArray.put(status, stateView);
            if (stateView.listener == null) {
                stateView.listener = listener;
            }
        }
        View layout = LayoutInflater.from(getContext()).inflate(mStatePageArray.get(status).view, null, false);
        addView(layout);
        stateView.bindView(layout, stateView.data, stateView.listener);

    }

    /**
     * 返回当前状态{SUCCESS, EMPTY, ERROR, NO_NETWORK, LOADING}
     */
    public int getStatus() {
        return mPageState;
    }

    public PlaceholderLayout setOnReloadListener(OnReloadListener listener) {
        this.listener = listener;
        return this;
    }


    private void setMatchParent() {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.setLayoutParams(layoutParams);
    }

    public interface OnReloadListener {
        void onReload(View v);
    }

    private Animation rotate;

    private void startLoadAnim() {
    }

    private void stopLoadAnim() {
    }

    public class IStateView<T extends Serializable> {
        public IStateView(int view, T t, OnReloadListener listener) {
            this.view = view;
            this.data = t;
            this.listener = listener;
        }

        private OnReloadListener listener;
        private int view;
        private T data;

        public void bindView(View view, T data, OnReloadListener listener) {

        }
    }

    public class NoticeStateView extends IStateView<String>{

        public NoticeStateView(int view, String s, OnReloadListener listener) {
            super(view, s, listener);
        }

        @Override
        public void bindView(View view, String data, OnReloadListener listener) {

        }
    }

}
