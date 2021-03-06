package edu.temple.mapchat.Activities

import android.Manifest
import android.app.DialogFragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.maps.MapView
import com.newwesterndev.encrypt_keeper.Utilities.RSAEncryptUtility
import edu.temple.mapchat.Fragments.AddNewUserFragment
import edu.temple.mapchat.Fragments.MapFragment
import edu.temple.mapchat.Fragments.PartnerListFragment
import edu.temple.mapchat.Model.Model
import edu.temple.mapchat.Model.RxBus
import edu.temple.mapchat.Network.RequestInterface
import edu.temple.mapchat.Utilities.Utility
import com.patloew.rxlocation.RxLocation
import edu.temple.mapchat.Fragments.AddNewPartnerFragment
import edu.temple.mapchat.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.TimeUnit
import android.util.Base64

class MainActivity : AppCompatActivity(), PartnerListFragment.PartnerListInterface, MapFragment.MapFragmentInterface
                                        , AddNewUserFragment.AddNewUserDialogListener, AddNewPartnerFragment.AddNewPartnerInterface{

    // Looking into dependency injection for this but didnt want to waste time until lab is complete
    // Dagger2 is the library i am thinking of using
    private var mCompositeDisposable = CompositeDisposable()
    private val mUtility = Utility(this)
    private var mDisposable: Disposable? = null
    private var mRxLocationDisposable: Disposable? = null
    private lateinit var mRequestInterface: RequestInterface
    private lateinit var partnerListFragment: PartnerListFragment
    private lateinit var mapFragment: MapFragment
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mAddress: Address? = null
    private lateinit var rxLocation: RxLocation
    private lateinit var locationRequest: LocationRequest
    private var mUserName: Model.User? = null
    private var mPartnerName: String? = null
    private val encryptDelegate = RSAEncryptUtility()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION), 10)

        // i dont even use this...delete when you make sure it doesnt break stuff haha
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // This is how im getting the location nonsense and blah blah blahhhhhhhh
        rxLocation = RxLocation(this)
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(10.toFloat())

        // Theres a better way to check if its landscape too...figure that out
        val mTwoPainz = findViewById<MapView>(R.id.partnerMapView) != null

        partnerListFragment = PartnerListFragment.newInstance()
        mapFragment = MapFragment.newInstance()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mapchat_nav_fragment, partnerListFragment)
        transaction.commit()

        if (mTwoPainz) {
            fragmentManager.executePendingTransactions()
            val transaction2 = fragmentManager.beginTransaction()
            transaction2.replace(R.id.partnerMapView, mapFragment).commit()
        }
    }

    override fun onPause() {
        super.onPause()
        mDisposable?.let { mCompositeDisposable.add(it) }
        mRxLocationDisposable?.let { mCompositeDisposable.add(it) }
        mCompositeDisposable.clear()
    }

    override fun onResume() {
        super.onResume()
        mRequestInterface = mUtility.loadJSON()
        mDisposable = pollServer(mRequestInterface)

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mRxLocationDisposable = rxLocation.location()
                        .updates(locationRequest)
                        .flatMap { rxLocation.geocoding().fromLocation(it).toObservable() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleLocationResponse, this::handleLocationError)
            }
        }
    }

    private fun handleLocationResponse(address: Address) {
        mAddress = address
        val currentUser = getCurrentUser()
        // this is my fail safe if they didnt make a user lol
        if (currentUser != null && currentUser.username != getString(R.string.defaultUser)) {
            postUserToServer(currentUser)
        }
    }

    private fun handleLocationError(error: Throwable) {
        Log.e("Location error", "Its all gooooooood")
    }

    override fun onStop() {
        super.onStop()
        mDisposable?.let { mCompositeDisposable.add(it) }
        mRxLocationDisposable?.let { mCompositeDisposable.add(it) }
        mCompositeDisposable.clear()
    }

    private fun pollServer(requestInterface: RequestInterface) : Disposable {
        return Observable.interval(30, TimeUnit.SECONDS)
                .startWith(0)
                .flatMap { requestInterface.getUsers() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
    }

    private fun handleResponse(userList: List<Model.User>) {
        mArrayList = ArrayList(userList)
        RxBus.publish(Model.UserList(mArrayList))
    }

    private fun handleError(error: Throwable) {
        Log.d(MainActivity::class.java.simpleName, error.localizedMessage)
    }

    override fun userItemSelected(user: Model.User) {
        // Check if username selected is saved within our shared prefs
        val sharedPref = this.getSharedPreferences("edu.temple.mapchat.PARTNER_LIST" ,Context.MODE_PRIVATE) ?: return
        val defaultValue = "Not Listed"
        val selectedUsersPublicKey = sharedPref.getString(user.username, defaultValue)
        Log.e("PublicKey", selectedUsersPublicKey)

        // User stuff
        val preferences = getSharedPreferences("edu.temple.MapChat.USER_NAME", Context.MODE_PRIVATE)
        val privateKey = preferences.getString("username_private_pem", "nah")
        val publicKey = preferences.getString("formatted_public_pem", "nak2")

        if (selectedUsersPublicKey != defaultValue) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("partner_name", user.username)
            intent.putExtra("public_key", selectedUsersPublicKey)
            intent.putExtra("myPrivateJawn", privateKey)
            intent.putExtra("myPublicJawn", publicKey)
            startActivity(intent)
        } else {
            mPartnerName = user.username
            val alert = AddNewPartnerFragment()
            alert.show(fragmentManager, "AddNewPartner")
        }
    }

    private fun postUserToServer(user: Model.User) {
        mRequestInterface.addUser(user.username, user.latitude, user.longitude)
                .enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        Log.e("POST", "YAY " + user.username)
                    }
                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        Log.e("POST", "NO")
                    }
                })
    }

    private fun postUserFCMToServer(user: Model.User, token: String) {
        mRequestInterface.addUserToken(user.username, token)
                .enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        Log.e("FCM POST", response.toString())
                        if (response?.isSuccessful!!) {
                            Log.e("FCM POST", response.toString())
                        } else {
                            Log.e("FCM POST", response.errorBody().toString())
                        }
                    }
                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        Log.e("FCM POST", "WOMP WOMP WOMPPPPP")
                    }
                })
    }

    override fun getUserArrayList(): ArrayList<Model.User> {
        return mArrayList
    }

    override fun getCurrentUser() : Model.User? {
        val preferences = getSharedPreferences("edu.temple.MapChat.USER_NAME", Context.MODE_PRIVATE)
        val user = preferences.getString("username", getString(R.string.defaultUser))
        mUtility.showToast(this, user)

        if (!user.isEmpty() && mAddress != null) {
            return Model.User(user, mAddress!!.latitude, mAddress!!.longitude)
        }
        return null
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    // **** Used for dialog fragment response communication *****
    // For adding a new username
    override fun onDialogPositiveClick(dialogFragment: DialogFragment, username: String) {
        if (mAddress != null) {
            mUserName = Model.User(username, mAddress?.latitude!!, mAddress?.longitude!!)
            val letUserName = mUserName

            // Generate public / private keys for the new user
            val keyPair = encryptDelegate.generateKey()
            val publicPEMFile = encryptDelegate.createPEMObject(keyPair.public)
            val privatePEMFile = encryptDelegate.createPrivatePEM(keyPair.private)
            Log.e("Private PEM", privatePEMFile)
            val formattedPublicPEM = encryptDelegate.formatPemPublicKeyString(publicPEMFile)
            val formattedPEM = encryptDelegate.formatPemPrivateKeyString(privatePEMFile)

            // Save username, public/private keys, and also PEM public key file for NFC exchange
            val prefs = this.getSharedPreferences("edu.temple.MapChat.USER_NAME", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            // Will replace these with a contract

            val publicKeyToString = Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT)
            val privateKeyToString = Base64.encodeToString(keyPair.private.encoded, Base64.DEFAULT)

            editor.putString("username", username)
            editor.putString("username_public", publicKeyToString)
            editor.putString("username_public_pem", publicPEMFile)
            editor.putString("username_private", privateKeyToString)
            editor.putString("username_private_pem", formattedPEM)
            editor.putString("formatted_public_pem", formattedPublicPEM)
            editor.apply()

            // get FCM from when the app was installed
            val token = prefs.getString("FCM_ID", "NO")

            if (letUserName != null && token != "NO") {
                postUserToServer(letUserName)

                // post FCM to karls server so we can chat with hats
                postUserFCMToServer(letUserName, token)
            }
        }
    }
    override fun onDialogNegativeClick(dialogFragment: DialogFragment) {
    }

    //For asking if the user wants to add a new partner (save public key)
    override fun onPartnerDialogPositiveClick(dialogFragment: DialogFragment) {
        val intent = Intent(this, KeyExchangeActivity::class.java)
        intent.putExtra("partnerName", mPartnerName)
        startActivity(intent)
    }
    override fun onPartnerDialogNegativeClick(dialogFragment: DialogFragment) {
    }

    companion object {
        var mArrayList: ArrayList<Model.User> = ArrayList()
    }
}
