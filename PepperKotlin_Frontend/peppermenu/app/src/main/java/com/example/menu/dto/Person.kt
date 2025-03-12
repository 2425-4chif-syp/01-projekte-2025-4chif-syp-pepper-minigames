package com.example.menu.dto

import java.time.LocalDate

data class Person(
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val roomNo: String,
    val isWorker: Boolean,
    val password: String
)