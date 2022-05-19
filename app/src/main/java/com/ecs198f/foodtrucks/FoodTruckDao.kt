package com.ecs198f.foodtrucks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodTruckDao {
    @Query("SELECT * FROM foodtruck")
    suspend fun listTrucks(): List<FoodTruck>

    @Query("SELECT * FROM foodtruck WHERE id=:id")
    suspend fun listTrucksOfType(id: String): List<FoodTruck>

    @Insert
    suspend fun addTruck(truck: FoodTruck)

    @Insert
    suspend fun addTrucks(trucks: List<FoodTruck>)

    @Delete
    suspend fun removeTruck(truck: FoodTruck)

    @Query("DELETE FROM foodtruck WHERE id=:id")
    suspend fun removeTrucksOfType(id: String)
}