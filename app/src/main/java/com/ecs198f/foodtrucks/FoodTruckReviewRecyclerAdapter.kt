package com.ecs198f.foodtrucks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecs198f.foodtrucks.databinding.FoodReviewItemBinding

class FoodTruckReviewRecyclerAdapter(private var items: List<FoodReview>):
    RecyclerView.Adapter<FoodTruckReviewRecyclerAdapter.ViewHolder>() {

    class ViewHolder(val binding: FoodReviewItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FoodReviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let {
            holder.binding.apply {
                reviewComment.text = it.content
                reviewName.text = it.authorName
                Glide.with(root).load(it.authorAvatarUrl).into(reviewAvatar)
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(items: List<FoodReview>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun getTruckID(): String {
        return items[0].truckId
    }
}