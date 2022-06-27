package br.com.stock_market_app.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getCompanyListing(
        @Query("apikey") apikey: String = API_KEY
    ): ResponseBody

    companion object {
        const val API_KEY = "XPCJCXUJ2P37OLBM"
        const val BASE_URL = "https://aphavantage.co"
    }
}