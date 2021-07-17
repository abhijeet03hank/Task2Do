package com.hank.task2do.model

import androidx.annotation.Nullable
import java.util.*


data class Task (

    val title : String? = null,
    val comment :String? = null,
    val timer : Date? = null,
    val status: Status? = null
        )

