package com.hc.uicomponent.placeView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.databinding.ObservableInt;


import com.hc.uicomponent.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PlaceholderLayout extends FrameLayout {
    private ObservableInt observableInt = new ObservableInt(4);

    /**
     * 默认（逻辑判断使用）
     */
    public final static int DEFAULT = -1;
    /**
     * 成功
     */
    public final static int SUCCESS = 0;
    /**
     * 空页
     */
    public final static int EMPTY = 1;
    /**
     * 错误
     */
    public final static int ERROR = 2;
    /**
     * 无网络
     */
    public final static int NO_NETWORK = 3;
    /**
     * 加载中
     */
    public final static int LOADING = 4;

    /**
     * 无通知
     */
    public final static int NO_NOTICE = 5;

    /**
     * 公告
     */
    public final static int NO_ANNO = 6;


    private int state;
    private Context mContext;
    private View errorPage;
    private View emptyPage;
    private View networkPage;
    private View loadingPage;

    private View contentView;

    //
    private View noNoticePage;
    private TextView mNoticeAnnoContentTv;

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
        build();
    }

    private void build() {
        errorPage = LayoutInflater.from(mContext).inflate(R.layout.status_load_failure, this, false);
        emptyPage = LayoutInflater.from(mContext).inflate(R.layout.status_no_data, this, false);
        networkPage = LayoutInflater.from(mContext).inflate(R.layout.status_no_network, this, false);
        loadingPage = LayoutInflater.from(mContext).inflate(R.layout.status_loading, this, false);

        errorPage.setBackgroundColor(backgroundColor);
        emptyPage.setBackgroundColor(backgroundColor);
        networkPage.setBackgroundColor(backgroundColor);
        loadingPage.setBackgroundColor(backgroundColor);

        //TODO 1 这地方需要扩展+处理,要不然后期还要修改此文件！！！
        noNoticePage = LayoutInflater.from(mContext).inflate(R.layout.widget_no_notice_tip, this, false);
        mNoticeAnnoContentTv = noNoticePage.findViewById(R.id.notity_anno_content_tv);

        this.addView(noNoticePage);

        this.addView(emptyPage);
        this.addView(errorPage);
        this.addView(networkPage);
        this.addView(loadingPage);

        setViewClick(networkPage, errorPage);
    }

    private void setViewClick(View... clickView) {
        for (int i = 0; i < clickView.length; i++) {
            clickView[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onReload(v);
                    }
                }
            });
        }
    }

    public  void setStatus(@Flavour int status) {
        this.state = status;
        if (null != contentView) {
            contentView.setVisibility(View.GONE);
        }
        setVisibility(View.VISIBLE);

        if (status != LOADING) {
            stopLoadAnim();
        }

        switch (status) {
            case SUCCESS:
                emptyPage.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                networkPage.setVisibility(View.GONE);
                loadingPage.setVisibility(View.GONE);
                if (null != contentView) {
                    setVisibility(View.GONE);
                    contentView.setVisibility(View.VISIBLE);
                }
                break;

            case LOADING:
                startLoadAnim();
                emptyPage.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                networkPage.setVisibility(View.GONE);
                loadingPage.setVisibility(View.VISIBLE);
                break;

            case EMPTY:
                emptyPage.setVisibility(View.VISIBLE);
                errorPage.setVisibility(View.GONE);
                networkPage.setVisibility(View.GONE);
                loadingPage.setVisibility(View.GONE);
                break;

            case ERROR:
                emptyPage.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                networkPage.setVisibility(View.GONE);
                loadingPage.setVisibility(View.GONE);
                break;

            case NO_NETWORK:
                emptyPage.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                networkPage.setVisibility(View.VISIBLE);
                loadingPage.setVisibility(View.GONE);
                break;

            case NO_NOTICE:
            case NO_ANNO:
                emptyPage.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                networkPage.setVisibility(View.GONE);
                loadingPage.setVisibility(View.GONE);
                noNoticePage.setVisibility(View.VISIBLE);
                //设置通知或公告文案
                mNoticeAnnoContentTv.setText(status == NO_NOTICE ? R.string.mall_notice_no_content : R.string.mall_anno_no_content);
                break;
        }
    }

    /**
     * 返回当前状态{SUCCESS, EMPTY, ERROR, NO_NETWORK, LOADING}
     */
    public int getStatus() {
        return state;
    }

    public PlaceholderLayout setOnReloadListener(OnReloadListener listener) {
        this.listener = listener;
        return this;
    }

    public void setContentView(View view) {
        this.contentView = view;
        setMatchParent();
    }

    private void setMatchParent() {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.setLayoutParams(layoutParams);
    }


    @IntDef({SUCCESS, EMPTY, ERROR, NO_NETWORK, LOADING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flavour {
    }

    public interface OnReloadListener {
        void onReload(View v);
    }

    private Animation rotate;

    private void startLoadAnim() {
    }

    private void stopLoadAnim() {
    }
}
