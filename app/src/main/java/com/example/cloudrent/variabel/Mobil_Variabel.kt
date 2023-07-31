package com.example.cloudrent.variabel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mobil_Variabel(
    val fotomobil: Int,
    val namamobil:String,
    val hargamobil:String
) : Parcelable
