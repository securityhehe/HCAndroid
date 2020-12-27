package com.hc.uicomponent;


import androidx.databinding.ObservableField;

public class LoanObservableField<T> extends ObservableField<T> {

    public LoanObservableField(T value) {
        super(value);
    }

    /**
     * Creates an empty observable object
     */
    public LoanObservableField() {
        super();
    }

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

