package com.newwesterndev.mapchat

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.newwesterndev.mapchat.Adapter.DataAdapter
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Network.RequestInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : Activity(), DataAdapter.Listener {

    private val TAG = MainActivity::class.java.simpleName
    private val BASE_URL = "https://kamorris.com"
    private var mCompositeDisposable: CompositeDisposable? = null
    private var mUserArrayList: ArrayList<Model.User>? = null
    private var mAdapter: DataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        mCompositeDisposable = CompositeDisposable()
        initRecyclerView()
        loadJSON()
    }

    private fun initRecyclerView() {
        mapchat_user_list.setHasFixedSize(true)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        mapchat_user_list.layoutManager = layoutManager
    }

    private fun loadJSON() {
        val requestInterface = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        mCompositeDisposable?.add(requestInterface.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(userList: List<Model.User>) {
        mUserArrayList = ArrayList(userList)
        mAdapter = DataAdapter(mUserArrayList!!, this)
        mapchat_user_list.adapter = mAdapter
    }

    private fun handleError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)
        Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(user: Model.User) {
        Toast.makeText(this, "${user.username} Clicked !", Toast.LENGTH_SHORT).show()
    }
}
