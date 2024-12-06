package com.popivyurii.ceapi

import com.popivyurii.ceapi.model.ExchangeRates
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface CurrencyExchangeApi {

    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getExchangeRates(): Result<ExchangeRates>
}

fun CurrencyExchangeApi(
    baseUrl: String
): CurrencyExchangeApi {
    return retrofit(baseUrl, json = Json).create()
}

private fun retrofit(
    baseUrl: String,
    okHttpClient: OkHttpClient? = null,
    json: Json
): Retrofit {
    val jsonConverterFactory = json.asConverterFactory("application/json".toMediaType())

    val modifiedOkHttpClient =
        (okHttpClient?.newBuilder() ?: OkHttpClient.Builder())
            .addInterceptor(HttpLoggingInterceptor().setLevel(BODY)).build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(jsonConverterFactory)
        .addCallAdapterFactory(ResultCallAdapterFactory.create())
        .client(modifiedOkHttpClient)
        .build()
}