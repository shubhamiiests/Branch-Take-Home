package com.example.Data

import com.example.Data.Local.TokenStore
import com.example.Data.Remote.BranchApi
import com.example.Data.Remote.LoginRequest
import com.example.Data.Remote.MessageDto
import com.example.Data.Remote.SendMessageBody
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BranchRepository @Inject constructor(
    private val api: BranchApi,
    private val tokenStore: TokenStore
) {
    val authToken: Flow<String?> = tokenStore.token

    suspend fun login(email: String, password: String) {
        val res = api.login(LoginRequest(email, password))
        tokenStore.set(res.authToken)
    }

    suspend fun logout() = tokenStore.set(null)

    suspend fun getMessages(): List<MessageDto> = api.getMessages()

    suspend fun sendMessage(threadId: Long, body: String): MessageDto =
        api.sendMessage(SendMessageBody(thread_id = threadId, body = body))

    suspend fun reset() = api.reset()
}