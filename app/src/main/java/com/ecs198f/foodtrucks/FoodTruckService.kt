package com.ecs198f.foodtrucks

import retrofit2.Call
import retrofit2.http.*

interface FoodTruckService {
    @GET("food-trucks")
    fun listFoodTrucks(): Call<List<FoodTruck>>

    @GET("food-trucks/{id}/items")
    fun listFoodItems(@Path("id") truckId: String): Call<List<FoodItem>>

    @GET("food-trucks/{truckId}/reviews")
    fun listFoodReviews(@Path("truckId") truckId: String): Call<List<FoodReview>>

    @POST("/food-trucks/{truckId}/reviews")
    fun postFoodReview(
        @Header("Authorization") auth: String,
        @Path("truckId") truckId: String,
        @Body review: ReviewType) : Call<Unit>
}