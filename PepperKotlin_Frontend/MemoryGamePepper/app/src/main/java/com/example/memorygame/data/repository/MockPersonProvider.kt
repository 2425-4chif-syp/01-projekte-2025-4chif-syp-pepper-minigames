package com.example.memorygame.data.repository

import com.example.memorygame.data.model.Person
import com.example.memorygame.data.model.PersonIntent

class MockPersonProvider : PersonProvider {
    override fun getPerson(): PersonIntent {
        return PersonIntent(
            id = 1,
            firstName = "Anna",
            lastName = "Müller"
        )
    }
}
