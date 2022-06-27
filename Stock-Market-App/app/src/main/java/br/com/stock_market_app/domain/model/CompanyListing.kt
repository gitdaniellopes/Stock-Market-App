package br.com.stock_market_app.domain.model

data class CompanyListing(
    val name: String,
    val symbol: String,
    val exchange: String
)
