package edu.temple.mapchat.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import edu.temple.mapchat.Model.Model
import edu.temple.mapchat.Model.RxBus
import edu.temple.mapchat.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChatActivity : AppCompatActivity() {

    private lateinit var mDisposable: Disposable
    private var mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mCompositeDisposable.add(RxBus.listen(Model.ChatMessage::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("Message From FB", it.toString())
                }))
    }

    override fun onStop() {
        super.onStop()
        //mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}
