package com.example.iiushka.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MessageEntityTest {

    @Test
    fun entityHasExpectedDefaults() {
        val msg = MessageEntity(
            text = "Привет",
            isFromUser = true,
            timestamp = 1_700_000_000_000L
        )
        assertEquals(0L, msg.id)
        assertTrue(msg.isFromUser)
        assertEquals("Привет", msg.text)
    }
}
