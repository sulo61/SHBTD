package io.sulek.schibstedtask.models

sealed class Content<out R> {

    data class Success<out T>(val data: T) : Content<T>()
    data class Failure(val throwable: Throwable) : Content<Nothing>()
    object Loading : Content<Nothing>()
    object Idle : Content<Nothing>()

}