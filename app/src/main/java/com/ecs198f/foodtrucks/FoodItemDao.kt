package com.ecs198f.foodtrucks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM fooditem")
    suspend fun listItems(): List<FoodItem>

    @Query("SELECT * FROM fooditem WHERE truckId=:truckId")
    suspend fun listItemsOfType(truckId: String): List<FoodItem>

    @Insert
    suspend fun addItem(item: FoodItem)

    @Insert
    suspend fun addItems(items: List<FoodItem>)

    @Delete
    suspend fun removeItem(item: FoodItem)

    @Query("DELETE FROM fooditem WHERE truckId=:truckId")
    suspend fun removeItemsOfType(truckId: String)
}