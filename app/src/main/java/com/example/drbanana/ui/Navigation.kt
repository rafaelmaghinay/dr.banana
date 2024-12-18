package com.example.drbanana.ui

import ResultScreen
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.drbanana.FeedbackScreen
import com.example.drbanana.R
import com.example.drbanana.ui.theme.DrBananaTheme

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            if (currentDestination != "result/{imageUri}/{classificationResult}" && currentDestination != "recommendations/{diseaseId}" && currentDestination != "feedback" && currentDestination != "help") {
                TopAppBar(
                    title = {
                        Text("Dr.Banana",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,)
                    },
                    navigationIcon = {
                        Image(painter = painterResource(id = R.drawable.bgremovedlogo), // Replace with your image resource
                            contentDescription = "App Logo",
                            modifier = Modifier.padding(8.dp)
                        )
                    },

                    actions = {
                        Image(
                            painter = painterResource(id = R.drawable.help), // Replace with your photo resource
                            contentDescription = "help",
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    navController.navigate("help")
                                }
                        )
                    },
                    backgroundColor = Color(0xFFD0FFCF),
                )
            }
        },
        bottomBar = {
            if (currentDestination != "result/{imageUri}/{classificationResult}" && currentDestination != "recommendations/{diseaseId}" && currentDestination != "feedback" && currentDestination != "help") {
                BottomNavigationBar(navController)
            }
        }

    ) { innerPadding ->
        // Content of the Home Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            NavigationHost(navController)
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("History") {
            HistoryScreen(navController)
        }

        composable(
            "result/{imageUri}/{classificationResult}",
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("classificationResult") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")?.let { Uri.parse(it) }
            val classificationResult = backStackEntry.arguments?.getString("classificationResult")
            val floatArray = classificationResult?.split(",")?.map { it.toFloat() }?.toFloatArray()
            ResultScreen(predictionResult = floatArray, imageUri = imageUri, navController)
        }

        composable("recommendations/{diseaseId}") { backStackEntry ->
            val diseaseId = backStackEntry.arguments?.getString("diseaseId")
            RecommendationsScreen(diseaseId = diseaseId, navController)
        }
        composable("feedback") {
            FeedbackScreen(navController)
        }

        composable("help") {
            HelpScreen(navController)
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", "home", painterResource(id = R.drawable.home)),
        BottomNavItem("History", "History", painterResource(id = R.drawable.history))
    )

    // Observe the current destination of the NavController
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    // Debugging log to check the current destination
    Log.d("Navigation", "Current destination: $currentDestination")

    BottomNavigation(
        backgroundColor = Color.White
    ) {
        items.forEach { item ->
            val isSelected = currentDestination == item.route // Check if the item is selected

            // Debugging log to check route matching
            Log.d("Navigation", "Item route: ${item.route}, Is selected: $isSelected")

            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    if (currentDestination != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .height(15.dp)
                            .width(30.dp)
                            .background(
                                color = if (isSelected) Color(0xFF90EE90) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp) // Rounded corners
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = item.icon,
                            contentDescription = item.name,
                            tint = if (isSelected) Color.White else Color.Black
                        )
                    }
                },
                label = {
                    Text(
                        text = item.name,
                        color = if (isSelected) Color.Black else Color.Gray
                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray
            )
        }
    }

    // Use LaunchedEffect to monitor route changes
    LaunchedEffect(currentDestination) {
        Log.d("Navigation", "LaunchedEffect triggered for current destination: $currentDestination")
    }
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: Painter
)
