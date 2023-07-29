package com.example.cloudrent.variabel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestVariabel(
    val quest:String,
) : Parcelable
