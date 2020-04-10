package io.sulek.schibstedtask.data

import io.reactivex.Single
import io.sulek.schibstedtask.data.dto.RepoDto
import io.sulek.schibstedtask.data.dto.RepoSearchResultDto
import io.sulek.schibstedtask.data.dto.UserSearchResultDto
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface GitApi {

    @GET("search/users")
    fun findUsers(@Query("q") query: String): Single<UserSearchResultDto>

    @GET("search/repositories")
    fun findRepositories(@Query("q") query: String): Single<RepoSearchResultDto>

    @GET("users/{userLogin}/repos")
    fun getUserRepos(@Path("userLogin") userLogin: String): Single<List<RepoDto>>

}