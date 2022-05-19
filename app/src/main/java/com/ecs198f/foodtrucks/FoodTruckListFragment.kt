package com.ecs198f.foodtrucks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckListBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodTruckListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFoodTruckListBinding.inflate(inflater, container, false)
        val recyclerViewAdapter = FoodTruckListRecyclerViewAdapter(listOf())

        binding.foodTruckListRecyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }

        (requireActivity() as MainActivity).apply {
            val truckDao = db.truckDao()
            title = "Food Trucks"
            lifecycleScope.launch {
                var foodTrucks = truckDao.listTrucks()
                var apiresponse: List<FoodTruck> = listOf()

                foodTruckService.listFoodTrucks().enqueue(object : Callback<List<FoodTruck>> {
                    override fun onResponse(call: Call<List<FoodTruck>>, response: Response<List<FoodTruck>>) {
                        apiresponse = response.body()!!
                        recyclerViewAdapter.updateItems(apiresponse)
                    }

                    override fun onFailure(call: Call<List<FoodTruck>>, t: Throwable) {
                        recyclerViewAdapter.updateItems(foodTrucks)
                        throw t
                    }
                })

                if (foodTrucks.isEmpty()) {
                    truckDao.addTrucks(apiresponse)
                }
            }
        }

        return binding.root
    }
}
