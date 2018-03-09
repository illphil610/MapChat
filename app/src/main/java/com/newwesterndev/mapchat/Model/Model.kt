package com.newwesterndev.mapchat.Model

import android.location.Location

/**
 * Created by philip on 2/28/18.
 */
object Model {
    data class User(val username: String, val latitude: String, val longitude: String) : Comparable<User> {
        override fun compareTo(other: User): Int {
            TODO("Location stuff not implemented")
        }
    }

    data class UserList(val users: ArrayList<User>)
}