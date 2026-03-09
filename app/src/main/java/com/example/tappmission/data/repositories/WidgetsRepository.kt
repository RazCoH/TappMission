package com.example.tappmission.data.repositories

import com.example.tappmission.data.Networking
import com.example.tappmission.data.models.NetworkResult
import com.example.tappmission.data.models.ServerRequest
import com.example.tappmission.data.responses.WidgetResponse
import com.example.tappmission.utils.UrlPaths


class WidgetsRepository(private val networking: Networking) {

    suspend fun getWheelWidgetData(): NetworkResult<WidgetResponse> {
        val params = mutableMapOf<String, String>()
        params["id"] = UrlPaths.CONFIG_PATH
        return networking.sendRequest(
            request = ServerRequest(url = UrlPaths.BASE_URL, params = params),
            serializer = WidgetResponse.serializer()
        )
    }

}
