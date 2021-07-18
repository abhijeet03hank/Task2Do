package com.hank.task2do.model

import android.os.Parcelable
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Task (

    val title : String? = null,
    val comment :String? = null,
    val timer : Date? = null,
    val status: Status? = null
        ):Parcelable

