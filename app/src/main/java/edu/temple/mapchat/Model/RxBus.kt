package edu.temple.mapchat.Model

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

// Uses object so we have a singleton instance
object RxBus {

    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}