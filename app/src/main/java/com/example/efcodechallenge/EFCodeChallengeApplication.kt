package com.example.efcodechallenge

import android.app.Application
import com.example.efcodechallenge.koin.appModule
import org.koin.android.ext.android.startKoin

class EFCodeChallengeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}