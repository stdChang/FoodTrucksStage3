package com.ecs198f.foodtrucks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckMenuBinding
import com.ecs198f.foodtrucks.databinding.FragmentFoodTruckReviewsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodTruckReviewsFragment(val recyclerViewAdapter: FoodTruckReviewRecyclerAdapter) : Fragment() {
    var RC_SIGN_IN = 0;
    lateinit var binding : FragmentFoodTruckReviewsBinding;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodTruckReviewsBinding.inflate(inflater, container, false)
        binding.reviewRecycler.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("699423245763-insnbbp034ep600msiqfan5g0b2pau67.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.signInButton.setOnClickListener{
            val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this.requireContext()) // <-- this black magic could work
        updateUI(false);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(true)

            binding.postButton.setOnClickListener{
                val text = binding.postEditText.text.toString()
                val data = ReviewType(text, emptyArray())
                (requireActivity() as MainActivity).apply {
                    foodTruckService.postFoodReview( "Bearer ${account!!.idToken}", recyclerViewAdapter.getTruckID(), data)
                        .enqueue(object : Callback<Unit> {
                            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                Log.i("post success?: ", "${response.isSuccessful}")
                                Log.i("test","${account!!.idToken}" )
                                binding.postEditText.text!!.clear()
                                binding.postEditText.clearFocus()
                                //TODO update recycler view
                            }

                            override fun onFailure(call: Call<Unit>, t: Throwable) {
                                Log.i("test","Failed to post")
                            }
                        })
                    Log.i("Post", "we sent this: " + text + "truck id was: " + recyclerViewAdapter.getTruckID())
                }
            }
        } catch (e: ApiException) {
            Log.w("signingin", "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }
    }

    private fun updateUI(signedIn : Boolean) {
        if (signedIn) {
            binding.signInButton.visibility = View.GONE
            binding.postTextView.visibility = View.VISIBLE
            binding.postEditText.visibility = View.VISIBLE
            binding.postButton.visibility = View.VISIBLE
        } else {
            binding.signInButton.visibility = View.VISIBLE
            binding.postTextView.visibility = View.GONE
            binding.postEditText.visibility = View.GONE
            binding.postButton.visibility = View.GONE
        }
    }
}

