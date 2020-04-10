package io.sulek.schibstedtask.data.dto

import com.google.gson.annotations.SerializedName

class UserSearchResultDto(
    @SerializedName("items") val items: List<UserDto>
)