package com.example.rxjava.viewmodel

import com.example.base.model.ApiHelper
import com.example.base.viewmodel.BaseViewModelFactory
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

abstract class RxViewModelFactory : BaseViewModelFactory() {
    override val apiHelper: ApiHelper by lazy {
        ApiHelper(RxJava3CallAdapterFactory.create())
    }
}