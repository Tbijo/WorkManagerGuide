package com.example.workmanagerguide

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileApi {

    @GET("/wp-content/uploads/2022/02/220849-scaled.jpg")
    suspend fun downloadImage(): Response<ResponseBody>

    // specifying response body so that Retrofit will not try to convert it
    // @Streaming to not save a large file in memory
    @GET
    @Streaming
    suspend fun downloadVideo(@Url fileURL: String): Response<ResponseBody>

//    companion object {
//        val instance by lazy {
//            Retrofit.Builder()
//                .baseUrl("https://pl-coding.com")
//                .build()
//                .create(FileApi::class.java)
//        }
//    }
}