package io.sulek.schibstedtask.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.sulek.schibstedtask.data.GitRepository
import io.sulek.schibstedtask.models.Content
import io.sulek.schibstedtask.models.User
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val gitRepository: GitRepository,
    private val observableScheduler: Scheduler
) : ViewModel() {

    private val _users = MutableLiveData<Content<List<User>>>()
    private val disposables = CompositeDisposable()
    private val searchSubject = BehaviorSubject.create<String>()
    private var findUserDisposable: Disposable? = null

    val users: LiveData<Content<List<User>>>
        get() = _users

    init {
        disposables.add(
            searchSubject.debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(observableScheduler)
                .subscribe { findUser(it) }
        )
    }

    override fun onCleared() {
        disposables.clear()
    }

    fun search(query: String) {
        searchSubject.onNext(query)
    }

    private fun findUser(query: String) {
        _users.value = Content.Loading
        findUserDisposable?.dispose()
        findUserDisposable = gitRepository.find(query)
            .doOnSubscribe { disposables.add(it) }
            .observeOn(observableScheduler)
            .subscribe(
                {
                    _users.postValue(Content.Success(it))
                },
                {
                    _users.postValue(Content.Failure(it))
                }
            )
    }

}