package com.example.rxjava.model.pagingsource

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import io.reactivex.rxjava3.core.Single

class IntRxPagingSource<T : Any>(
    private val singleSource: (Int) -> Single<List<T>>,
    private val startedIndex: Int = 0
) : RxPagingSource<Int, T>() {
    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, T>> {
        val position = params.key ?: startedIndex
        return singleSource(position).map {
                LoadResult.Page(
                    data = it,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = position + 1
                )
            }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}