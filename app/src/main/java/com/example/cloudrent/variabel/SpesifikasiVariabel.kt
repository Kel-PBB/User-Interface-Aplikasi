package com.example.cloudrent.variabel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpesifikasiVariabel(
    val fotospek: Int,
    val judul_spek:String,
    val isi_spek:String
) : Parcelable

