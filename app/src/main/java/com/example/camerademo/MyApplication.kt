package com.example.camerademo

import android.app.Application
import com.example.camerademo.util.FileClassMethodTag
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // Timber注册
        if (BuildConfig.DEBUG) {
            Timber.plant(FileClassMethodTag())
        }
    }

}