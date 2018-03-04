package com.newwesterndev.mapchat

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.newwesterndev.mapchat.Adapter.DataAdapter
import com.newwesterndev.mapchat.Fragments.PartnerListFragment
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus
import com.newwesterndev.mapchat.Network.RequestInterface
import com.newwesterndev.mapchat.Utilities.Utility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : Activity(), DataAdapter.Listener {

    private var mCompositeDisposable = CompositeDisposable()
    private val mUtility = Utility(this)
    private lateinit var mDisposable: Disposable
    private var mUserArrayList: ArrayList<Model.User> = ArrayList()
    private lateinit var mRequestInterface: RequestInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager.inTransaction { add(R.id.mapchat_nav_fragment,
                PartnerListFragment.newInstance()) }
    }

    private fun handleResponse(userList: List<Model.User>) {
        Toast.makeText(this, "Updating list", Toast.LENGTH_SHORT).show()
        mUserArrayList = ArrayList(userList)
        RxBus.publish(Model.UserList(mUserArrayList))
    }

    private fun handleError(error: Throwable) {
        Log.d(MainActivity::class.java.simpleName, error.localizedMessage)
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    private fun pollServer(requestInterface: RequestInterface) {
        mDisposable = Observable.interval(30, TimeUnit.SECONDS)
                .startWith(0)
                .flatMap { requestInterface.getUsers() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
    }

    override fun onItemClick(user: Model.User) {
        Toast.makeText(this, "${user.username}, ${user.latitude}, " +
                user.longitude, Toast.LENGTH_SHORT).show()
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    override fun onResume() {
        super.onResume()
        mRequestInterface = mUtility.loadJSON()
        pollServer(mRequestInterface)
    }

    override fun onStop() {
        super.onStop()
        mCompositeDisposable.add(mDisposable)
        mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        //mUtility.clearDisposables(mCompositeDisposable, mDisposable)
        mCompositeDisposable.add(mDisposable)
        mCompositeDisposable.clear()
    }
}
