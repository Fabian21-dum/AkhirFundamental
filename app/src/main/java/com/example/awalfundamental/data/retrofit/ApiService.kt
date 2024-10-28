package com.example.awalfundamental.data.retrofit

import com.example.awalfundamental.data.response.DetailEventResponse
import com.example.awalfundamental.data.response.EventResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

    interface ApiService {
        @GET("/events")
        suspend fun getEvents(@Query("active") active: Int,
                              @Query("limit") limit: Int): Response<EventResponse>
        @GET("/events")
        suspend fun getEvent(@Query("apiKey") apiKey: String): Response<EventResponse>
        @GET("/events/{id}")
        suspend fun getDetail(@Path("id") id: String): Response<DetailEventResponse>

        @POST("events/{eventId}/reviews")
        suspend fun postReview(
            @Path("eventId") eventId: String,
            @Query("name") name: String,
            @Query("review") review: String
        ): Response<EventResponse>



    }
