package io.sulek.schibstedtask.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.sulek.schibstedtask.Constants
import io.sulek.schibstedtask.R
import io.sulek.schibstedtask.models.Content
import io.sulek.schibstedtask.models.User
import io.sulek.schibstedtask.ui.views.DataStateLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Dzień dobry,
 * Zbudowałem aplikacje w oparciu o MVVM i Koin'a
 * - Jeżeli chodzi o MVP jest mi bardzo dobrze znane Presenter <-> Contract <-> View,
 * zasadnicza zmiana mvvm a mvp to że presenter woła funkcje contractu views, nie będzie w tym wypadku
 * listenera (chyba że chcemy), dodatkowo warto pamiętać o attach i detach contractu view i pilnowaniu
 * disposable od rx'a. Presenter tworzony przez DI podobnie jak tutaj ViewModel, z tym że presenter
 * jako interfejs contractu
 * - Dużo pracowałem z Daggerem, setup Koin'a zmigrowałem z innego projektu
 * żeby móc zacząć pisać już właściwy kod, z Daggera są mi znane komponenty, moduły, submoduły,
 * provide i binds
 */
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private var adapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSearch()
        initRecycler()
        initObservers()
    }

    override fun onDestroy() {
        recyclerView.adapter = null
        super.onDestroy()
    }

    private fun initSearch() {
        fieldQuery.doAfterTextChanged {
            mainViewModel.search(
                it?.toString() ?: Constants.EMPTY_STRING
            )
        }
    }

    private fun initRecycler() {
        adapter = MainAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initObservers() {
        mainViewModel.users.observe(this, Observer { onUsers(it) })
    }

    private fun onUsers(user: Content<List<User>>) = when (user) {
        is Content.Success -> with(user.data) {
            if (isEmpty()) dataStateLayout.changeState(DataStateLayout.State.EMPTY)
            else {
                dataStateLayout.changeState(DataStateLayout.State.DATA)
                adapter?.updateItems(this)
            }
        }
        is Content.Failure -> dataStateLayout.changeState(DataStateLayout.State.FAILURE)
        Content.Loading -> dataStateLayout.changeState(DataStateLayout.State.LOADING)
        Content.Idle -> dataStateLayout.changeState(DataStateLayout.State.EMPTY)
    }


}
