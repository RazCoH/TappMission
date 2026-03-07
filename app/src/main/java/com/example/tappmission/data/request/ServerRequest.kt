package com.example.tappmission.data.request

data class ServerRequest(
    val url: String,
    val method: Method = Method.GET,
    val headers: Map<String, String> = emptyMap(),
    val params: Map<String, String> = emptyMap(),
    val body: String? = null
) {
    enum class Method { GET, POST }
}
