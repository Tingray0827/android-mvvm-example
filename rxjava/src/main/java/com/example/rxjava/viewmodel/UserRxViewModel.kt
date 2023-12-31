package com.example.rxjava.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava3.cachedIn
import androidx.paging.rxjava3.flowable
import com.example.base.model.data.PageData
import com.example.base.model.data.PagingModel
import com.example.rxjava.model.pagingsource.IntRxPagingSource
import com.example.rxjava.model.service.UserService
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
abstract class UserRxViewModel(private val userService: UserService) : ViewModel() {
    protected val disposable = CompositeDisposable()

    protected fun createUserPager() =
        Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                IntRxPagingSource(
                    singleSource = ::getUserInformation,
                    startedIndex = 1
                )
            }
        ).flowable.cachedIn(viewModelScope)

    private fun getUserInformation(currentPage: Int) =
        Singles.zip(
            userService.queryUserInformation(currentPage),
            userService.queryUserNameColor(currentPage)
        )
            .subscribeOn(Schedulers.io())
            .map { result ->
                val userResponse = result.first
                val extraResponse = result.second

                if (!userResponse.isSuccessful ||
                    !extraResponse.isSuccessful ||
                    userResponse.body()?.data == null ||
                    extraResponse.body()?.data == null
                ) {
                    throw Throwable("Response is invalid.")
                }
                Pair(userResponse.body()!!, extraResponse.body()!!)
            }
            .map<List<PagingModel>> { result ->
                val userBody = result.first
                val extraBody = result.second
                userBody.data!!.zip(extraBody.data!!).map {
                    val userData = it.first
                    val extraData = it.second

                    PagingModel.Content(userData.apply {
                        if (id == extraData.id) {
                            extra = extraData
                        }
                        page = PageData(
                            page = userBody.page,
                            perPage = userBody.perPage,
                            total = userBody.total,
                            totalPages = userBody.totalPages
                        )
                    })
                }
            }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}