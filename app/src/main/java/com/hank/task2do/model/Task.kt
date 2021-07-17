package com.hank.task2do.model

import java.util.*


data class Task (
    val title : String,
    val comment :String,
    val timer : Date,
    val status: Status
        )