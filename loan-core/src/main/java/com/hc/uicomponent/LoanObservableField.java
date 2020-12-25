package com.hc.uicomponent;


import androidx.databinding.ObservableField;

public class LoanObservableField<T> extends ObservableField<T> {
    public void set(T value) {
        super.set(value);
        t.callT(value);
    }
    private CallT<T> t;
    public LoanObservableField<T> setCallT(CallT<T> call){

       this.t = call;
       return this;
    }
    public interface CallT<T>{
        void callT(T value);
    }
}

