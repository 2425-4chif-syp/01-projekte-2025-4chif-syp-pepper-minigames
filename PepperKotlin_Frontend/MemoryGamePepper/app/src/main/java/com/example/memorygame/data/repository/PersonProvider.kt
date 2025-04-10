package com.example.memorygame.data.repository

import com.example.memorygame.data.model.PersonIntent

interface PersonProvider {
    fun getPerson(): PersonIntent
}
