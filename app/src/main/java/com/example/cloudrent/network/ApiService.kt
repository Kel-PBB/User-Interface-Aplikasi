package com.example.cloudrent.network

import android.net.Uri
import com.example.cloudrent.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface ApiService {
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/register")
    fun insertUser(
        @Field("email") email : String,
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String
    ): Call<ResponseUserRegister>

    @POST("api/login")
    fun loginUser(@Body request: LoginRequest): Call<ResponseUserLogin>

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/search")
    fun searchMobil(
        @Field("tanggal_mulai") tanggal_mulai: String,
        @Field("tanggal_selesai") tanggal_selesai: String,
        @Field("jam_pengembalian") jam_penjemputan: String
    ): Call<SearchResponse>

    @Headers("Accept: application/json")
    @GET("api/mobil/show/{kode_mobil}")
    fun detailMobil(
        @Path("kode_mobil") kode_mobil: String
    ): Call<MobilDetail>

    @Headers("Accept: application/json")
    @GET("api/pesanan/form")
    fun pesanForm(): Call<ResponseFormPesan>

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/pesanan/tambah")
    fun tambahPesanan(
        @Field("mobil_id") mobil_id: Int?,
        @Field("waktu_pengambilan") waktu_pengambilan: String?,
        @Field("tanggal_rental_mulai") tanggal_rental_mulai: String?,
        @Field("tanggal_rental_selesai") tanggal_rental_selesai: String?,
        @Field("total") total: String?,
        @Field("status_id") status_id: Int?,
        @Field("email_pemesan") email_pemesan: String?,
        @Field("no_hp") no_hp: String?,
        @Field("nama_pemesan") nama_pemesan: String?,
    ): Call<ResponseTambahPesanan>

    @Headers("Accept: application/json")
    @GET("api/pesanan/list")
    fun pesananList(): Call<ResponsePesanans>

    @Headers("Accept: application/json")
    @GET("api/user/profile")
    fun userProfile(): Call<ResponseUserProfile>

    @Headers("Accept: application/json")
    @POST("api/logout")
    fun logout(): Call<ResponseLogout>

    @Headers("Accept: application/json")
    @GET("api/beranda")
    fun beranda(): Call<ResponseBeranda>

    @Headers("Accept: application/json")
    @GET("api/pesanan/show/{kode_pesanan}")
    fun pesananShow(
        @Path("kode_pesanan") kode_pesanan: String
    ): Call<ResponsePesananShow>

    @Headers("Accept: application/json")
    @Multipart
    @POST("api/invoice/buktiTransfer")
    fun uploadBukti(
        @Part gambar: MultipartBody.Part
    ): Call<ResponseUploadBukti>

    @Headers("Accept: application/json")
    @GET("api/notification/index")
    fun notificationList(): Call<ResponseNotifications>
}