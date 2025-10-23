package com.example.Data.Remote

import retrofit2.http.*

interface BranchApi {
    @POST("api/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/messages")
    suspend fun getMessages(): List<MessageDto>

    @POST("api/messages")
    suspend fun sendMessage(@Body body: SendMessageBody): MessageDto

    @POST("api/reset")
    suspend fun reset(): Unit
}

data class SendMessageBody(
    val thread_id: Long,
    val body: String
)