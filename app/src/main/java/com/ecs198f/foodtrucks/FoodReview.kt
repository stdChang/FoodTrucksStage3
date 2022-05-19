package com.ecs198f.foodtrucks

data class FoodReview(
    var id: String,
    var truckId: String,
    var authorName: String,
    var authorAvatarUrl: String,
    var content: String,
    var imageUrls: Array<String>
)
