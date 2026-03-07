package com.example.tappmission.data.repositories

import com.example.tappmission.data.Networking
import com.example.tappmission.data.models.NetworkResult
import com.example.tappmission.data.models.ServerRequest
import com.example.tappmission.data.responses.WidgetResponse
import com.example.tappmission.utils.URLS


class WidgetsRepository(private val networking: Networking) {

    suspend fun getWheelWidgetData(): NetworkResult<WidgetResponse> =
        networking.sendRequest(
            request = ServerRequest(url = URLS.WHEEL_WIDGET_CONFIG),
            serializer = WidgetResponse.serializer()
        )
}
