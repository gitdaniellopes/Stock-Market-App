package br.com.stock_market_app.data.repository

import br.com.stock_market_app.data.local.StockDatabase
import br.com.stock_market_app.data.mapper.toCompanyListing
import br.com.stock_market_app.data.remote.StockApi
import br.com.stock_market_app.domain.model.CompanyListing
import br.com.stock_market_app.domain.repository.StokeRepository
import br.com.stock_market_app.util.ResourceState
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StokeRepositoryImpl @Inject constructor(
    val api: StockApi,
    val database: StockDatabase
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
            val csvReader = CSVReader(InputStreamReader(response.byteStream()))

        } catch (e: IOException) {
            e.printStackTrace()
            emit(ResourceState.Error("Não foi possível carregar os dados"))
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(ResourceState.Error("Não foi possível carregar os dados"))
        }
    }
}