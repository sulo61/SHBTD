package io.sulek.schibstedtask.data.dto

import com.google.gson.annotations.SerializedName

class RepoSearchResultDto(
    @SerializedName("items") val items: List<RepoOwnerDto>
)

class RepoOwnerDto(
    @SerializedName("owner") val owner: UserDto
)