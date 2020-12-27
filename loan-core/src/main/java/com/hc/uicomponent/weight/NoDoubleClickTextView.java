package com.hc.uicomponent.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Disable continuous clicks within 1000 milliseconds for AppCompatTextView
 */
public class NoDoubleClickTextView extends AppCompatTextView {
    private long clickTimeInterval = 1000;
    private long previousTime;

    public NoDoubleClickTextView(Context context) {
        super(context);
    }

    public NoDoubleClickTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoDoubleClickTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param event
     *         touch事件
     *
     * @return 是否消耗点击事件
     * true - 消耗点击事件
     * false - 不消耗点击事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                long currentTime = System.currentTimeMillis();
                if (currentTime - previousTime < clickTimeInterval) {
                    return true;
                }
                previousTime = currentTime;
                break;
        }
        return super.onTouchEvent(event);
    }
}
