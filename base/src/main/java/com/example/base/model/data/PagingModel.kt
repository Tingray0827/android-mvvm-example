package com.example.base.model.data

sealed class PagingModel {
    object PageNumber : PagingModel()
    class Content<T>(val data: T): PagingModel()
}