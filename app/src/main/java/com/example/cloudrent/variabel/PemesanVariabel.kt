package com.example.cloudrent.variabel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PemesanVariabel(
    var nama:String,
    var email:String
) : Parcelable
