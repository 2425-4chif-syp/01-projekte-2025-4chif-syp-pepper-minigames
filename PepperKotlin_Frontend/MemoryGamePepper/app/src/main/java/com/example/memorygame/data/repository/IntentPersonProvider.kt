package com.example.memorygame.data.repository

import android.content.Intent
import com.example.memorygame.data.model.PersonIntent

class IntentPersonProvider(private val intent: Intent) : PersonProvider {
    override fun getPerson(): PersonIntent {
        return PersonIntent(
            id = intent.getLongExtra("id", -1),
            firstName = intent.getStringExtra("firstName") ?: "",
            lastName = intent.getStringExtra("lastName") ?: "",
        )
    }
}
