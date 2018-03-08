package com.newwesterndev.mapchat.Network

import com.newwesterndev.mapchat.Model.Model
import io.reactivex.Observable
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.*

/**
 * Created by philip on 2/28/18.
 */
interface RequestInterface {

    @GET("lab/get_locations.php")
    fun getUsers() : Observable<List<Model.User>>

    @POST("lab/register_location.php")
    @FormUrlEncoded
    fun addUser(
            @Field("user") username: String,
            @Field("latitude") latitude: String,
            @Field("longitude") longitude: String) : Observable<Model.User>

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
