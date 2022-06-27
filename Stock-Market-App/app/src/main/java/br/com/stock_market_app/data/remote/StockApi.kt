package br.com.stock_market_app.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getCompanies(
        @Query("apikey") apikey: String
    ): ResponseBody

    companion object {
        const val API_KEY = "XPCJCXUJ2P37OLBM"
        const val BASE_URL = "https://aphavantage.co"
    }
}