package com.example.base.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.example.base.model.ApiHelper

abstract class BaseViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    abstract val apiHelper: ApiHelper
}