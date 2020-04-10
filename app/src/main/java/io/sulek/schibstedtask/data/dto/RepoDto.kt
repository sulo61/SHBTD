package io.sulek.schibstedtask.data.dto

import com.google.gson.annotations.SerializedName
import io.sulek.schibstedtask.models.Repo

class RepoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("owner") val owner: UserDto
)

fun RepoDto.toDomainModel() = Repo(
    id = id,
    name = name
)