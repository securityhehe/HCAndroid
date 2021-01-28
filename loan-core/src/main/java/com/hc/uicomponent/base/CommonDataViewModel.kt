package com.hc.uicomponent.base

import androidx.lifecycle.MutableLiveData

class CommonDataViewModel : BaseViewModel(){
    var bankData =  MutableLiveData<String?>()
}