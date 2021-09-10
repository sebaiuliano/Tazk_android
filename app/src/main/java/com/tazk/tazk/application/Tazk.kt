package com.tazk.tazk.application

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.tazk.tazk.application.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Tazk: SplitCompatApplication() {
    override fun onCreate() {
        super.onCreate()
        initiateKoin()
    }

    private fun initiateKoin(){
        startKoin {
//            //Declaro el uso de propiedades
//            fileProperties()
//            //Declaro el logger en DEBUG
//            androidLogger(Level.DEBUG)
            //Declaro el uso de AndroidContext
            androidContext(this@Tazk)
            //Declaro los modulos
            modules(listOf(
                //Modulo de los ViewModels
                viewModelModule
            ))
        }
    }
}