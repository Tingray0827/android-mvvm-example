package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.example.base.model.data.PagingModel
import com.example.rxjava.model.service.UserService
import com.example.rxjava.viewmodel.UserRxViewModel
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

class UserViewModel(userService: UserService) : UserRxViewModel(userService) {
    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> = _errorLiveData

    private val _userPagingDataLiveData = MutableLiveData<PagingData<PagingModel>>()
    val userPagingDataLiveData: LiveData<PagingData<PagingModel>> = _userPagingDataLiveData

    fun queryUser() {
        createUserPager()
            .subscribeBy(
                onNext = {
                    _userPagingDataLiveData.postValue(it)
                },
                onError = {
                    _errorLiveData.postValue(it)
                }
            ).addTo(disposable)
    }
}