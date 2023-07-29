package com.example.cloudrent.variabel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuVariabel(
    val menu:String,
    val id: Int,
    val jumlah: Int,
    var isSelected: Boolean = false
) : Parcelable

