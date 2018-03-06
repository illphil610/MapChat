package com.newwesterndev.mapchat.Utilities

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.newwesterndev.mapchat.MainActivity
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus
import com.newwesterndev.mapchat.Network.RequestInterface
import com.newwesterndev.mapchat.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Utility(context: Context) {

    val mContext = context
    private var mUserArrayList: ArrayList<Model.User> = ArrayList()

    fun clearDisposables(compositeDisposable: CompositeDisposable, disposable: Disposable) {
        compositeDisposable.add(disposable)
        compositeDisposable.clear()
    }

    fun loadJSON(): RequestInterface {
        return Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.kamorris))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)
    }

    fun pollServer(requestInterface: RequestInterface) : Disposable {
        return Observable.interval(30, TimeUnit.SECONDS)
                .startWith(0)
                .flatMap { requestInterface.getUsers() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
    }

    private fun handleResponse(userList: List<Model.User>) {
        Toast.makeText(mContext, "Updating list", Toast.LENGTH_SHORT).show()
        mUserArrayList = ArrayList(userList)
        RxBus.publish(Model.UserList(mUserArrayList))
    }

    private fun handleError(error: Throwable) {
        Log.d(MainActivity::class.java.simpleName, error.localizedMessage)
        Toast.makeText(mContext, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    fun showToast(content: Context, message: String) {
        Toast.makeText(content, message, Toast.LENGTH_LONG).show()
    }
}