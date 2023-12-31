package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rxjava.model.service.UserService
import com.example.rxjava.viewmodel.RxViewModelFactory

object ViewModelFactory : RxViewModelFactory() {

    private val userService: UserService by lazy {
        apiHelper.create(UserService::class.java)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                return modelClass.getDeclaredConstructor(UserService::class.java)
                    .newInstance(userService)
            }
        }
        return super.create(modelClass)
    }
}