package io.sulek.schibstedtask.di

import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.sulek.schibstedtask.Constants
import io.sulek.schibstedtask.data.GitApi
import io.sulek.schibstedtask.data.GitRepository
import io.sulek.schibstedtask.data.GitRepositoryImpl
import io.sulek.schibstedtask.ui.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val SUBSCRIBE_SCHEDULER = "subscribeScheduler"
private const val OBSERVE_SCHEDULER = "observeScheduler"

val viewModelModule = module {

    single(named(SUBSCRIBE_SCHEDULER)) { Schedulers.io() }
    single(named(OBSERVE_SCHEDULER)) { AndroidSchedulers.mainThread() }

    viewModel {
        MainViewModel(
            gitRepository = get(),
            observableScheduler = get(named(OBSERVE_SCHEDULER))
        )
    }

}

val dataModule = module {

    single {
        GsonBuilder().create()
    }

    single {
        OkHttpClient.Builder()
            .connectTimeout(Constants.CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .pingInterval(Constants.PING_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor {
                val request = it.request().newBuilder().apply {
                    addHeader("Content-Type", "application/json")
                    addHeader(
                        "Authorization",
                        "Basic c3VsbzYxOjUyYTFjOTBkZmIyYzY5ZWI0MmUyZDkzMzU4MTNiNDA5YTNkOWVmNzk="
                    )
                }
                it.proceed(request.build())
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .client(get())
            .build()
    }

    single<GitApi> { get<Retrofit>().create(GitApi::class.java) }

    single<GitRepository> {
        GitRepositoryImpl(
            gitApi = get(),
            subscribeScheduler = get(named(SUBSCRIBE_SCHEDULER))
        )
    }

}