package com.example.tappmission.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WidgetResponse(
    @SerialName("data")
    val data: List<WidgetData>? = null,
    @SerialName("meta")
    val meta: MetaData? = null
)

@Serializable
data class WidgetData(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("network")
    val network: NetworkConfig? = null,
    @SerialName("wheel")
    val wheel: WheelConfig? = null
)

@Serializable
data class NetworkConfig(
    @SerialName("attributes")
    val attributes: NetworkAttributes? = null,
    @SerialName("assets")
    val assets: NetworkAssets? = null
)

@Serializable
data class NetworkAttributes(
    @SerialName("refreshInterval")
    val refreshInterval: Int? = null,
    @SerialName("networkTimeout")
    val networkTimeout: Long? = null,
    @SerialName("retryAttempts")
    val retryAttempts: Int? = null,
    @SerialName("cacheExpiration")
    val cacheExpiration: Long? = null,
    @SerialName("debugMode")
    val debugMode: Boolean? = null
)

@Serializable
data class NetworkAssets(
    @SerialName("host")
    val host: String? = null
)

@Serializable
data class WheelConfig(
    @SerialName("rotation")
    val rotation: RotationConfig? = null,
    @SerialName("assets")
    val wheelAssets: WheelAssets? = null
)

@Serializable
data class RotationConfig(
    @SerialName("duration")
    val duration: Int? = null,
    @SerialName("minimumSpins")
    val minimumSpins: Int? = null,
    @SerialName("maximumSpins")
    val maximumSpins: Int? = null,
    @SerialName("spinEasing")
    val spinEasing: String? = null
)

@Serializable
data class WheelAssets(
    @SerialName("bg")
    val background: String? = null,
    @SerialName("wheelFrame")
    val wheelFrame: String? = null,
    @SerialName("wheelSpin")
    val wheelSpin: String? = null,
    @SerialName("wheel")
    val wheelImage: String? = null
)

@Serializable
data class MetaData(
    @SerialName("version")
    val version: Int? = null,
    @SerialName("copyright")
    val copyright: String? = null
)