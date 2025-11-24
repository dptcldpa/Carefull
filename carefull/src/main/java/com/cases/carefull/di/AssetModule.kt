package com.cases.carefull.di

import android.content.Context
import com.cases.carefull.data.di.DepartmentCode
import com.cases.carefull.domain.model.DepartmentCodeItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStreamReader
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AssetModule {

    @Provides
    @Singleton
    @DepartmentCode
    fun provideDepartmentCodeMap(
        @ApplicationContext context: Context,
        gson: Gson
    ): Map<String, String> {
        return try {
            val inputStream = context.assets.open("department_codes.json")
            val reader = InputStreamReader(inputStream)

            val listType = object : TypeToken<List<DepartmentCodeItem>>() {}.type
            val departmentCodeList: List<DepartmentCodeItem> = gson.fromJson(reader, listType)

            departmentCodeList.associate { it.name to it.code }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }
}