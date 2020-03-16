package com.nrxus.calllog

import android.database.Cursor
import android.os.Build
import android.telephony.PhoneNumberUtils
import java.util.Date
import java.util.Locale

typealias AndroidCalls = android.provider.CallLog.Calls

class CallLog(val entries: List<Entry>) {
    data class Entry(
        val number: String,
        val direction: Direction,
        val time: Date
    ) {
        companion object {
            val PROJECTION = arrayOf(AndroidCalls.TYPE, AndroidCalls.NUMBER, AndroidCalls.DATE)

            /**
             * Returns an CallLog.Entry from a Cursor
             * This cursor must have been obtained using the exposed CallLog.Entry.PROJECTION array
            */
            fun fromCursor(cursor: Cursor): Entry {
                val direction = Direction.fromType(cursor.getInt(0))
                val number = formatPhone(cursor.getString(1))
                val date = cursor.getLong(2)

                return Entry(number, direction, Date(date))
            }

            @Suppress("DEPRECATION")
            private fun formatPhone(number: String): String {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    PhoneNumberUtils.formatNumber(number, Locale.getDefault().country)
                } else {
                    PhoneNumberUtils.formatNumber(number)
                } ?: number
            }
        }
    }

    enum class Direction {
        Incoming,
        Outgoing;

        companion object {
            fun fromType(type: Int): Direction = when (type) {
                AndroidCalls.OUTGOING_TYPE -> Outgoing
                else -> Incoming
            }
        }
    }
}
