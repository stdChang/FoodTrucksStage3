package com.ecs198f.foodtrucks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val recyclerViewAdapterMenu = FoodItemListRecyclerViewAdapter(listOf())
val recyclerViewAdapterReview = FoodTruckReviewRecyclerAdapter(listOf())

class FoodTruckDetailFragment : Fragment() {
    private val args: FoodTruckDetailFragmentArgs by navArgs()
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFoodTruckDetailBinding.inflate(inflater, container, false)

        args.foodTruck.let {
            binding.apply {
                Glide.with(root).load(it.imageUrl).into(foodTruckDetailImage)
                foodTruckDetailPriceLevel.text = "$".repeat(it.priceLevel)
                foodTruckDetailLocation.text = it.location
                foodTruckDetailTime.text = it.formattedTimeInterval
            }

            (requireActivity() as MainActivity).apply {
                var itemDao = db.itemDao()
                title = it.name
                lifecycleScope.launch {
                    var foodItems = itemDao.listItems()
                    var apiresponse: List<FoodItem> = listOf()

                    foodTruckService.listFoodItems(it.id).enqueue(object : Callback<List<FoodItem>> {
                        override fun onResponse(call: Call<List<FoodItem>>, response: Response<List<FoodItem>>) {
                            apiresponse = response.body()!!
                            recyclerViewAdapterMenu.updateItems(apiresponse)
                        }

                        override fun onFailure(call: Call<List<FoodItem>>, t: Throwable) {
                            recyclerViewAdapterMenu.updateItems(foodItems)
                            throw t
                        }
                    })

                    if (foodItems.isEmpty()) {
                        itemDao.addItems(apiresponse)
                    }
                }


                foodTruckService.listFoodReviews(it.id).enqueue(object : Callback<List<FoodReview>> {
                    override fun onResponse(
                        call: Call<List<FoodReview>>,
                        response: Response<List<FoodReview>>
                    ) {
                        Log.i("GELP2", response.body()!!.toString())
                        recyclerViewAdapterReview.updateItems(response.body()!!)
                    }

                    override fun onFailure(call: Call<List<FoodReview>>, t: Throwable) {
                        throw t
                    }
                })
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titles = arrayOf("Menu", "Reviews")

        pagerAdapter = PagerAdapter(this)
        viewPager = view.findViewById(R.id.detailPager)
        viewPager.adapter = pagerAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}


class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = when (position) {
            0 -> FoodTruckMenuFragment(recyclerViewAdapterMenu)
            else -> FoodTruckReviewsFragment(recyclerViewAdapterReview)
        }
        return fragment
    }
}