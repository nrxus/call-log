package com.nrxus.calllog

import android.Manifest
import android.content.ContentResolver
import android.database.Cursor
import androidx.annotation.RequiresPermission

class CallLogBuilder {
    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    fun build(contentResolver: ContentResolver): CallLog? {
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                AndroidCalls.CONTENT_URI,
                CallLog.Entry.PROJECTION,
                null,
                null,
                "${AndroidCalls.DATE} DESC"
            )

            if (cursor == null) {
                return null
            }

            return cursor.let { generateSequence { if (it.moveToNext()) it else null } }
                .take(50)
                .map { CallLog.Entry.fromCursor(it) }
                .let { CallLog(it.toList()) }
        } finally {
            cursor?.close()
        }
    }
}
