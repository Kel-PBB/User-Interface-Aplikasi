package com.example.cloudrent.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: String
)

data class ResponseUserRegister(
    @SerializedName("data") val data: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("status") val status: String
)

data class ResponseUserLogin(
    @SerializedName("token") val token: String,
    @SerializedName("status") val status: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SearchResponse(
    @SerializedName("mobil") val mobil: List<Mobil>,
    @SerializedName("message") val message: String,
    @SerializedName("search") val search: Search
)

data class MobilDetail(
    @SerializedName("days") val days: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("search") val search: Search,
    @SerializedName("mobil") val mobil: MobilClass,
    @SerializedName("message") val message: String
)

data class ResponseFormPesan(
    @SerializedName("user") val user: User,
    @SerializedName("kode_mobil") val kode_mobil: String,
    @SerializedName("days") val days: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("search") val search: Search,
)

data class ResponseTambahPesanan(
    @SerializedName("pesanan") val pesanan: Pesanan,
    @SerializedName("message") val message: String,
)

data class ResponsePesanans(
    @SerializedName("pesanans") val pesanans: List<Pesanans>
)

data class Pesanan(
    @SerializedName("kode_pesanan") val kode_pesanan: String,
    @SerializedName("mobil_id") val mobil_id: String,
    @SerializedName("waktu_pengambilan") val waktu_pengambilan: String,
    @SerializedName("tanggal_rental_mulai") val tanggal_rental_mulai: String,
    @SerializedName("tanggal_rental_selesai") val tanggal_rental_selesai: String,
    @SerializedName("total") val total: String,
    @SerializedName("nama_pemesan") val nama_pemesan: String,
    @SerializedName("email_pemesan") val email_pemesan: String,
    @SerializedName("no_hp") val no_hp: String,
    @SerializedName("status_id") val status_id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("update_at") val update_at: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("id") val id: Int,
)

data class Pesanans(
    @SerializedName("id") val id: Int,
    @SerializedName("kode_pesanan") val kode_pesanan: String,
    @SerializedName("mobil_id") val mobil_id: String,
    @SerializedName("waktu_pengambilan") val waktu_pengambilan: String,
    @SerializedName("tanggal_rental_mulai") val tanggal_rental_mulai: String,
    @SerializedName("tanggal_rental_selesai") val tanggal_rental_selesai: String,
    @SerializedName("total") val total: String,
    @SerializedName("nama_pemesan") val nama_pemesan: String,
    @SerializedName("email_pemesan") val email_pemesan: String,
    @SerializedName("no_hp") val no_hp: String,
    @SerializedName("status_id") val status_id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("update_at") val update_at: String,
    @SerializedName("status") val status: Status,
    @SerializedName("mobil") val mobil: Mobil
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("kode_user") val kode_ser: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
)

data class Status(
    @SerializedName("id")val id: Int,
    @SerializedName("name")val name: String,
    @SerializedName("created_at")val created_at: String?,
    @SerializedName("updated_at")val updated_at: String?,
)

data class Search(
    @SerializedName("tanggal_mulai") val tanggal_mulai: String,
    @SerializedName("tanggal_selesai") val tanggal_selesai: String,
    @SerializedName("jam_pengembalian") val jam_pengembalian: String
)

data class MobilClass(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("kode_mobil") val kode_mobil: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("harga") val harga: String,
    @SerializedName("mesin") val mesin: String,
    @SerializedName("bahan_bakar") val bahan_bakar: String,
    @SerializedName("transmisi") val transmisi: String,
    @SerializedName("seat") val seat: String,
    @SerializedName("tanggal_tersedia") val tanggal_tersedia: String?,
    @SerializedName("gambar") val gambar: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("updated_at") val updated_at: String?
)

data class Mobil(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("kode_mobil") val kode_mobil: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("harga") val harga: String,
    @SerializedName("mesin") val mesin: String,
    @SerializedName("bahan_bakar") val bahan_bakar: String,
    @SerializedName("transmisi") val transmisi: String,
    @SerializedName("seat") val seat: String,
    @SerializedName("tanggal_tersedia") val tanggal_tersedia: String?,
    @SerializedName("gambar") val gambar: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("updated_at") val updated_at: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nama)
        parcel.writeString(kode_mobil)
        parcel.writeString(brand)
        parcel.writeString(harga)
        parcel.writeString(mesin)
        parcel.writeString(bahan_bakar)
        parcel.writeString(transmisi)
        parcel.writeString(seat)
        parcel.writeString(tanggal_tersedia)
        parcel.writeString(gambar)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mobil> {
        override fun createFromParcel(parcel: Parcel): Mobil {
            return Mobil(parcel)
        }

        override fun newArray(size: Int): Array<Mobil?> {
            return arrayOfNulls(size)
        }
    }
}

