package io.sulek.schibstedtask.models

data class User(
    val id: Long,
    val name: String,
    val avatarUrl: String?,
    val repos: List<Repo>
)