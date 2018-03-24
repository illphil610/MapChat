package edu.temple.mapchat.Model

import java.security.KeyPair

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

    data class ChatMessage(var sendToUser: String,
                           var sentFromUser: String,
                           var message: String)

    data class ProviderKeys(val keys: KeyPair,
                           val publicKeyAsString: String,
                           val privateKeyAsString: String)
}