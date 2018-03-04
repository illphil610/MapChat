package com.newwesterndev.mapchat.Utilities

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import android.provider.Settings.Global.getString
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus
import com.newwesterndev.mapchat.Network.RequestInterface
import com.newwesterndev.mapchat.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.AccessControlContext

/**
 * Created by philip on 3/1/18.
 */
class Utility(context: Context) {

    val mContext = context

    fun clearDisposables(compositeDisposable: CompositeDisposable, disposable: Disposable) {
        compositeDisposable.add(disposable)
        compositeDisposable.clear()
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    fun loadJSON(): RequestInterface {
        val requestInterface = Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.kamorris))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)
        return requestInterface
    }
}