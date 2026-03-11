package com.example.tappmission.data.remote

import com.example.tappmission.data.models.DataResult
import com.example.tappmission.data.remote.ServerRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class Networking(
    private val client: OkHttpClient,
    private val json: Json
) {

    suspend fun <T> sendRequest(
        request: ServerRequest,
        serializer: KSerializer<T>
    ): DataResult<T> = withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(buildRequest(request)).execute()
            if (response.isSuccessful) {
                val body = response.body.string()
                DataResult.Success(json.decodeFromString(serializer, body))
            } else {
                DataResult.Error(response.code, response.message)
            }
        } catch (e: IOException) {
            DataResult.Exception(e)
        } catch (e: SerializationException) {
            DataResult.Exception(e)
        }
    }

    private fun buildRequest(request: ServerRequest): Request {
        val url = request.url.toHttpUrl().newBuilder().apply {
            request.params.forEach { (key, value) -> addQueryParameter(key, value) }
        }.build()

        val builder = Request.Builder().url(url).apply {
            request.headers.forEach { (key, value) -> addHeader(key, value) }
        }

        when (request.method) {
            ServerRequest.Method.GET -> builder.get()
            ServerRequest.Method.POST -> {
                val body = (request.body ?: "").toRequestBody(JSON_MEDIA_TYPE)
                builder.post(body)
            }
        }

        return builder.build()
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}