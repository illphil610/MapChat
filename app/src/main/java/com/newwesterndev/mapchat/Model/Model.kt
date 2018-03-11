package com.newwesterndev.mapchat.Model

/**
 * Created by philip on 2/28/18.
 */
object Model {
    data class User(var username: String,
                    var latitude: Double,
                    var longitude: Double)

    data class Partner(var username: String,
                       var latitude: Double,
                       var longitude: Double,
                       var distance: Float) : Comparable<Partner> {
        override fun compareTo(other: Partner) = when {
            distance < other.distance -> -1
            distance > other.distance -> 1
            else -> 0
        }
    }

    data class UserList(var users: ArrayList<User>)
}