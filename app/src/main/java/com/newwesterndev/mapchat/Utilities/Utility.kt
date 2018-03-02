package com.newwesterndev.mapchat.Utilities

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by philip on 3/1/18.
 */
class Utility {

    fun clearDisposables(compositeDisposable: CompositeDisposable, disposable: Disposable) {
        compositeDisposable.add(disposable)
        compositeDisposable.clear()
    }
}