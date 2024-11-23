package com.popivyurii.ceapi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * {"base":"EUR","date":"2024-10-20","rates":{"AED":4.147043,"AFN":118.466773,"ALL":120.73174,...}}
 */
@Serializable
data class ExchangeRates (
    @SerialName("base") val base: String? = null,
    @SerialName("date") val date: String? = null,
    @SerialName("rates") val rates: Map<String, Double>?= null
)