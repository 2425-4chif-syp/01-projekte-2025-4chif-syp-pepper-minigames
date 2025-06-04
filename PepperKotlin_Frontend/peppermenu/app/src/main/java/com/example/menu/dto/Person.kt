package com.example.menu.dto

data class Person(
    val pid: Number,
    val firstName: String,
    val lastName: String,
    // True = Mann
    // False = Frau
    val gender: Boolean,
    val isWorker: Boolean
)