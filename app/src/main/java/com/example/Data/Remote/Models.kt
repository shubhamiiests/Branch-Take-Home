package com.example.Data.Remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("auth_token") val authToken: String
)

@Serializable
data class MessageDto(
    val id: Long,
    @SerialName("thread_id") val threadId: Long,
    @SerialName("user_id") val userId: Long?,
    @SerialName("agent_id") val agentId: Long?,
    val body: String,
    val timestamp: String
)