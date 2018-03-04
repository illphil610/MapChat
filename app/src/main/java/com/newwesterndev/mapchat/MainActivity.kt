package com.newwesterndev.mapchat

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.widget.Toast
import com.newwesterndev.mapchat.Adapter.DataAdapter
import com.newwesterndev.mapchat.Fragments.PartnerListFragment
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Network.RequestInterface
import com.newwesterndev.mapchat.Utilities.Utility
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MainActivity : Activity(), DataAdapter.Listener {

    private var mCompositeDisposable = CompositeDisposable()
    private val mUtility = Utility(this)
    private lateinit var mDisposable: Disposable
    private var mUserArrayList: ArrayList<Model.User> = ArrayList()
    private lateinit var mRequestInterface: RequestInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager.inTransaction {
            replace(R.id.mapchat_nav_fragment, PartnerListFragment.newInstance())
        }
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    override fun onItemClick(user: Model.User) {
        Toast.makeText(this, "${user.username}, ${user.latitude}, " +
                user.longitude, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        mRequestInterface = mUtility.loadJSON()
        mDisposable = mUtility.pollServer(mRequestInterface)
    }

    override fun onStop() {
        super.onStop()
        mUtility.clearDisposables(mCompositeDisposable, mDisposable)
    }
}
