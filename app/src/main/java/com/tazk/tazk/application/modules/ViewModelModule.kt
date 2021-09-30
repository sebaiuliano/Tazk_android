package com.tazk.tazk.application.modules

import com.tazk.tazk.ui.login.LoginViewModel
import com.tazk.tazk.ui.main.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get(), get()) }
}