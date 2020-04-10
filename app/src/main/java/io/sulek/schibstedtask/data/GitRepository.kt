package io.sulek.schibstedtask.data

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.sulek.schibstedtask.data.dto.*
import io.sulek.schibstedtask.models.User
import timber.log.Timber

interface GitRepository {

    fun find(query: String): Single<List<User>>

}

class GitRepositoryImpl(
    private val gitApi: GitApi,
    private val subscribeScheduler: Scheduler
) : GitRepository {

    // TODO replace with db for longer persistence
    private val cachedUsers = mutableMapOf<Long, User>()

    override fun find(query: String): Single<List<User>> {
        val usersDto = mutableListOf<UserDto>()

        // search in users and repositories
        return Single.zip(
            gitApi.findUsers(query),
            gitApi.findRepositories(query),
            BiFunction<UserSearchResultDto, RepoSearchResultDto, List<UserDto>> { users, repos ->
                val mergedUsers = mutableListOf<UserDto>()
                mergedUsers.addAll(users.items)
                mergedUsers.addAll(repos.items.map { it.owner })
                mergedUsers
            }
        )
            // collect all unique user names, and fetch their repositories
            .flatMap {
                usersDto.addAll(it.distinctBy { user -> user.login })
                val repos = usersDto.map { user -> gitApi.getUserRepos(user.login) }
                Single.merge(repos).toList()
            }
            // match repositories for users, and parse to domain models
            .flatMap {
                Single.fromCallable {
                    val pairs = mutableMapOf<Long, MutableList<RepoDto>>()
                    it.forEach { repos ->
                        repos.take(3).forEach { repo ->
                            pairs[repo.owner.id]?.add(repo) ?: run {
                                pairs.put(repo.owner.id, mutableListOf(repo))
                            }
                        }
                    }
                    val users = usersDto.map { it.toDomainModel(pairs[it.id] ?: emptyList()) }
                    cachedUsers.putAll(users.map { it.id to it })
                    users
                }
            }
            .doOnError { Timber.e(it, "Error while fetching user repositories") }
            .onErrorResumeNext {
                val users = cachedUsers.values.filter { it.name.toLowerCase().contains(query) }
                Single.just(users)
            }
            .subscribeOn(subscribeScheduler)
    }
}