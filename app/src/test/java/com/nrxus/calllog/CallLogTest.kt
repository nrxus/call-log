package com.nrxus.calllog

import android.database.Cursor
import android.telephony.PhoneNumberUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.util.Date
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CallLogTest {
    @BeforeEach
    @Suppress("DEPRECATION")
    fun setup() {
        // mock the static call to PhoneNumberUtils
        // consider moving this to a class that is better built for testing
        mockkStatic(PhoneNumberUtils::class)
        every { PhoneNumberUtils.formatNumber(any()) } answers {
            this.arg(0)
        }
    }

    @Test
    fun `Entry#fromCursor converts a cursor into a Entry`() {
        val cursor = mockk<Cursor>()
        val date = Date()

        every { cursor.getInt(0) } returns AndroidCalls.OUTGOING_TYPE
        every { cursor.getString(1) } returns "5552345"
        every { cursor.getLong(2) } returns date.time

        val entry = CallLog.Entry.fromCursor(cursor)

        assertEquals(
            CallLog.Entry(
                number = "5552345",
                direction = CallLog.Direction.Outgoing,
                time = date
            ),
            entry
        )
    }

    // TODO: Investigate using table/parameterized tests
    @Test
    fun `Direction#fromType maps android call types into a direction`() {
        assertEquals(
            CallLog.Direction.fromType(AndroidCalls.OUTGOING_TYPE),
            CallLog.Direction.Outgoing
        )
        assertEquals(
            CallLog.Direction.fromType(AndroidCalls.INCOMING_TYPE),
            CallLog.Direction.Incoming
        )
        assertEquals(
            CallLog.Direction.fromType(AndroidCalls.ANSWERED_EXTERNALLY_TYPE),
            CallLog.Direction.Incoming
        )
        assertEquals(
            CallLog.Direction.fromType(AndroidCalls.BLOCKED_TYPE),
            CallLog.Direction.Incoming
        )
        assertEquals(
            CallLog.Direction.fromType(AndroidCalls.MISSED_TYPE),
            CallLog.Direction.Incoming
        )
        assertEquals(
            CallLog.Direction.fromType(AndroidCalls.REJECTED_TYPE),
            CallLog.Direction.Incoming
        )
        assertEquals(
            CallLog.Direction.fromType(AndroidCalls.VOICEMAIL_TYPE),
            CallLog.Direction.Incoming
        )
    }
}
