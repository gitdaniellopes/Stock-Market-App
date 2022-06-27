package br.com.stock_market_app.data.repository

import br.com.stock_market_app.data.csv.CSVParser
import br.com.stock_market_app.data.local.StockDatabase
import br.com.stock_market_app.data.mapper.toCompanyListing
import br.com.stock_market_app.data.mapper.toCompanyListingEntity
import br.com.stock_market_app.data.remote.StockApi
import br.com.stock_market_app.domain.model.CompanyListing
import br.com.stock_market_app.domain.repository.StokeRepository
import br.com.stock_market_app.util.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StokeRepositoryImpl @Inject constructor(
    val api: StockApi,
    val database: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListing>
) : StokeRepository {

    private val dao = database.stockDao

    override suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<ResourceState<List<CompanyListing>>> = flow {
        emit(ResourceState.Loading(isLoading = true))
        val localListings = dao.searchCompanyListing(query)
        emit(ResourceState.Success(
            data = localListings.map { it.toCompanyListing() }
        ))

        val isSbEmpty = localListings.isEmpty() && query.isBlank()
        val shouldJustLoadFromCache = !isSbEmpty && !fetchFromRemote

        //Carregando apenas o que esta no banco de dados em cache
        if (shouldJustLoadFromCache) {
            emit(ResourceState.Loading(false))
            return@flow
        }

        val remoteListings = try {
            val response = api.getCompanyListing()
            //para ler arquivos CSV
            companyListingsParser.parse(response.byteStream())

        } catch (e: IOException) {
            e.printStackTrace()
            emit(ResourceState.Error("Não foi possível carregar os dados"))
            null
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(ResourceState.Error("Não foi possível carregar os dados"))
            null
        }

        remoteListings?.let { listings ->
            dao.clearCompanyListings()
            dao.insertCompanyListings(
                listings.map { it.toCompanyListingEntity() }
            )
            emit(ResourceState.Success(
                data = dao.searchCompanyListing("").map {
                    it.toCompanyListing()
                }
            ))
            emit(ResourceState.Loading(isLoading = false))

        }
    }
}