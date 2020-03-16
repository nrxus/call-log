package com.nrxus.calllog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class CallLogViewModel(application: Application) : AndroidViewModel(application) {
    val callLog: LiveData<Result<CallLog, Unit>>
        get() = _callLog

    private val _callLog: CallLogLiveData by lazy { CallLogLiveData(application) }

    fun permissionsGranted() {
        _callLog.triggerChange()
    }
}
