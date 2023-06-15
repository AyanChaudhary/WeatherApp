package com.example.weatherapp2.Datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class weatherDetails(val context:Context) {

    val Context.dataStore : DataStore<Preferences> by preferencesDataStore("pref")

    suspend fun writeToDataStore(key:String,value:String){
        val dataStoreKey= stringPreferencesKey(key)
        context.dataStore.edit {
            it[dataStoreKey]=value
        }
    }

    suspend fun readFromDataStore(key:String) : String?{
        val dataStoreKey= stringPreferencesKey(key)
        val preference=context.dataStore.data.first()
        return preference[dataStoreKey]
    }
}