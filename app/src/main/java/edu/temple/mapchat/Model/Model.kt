package edu.temple.mapchat.Model

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