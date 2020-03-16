package com.nrxus.calllog

import android.content.ContentResolver
import android.telephony.PhoneNumberUtils
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CallLogBuilderTest {
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
    fun `returns top 50 descending on date`() {
        val contentResolver = mockk<ContentResolver>()

        every {
            contentResolver.query(
                AndroidCalls.CONTENT_URI,
                CallLog.Entry.PROJECTION,
                null,
                null,
                "${AndroidCalls.DATE} DESC"
            )
        } returns mockk {
            every { moveToNext() } returns true
            every { getInt(0) } returns AndroidCalls.INCOMING_TYPE
            every { getString(1) } returns "5555555"
            every { getLong(2) } returns 50
            every { close() } just Runs
        }

        val callLog = CallLogBuilder().build(contentResolver)
        assertEquals(callLog?.entries?.size, 50)
    }

    @Test
    fun `handles a null cursor`() {
        val contentResolver = mockk<ContentResolver>()

        every {
            contentResolver.query(
                AndroidCalls.CONTENT_URI,
                CallLog.Entry.PROJECTION,
                null,
                null,
                "${AndroidCalls.DATE} DESC"
            )
        } returns null

        assertNull(CallLogBuilder().build(contentResolver))
    }
}