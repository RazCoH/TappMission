package com.example.tappmission.data.remote

import com.example.tappmission.data.models.DataResult
import com.example.tappmission.data.models.ServerRequest
import com.example.tappmission.data.remote.responses.WidgetResponse
import com.example.tappmission.utils.UrlPaths

/**
 * Responsible exclusively for fetching [WidgetResponse] from the remote API.
 * No caching, no business logic — just the network call.
 */
class RemoteDataSource(private val networking: Networking) {

    suspend fun fetchWheelWidgetData(): DataResult<WidgetResponse> {
        val params = mutableMapOf("id" to UrlPaths.CONFIG_PATH)
        return networking.sendRequest(
            request = ServerRequest(url = UrlPaths.BASE_URL, params = params),
            serializer = WidgetResponse.serializer()
        )
    }
}
