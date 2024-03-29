package br.com.stock_market_app.util

sealed class ResourceState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : ResourceState<T>(data)
    class Error<T>(message: String, data: T? = null) : ResourceState<T>(data, message)
    class Loading<T>(val isLoading: Boolean) : ResourceState<T>(null)
}
