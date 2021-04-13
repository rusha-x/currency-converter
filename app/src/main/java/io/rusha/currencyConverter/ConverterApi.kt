package io.rusha.currencyConverter

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface ConverterApi {
    @GET("latest")
    fun getConversions(): Single<Conversions>
}