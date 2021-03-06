package io.ktor.http

import kotlinx.io.charsets.*

fun String.parseUrlEncodedParameters(defaultEncoding: Charset = Charsets.UTF_8, limit: Int = 1000): Parameters {
    val parameters: List<Pair<String, String>> =
        split("&", limit = limit).map { it.substringBefore("=") to it.substringAfter("=", "") }
    val encoding: String =
        parameters.firstOrNull { it.first == "_charset_" }?.second ?: defaultEncoding.name

    val charset = Charset.forName(encoding)
    return Parameters.build {
        parameters.forEach { (key, value) ->
            append(
                key.decodeURLQueryComponent(charset = charset),
                value.decodeURLQueryComponent(charset = charset)
            )
        }
    }
}

fun List<Pair<String, String?>>.formUrlEncode(): String = StringBuilder().apply {
    formUrlEncodeTo(this)
}.toString()

fun List<Pair<String, String?>>.formUrlEncodeTo(out: Appendable) {
    filter { it.second != null }.joinTo(
        out, "&"
    ) {
        val key = it.first.encodeURLParameter(spaceToPlus = true)
        val value = it.second.toString().encodeURLParameter(spaceToPlus = true)

        "$key=$value"
    }
}

fun Parameters.formUrlEncode(): String = entries()
    .flatMap { e -> e.value.map { e.key to it } }
    .formUrlEncode()

fun Parameters.formUrlEncodeTo(out: Appendable) {
    entries()
        .flatMap { e -> e.value.map { e.key to it } }
        .formUrlEncodeTo(out)
}
