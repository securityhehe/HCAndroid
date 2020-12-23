package com.hc.uicomponent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MWebView extends WebView {

    private Paint mPaint;
    private int mWidth = 0;
    private int progress = 0;

    /**
     * 是否显示加载动画
     */
    private boolean isShowPro = true;


    public MWebView(Context context) {
        super(context);
        init();
    }

    public MWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initWebViewSettings();
        setWebViewClient(client);
        setWebChromeClient(mWebChromeClient);

        mPaint = new Paint();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        int strokeWidth = dip2px(getContext(), 5);
        mPaint.setStrokeWidth(strokeWidth);
        // 圆角
        mPaint.setPathEffect(new CornerPathEffect(3));

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取宽度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getWidth();

    }

    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    };

    /**
     * 绘制加载进度
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (progress != 100 && isShowPro) {
            canvas.drawLine(0, 0, mWidth * progress / 100, 0, mPaint);
        }
    }

    /**
     *
     */
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
            try {
                progress = i;
                invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 初始化
     */
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(false);
        webSetting.setDatabaseEnabled(false);
        webSetting.setGeolocationEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // 设置 可以放大缩小
        webSetting.setBuiltInZoomControls(false);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // 获取屏幕的大小
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        if (height > width) {
            // 适应横屏
            setInitialScale(32);
        } else {
            // 适应竖屏
            setInitialScale(39);
        }
        //target 23 default false, so manual set true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }

    /**
     * 设置是否显示 加载进度条
     *
     * @param isShowPro
     */
    public void setShowPro(boolean isShowPro) {
        this.isShowPro = isShowPro;
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        try {
            this.progress = progress;
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebViewClient getWebViewClient() {
        return client;
    }

    public WebChromeClient getWebChromeClient() {
        return mWebChromeClient;
    }

}
