package com.zybooks.basket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.zybooks.basket.data.AppDatabase
import com.zybooks.basket.data.BasketRepository
import com.zybooks.basket.navigation.NavGraph
import com.zybooks.basket.ui.theme.BASKETTheme
import com.zybooks.basket.viewmodel.AuthViewModel
import com.zybooks.basket.viewmodel.BasketViewModel
import com.zybooks.basket.viewmodel.CommunityViewModel
import com.zybooks.basket.viewmodel.CommunityViewModelFactory

class MainActivity : ComponentActivity() {

    private val basketViewModel: BasketViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val communityViewModel: CommunityViewModel by viewModels {
        val basketDao = AppDatabase.getDatabase(this).basketDao()
        val basketRepository = BasketRepository(basketDao)
        CommunityViewModelFactory(basketRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            BASKETTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        basketViewModel = basketViewModel,
                        authViewModel = authViewModel,
                        communityViewModel = communityViewModel
                    )
                }
            }
        }
    }
}