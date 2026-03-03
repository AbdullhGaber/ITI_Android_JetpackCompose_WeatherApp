package com.iti.weatherapp.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiState<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiState.Success(body)
                } else {
                    ApiState.Error("Response body is null")
                }
            } else {
                ApiState.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: IOException) {
            ApiState.Error("Network error. Please check your internet connection.")
        } catch (e: Exception) {
            ApiState.Error("An unexpected error occurred: ${e.message}")
        }
    }
}