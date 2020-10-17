package ru.den.free.neuronet3.app

import android.app.Application


fun app() : App = App.instance!!

class App : Application() {
    companion object{
        var instance : App? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}