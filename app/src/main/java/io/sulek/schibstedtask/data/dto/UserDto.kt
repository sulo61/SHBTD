package io.sulek.schibstedtask.data.dto

import com.google.gson.annotations.SerializedName
import io.sulek.schibstedtask.models.Repo
import io.sulek.schibstedtask.models.User

class UserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)

fun UserDto.toDomainModel(repos: List<RepoDto>) = User(
    id = id,
    name = login,
    avatarUrl = avatarUrl,
    repos = repos.map { it.toDomainModel() }.take(3)
)