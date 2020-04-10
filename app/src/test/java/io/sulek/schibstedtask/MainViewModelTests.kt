package io.sulek.schibstedtask

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.sulek.schibstedtask.data.GitRepository
import io.sulek.schibstedtask.models.Content
import io.sulek.schibstedtask.ui.MainViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`


@RunWith(JUnit4::class)
class MainViewModelTests {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val gitRepository = mock<GitRepository>()
    private val viewModel = MainViewModel(gitRepository, Schedulers.trampoline())

    @Test
    fun `request query and expect success`() {
        val query = "schibsted"
        `when`(gitRepository.find(query)).thenReturn(Single.just(emptyList()))
        viewModel.search(query)
        viewModel.users.test()
            .awaitNextValue()
            .assertValue { it is Content.Loading }
            .awaitNextValue()
            .assertValue { it is Content.Success }
    }

    @Test
    fun `request query and expect failure`() {
        val query = "schibsted"
        `when`(gitRepository.find(query)).thenReturn(Single.error(Throwable("arr!")))
        viewModel.search(query)
        viewModel.users.test()
            .awaitNextValue()
            .assertValue { it is Content.Loading }
            .awaitNextValue()
            .assertValue { it is Content.Failure }
    }

}