package com.hc.load

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LoanViewModel(app: Application) : AndroidViewModel(app){
    var status =  MutableLiveData<Int>()
    fun doGetMoney(){
        status.value = 1
    }



}
