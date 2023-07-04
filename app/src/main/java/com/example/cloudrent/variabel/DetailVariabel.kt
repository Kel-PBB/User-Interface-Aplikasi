package com.example.cloudrent.variabel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailVariabel(
    val fotomobil: Int,
    val nama_mobil:String,
    val isi_spek:String
) : Parcelable