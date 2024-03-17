package com.darkn0va.timber

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.darkn0va.timber.api.data.NavigationBottomItems
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.composable.*
import com.darkn0va.timber.ui.theme.TimberTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimberTheme {
                val navController = rememberNavController()
                var currentUser by remember { mutableStateOf(User()) }
                var selectedBottomItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                var selectedTopItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }

                var bottomBarState by rememberSaveable {
                    mutableStateOf(false)
                }

                val navBackStateEntry by navController.currentBackStackEntryAsState()

                bottomBarState = when (navBackStateEntry?.destination?.route) {
                    "login" -> {
                        false
                    }

                    else -> {
                        true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            if (bottomBarState) {
                                NavigationBar {
                                    NavigationBottomItems.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedBottomItemIndex == index,
                                            onClick = {
                                                selectedBottomItemIndex = index
                                                navController.navigate(item.route)
                                            },
                                            label = {
                                                Text(text = item.title)
                                            },
                                            icon = {
                                                BadgedBox(
                                                    badge = {
                                                        if (item.hasNews) {
                                                            Badge()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (selectedBottomItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                                        contentDescription = item.title
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
                            NavHost(navController = navController, startDestination = "login") {
                                composable("login") {
                                    Login(navController = navController, onLoggedUser = { result ->
                                        currentUser = result
                                    })
                                }
                                navigation("home", route = "app") {
                                    navigation("feed", route = "home") {
                                        composable("feed") {
                                            Feed(navController = navController)
                                        }
                                    }
                                    composable("search") {
                                        Search(navController = navController)
                                    }
                                    composable("location/{locationID}") { backStackEntry ->
                                        backStackEntry.arguments?.getString("locationID")
                                            ?.let { LocationCom(navController, it, currentUser) }
                                    }
                                    composable("createBooking/{locationID}") {backStackEntry ->
                                        backStackEntry.arguments?.getString("locationID")
                                            ?.let { CreateBooking(navController,currentUser, it) }
                                    }
                                    composable("bookings") {
                                        currentUser.userUUID?.let { it1 -> BookedLocations(it1, navController) }
                                    }
                                    navigation("userInfo", route = "user") {
                                        composable("userInfo") {
                                            UserPage(navController = navController, user = currentUser)
                                        }
                                        composable("createLocation") {
                                            CreateLocation(navController, user = currentUser)
                                        }
                                        composable("userLocations") {
                                            currentUser.userUUID?.let { it1 -> UserLocations(it1, navController) }
                                        }
                                        composable("dangerzone") {
                                            DangerZone(navController = navController, user = currentUser)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
