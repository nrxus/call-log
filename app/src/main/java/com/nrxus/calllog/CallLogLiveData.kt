package com.nrxus.calllog

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallLogLiveData(private val context: Context) : MutableLiveData<Result<CallLog, Unit>>() {
    private val observer: ContentObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch { loadData() }
        }
    }
    private var job: Job? = null

    override fun onActive() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            postValue(Result.Err(Unit))
            return
        }

        job = CoroutineScope(Dispatchers.Main).launch { loadData() }
        context.contentResolver.registerContentObserver(
            AndroidCalls.CONTENT_URI, true, observer
        )
    }

    override fun onInactive() {
        job?.cancel()
        context.contentResolver.unregisterContentObserver(observer)
    }

    fun triggerChange(): Unit = observer.onChange(true)

    private suspend fun loadData() {
        withContext(Dispatchers.IO) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALL_LOG
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                postValue(Result.Err(Unit))
            }

            CallLogBuilder().build(context.contentResolver)?.also { postValue(Result.Ok(it)) }
        }
    }
}
