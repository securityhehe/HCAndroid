package com.hc.load.epoch.callback;

import java.util.Observable;

/**
 * @Author : ZhangHe
 * @Time : 2020/5/6 15:07
 * @Desc : 用来进行Epoch风控的同步情况的回调来提交订单
 */
public class ObservableObject extends Observable {
    private static ObservableObject instance = new ObservableObject();

    public static ObservableObject getInstance() {
        return instance;
    }

    private ObservableObject() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}