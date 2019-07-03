package dev.affan.sinergi

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    companion object {
        private var mInstance: App? = null

        val instance: App?
            @Synchronized get() {
                var app: App? = null
                synchronized(App::class.java) {
                    app = mInstance
                }
                return app
            }
    }

}