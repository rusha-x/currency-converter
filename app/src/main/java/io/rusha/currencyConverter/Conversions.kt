package io.rusha.currencyConverter

import com.google.gson.annotations.SerializedName

class Conversions(
    @SerializedName("base_code")
    val baseCode: String,
    @SerializedName("rates")
    val rates: Map<String, Double>
) {
    fun convert(input: Double, fromCurrencyText: String, toCurrencyText: String ): Double {
        val from = rates[fromCurrencyText] ?: 1.0
        val to = rates[toCurrencyText] ?: 1.0
        return input / from * to
    }
}