package br.com.stock_market_app.domain.repository

import br.com.stock_market_app.domain.model.CompanyListing
import br.com.stock_market_app.util.ResourceState
import kotlinx.coroutines.flow.Flow

interface StokeRepository {

    suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<ResourceState<List<CompanyListing>>>
}