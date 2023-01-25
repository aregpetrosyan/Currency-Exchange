package com.aregyan.compose.repository

import com.aregyan.compose.database.AppDatabase
import com.aregyan.compose.database.asDomainModel
import com.aregyan.compose.domain.User
import com.aregyan.compose.network.ExchangeRatesApi
import com.aregyan.compose.network.UsersApi
import com.aregyan.compose.network.model.asDatabaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(
    private val exchangeRatesApi: ExchangeRatesApi,
    private val appDatabase: AppDatabase
) {

    val users: Flow<List<User>?> =
        appDatabase.usersDao.getUsers().map { it?.asDomainModel() }

    suspend fun fetchExchangeRates() {
        try {
            val exchangeRates = exchangeRatesApi.getExchangeRates()
            println("YOYO $exchangeRates")
//            appDatabase.usersDao.insertUsers(users.asDatabaseModel())
        } catch (e: Exception) {
            Timber.w(e)
        }
    }
}