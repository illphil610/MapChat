package edu.temple.mapchat.Network

import edu.temple.mapchat.Model.Model
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface RequestInterface {

    @GET("lab/get_locations.php")
    fun getUsers() : Observable<List<Model.User>>

    @POST("lab/register_location.php")
    @FormUrlEncoded
    fun addUser(
            @Field("user") username: String,
            @Field("latitude") latitude: Double,
            @Field("longitude") longitude: Double) : Call<Void>

    @POST("lab/fcm_register.php")
    @FormUrlEncoded
    fun addUserToken(
            @Field("user") username: String,
            @Field("token") fcm_token: String) : Call<Void>

    companion object Factory
        fun create(): RequestInterface {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://kamorris.com")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(RequestInterface::class.java)
        }
}
