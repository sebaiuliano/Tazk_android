package com.tazk.tazk.application

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.tazk.tazk.application.modules.*
import com.tazk.tazk.application.modules.entities.taskModule
import com.tazk.tazk.util.services.WifiService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Tazk: SplitCompatApplication() {
    override fun onCreate() {
        super.onCreate()
        initiateKoin()
        initiateServices()
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
                //Modulos de las entidades
                taskModule,
                //Modulo de la bd
                databaseModule,
                //Modulo de los ViewModels
                viewModelModule,
                //Modulo de API
                networkModule, apiRepositoryModule, apiModule
            ))
        }
    }

    private fun initiateServices(){
        WifiService.instance.initializeWithApplicationContext(this)
    }
}