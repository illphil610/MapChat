package com.newwesterndev.mapchat.Network

import com.newwesterndev.mapchat.Model.Model
import io.reactivex.Observable
import retrofit2.http.GET
import java.util.*

/**
 * Created by philip on 2/28/18.
 */
interface RequestInterface {

    @GET("lab/get_locations.php")
    fun getUsers() : Observable<List<Model.User>>
}