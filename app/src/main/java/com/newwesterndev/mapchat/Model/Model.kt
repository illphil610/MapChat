package com.newwesterndev.mapchat.Model

import android.location.Location

/**
 * Created by philip on 2/28/18.
 */
object Model {
    data class User(var username: String, var latitude: Double, var longitude: Double) : Comparable<User> {
        override fun compareTo(other: User): Int {
            TODO("Location stuff not implemented")
        }
    }

    data class UserList(var users: ArrayList<User>)
}