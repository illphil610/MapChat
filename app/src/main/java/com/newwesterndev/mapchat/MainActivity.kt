package com.newwesterndev.mapchat

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.newwesterndev.mapchat.Adapter.DataAdapter
import com.newwesterndev.mapchat.Fragments.PartnerListFragment
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Network.RequestInterface
import com.newwesterndev.mapchat.Utilities.Utility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : Activity(), DataAdapter.Listener {

    private var mCompositeDisposable = CompositeDisposable()
    private val mUtility = Utility()
    private lateinit  var mDisposable : Disposable
    private lateinit  var mUserArrayList: ArrayList<Model.User>
    private lateinit  var mAdapter: DataAdapter
    private lateinit var mRequestInterface : RequestInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {

            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.mapchat_nav_fragment, PartnerListFragment.newInstance())
            fragmentTransaction.commit()

        }

    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    /*
    private fun initRecyclerView() {
        mapchat_user_list.setHasFixedSize(true)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        mapchat_user_list.layoutManager = layoutManager
    }
    */

    private fun loadJSON(): RequestInterface {
        val requestInterface = Retrofit.Builder()
                .baseUrl(getString(R.string.kamorris))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)
        mCompositeDisposable
                .add(requestInterface.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
        return requestInterface
    }

    private fun handleResponse(userList: List<Model.User>) {
        Toast.makeText(this, "Updating list", Toast.LENGTH_SHORT).show()
        mUserArrayList = ArrayList(userList)
        mAdapter = DataAdapter(mUserArrayList, this)
        //mapchat_user_list.adapter = mAdapter
    }

    private fun handleError(error: Throwable) {
        Log.d(MainActivity::class.java.simpleName, error.localizedMessage)
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    private fun pollServer(requestInterface: RequestInterface) {
        mDisposable = Observable.interval(30, TimeUnit.SECONDS)
                .flatMap { requestInterface.getUsers() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
    }

    override fun onItemClick(user: Model.User) {
        Toast.makeText(this, "${user.username}, ${user.latitude}, " +
                user.longitude, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        mRequestInterface = loadJSON()
        pollServer(mRequestInterface)
    }

    override fun onPause() {
        super.onPause()
        mUtility.clearDisposables(mCompositeDisposable, mDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mUtility.clearDisposables(mCompositeDisposable, mDisposable)
    }
}
