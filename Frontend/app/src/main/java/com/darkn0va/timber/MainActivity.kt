package com.darkn0va.timber

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.*
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.api.ktorHttpClient
import com.darkn0va.timber.api.testAPI
import com.darkn0va.timber.ui.theme.NavigationItems
import com.darkn0va.timber.ui.theme.TimberTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimberTheme {
                val navController = rememberNavController()
                var currentUser by remember { mutableStateOf(User()) }
                var selectedItemIndex by rememberSaveable {
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
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            if (bottomBarState) {
                                NavigationBar {
                                    NavigationItems.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = {
                                                selectedItemIndex = index
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
                                                        imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
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

                    }
                }
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        Login(navController = navController, onLoggedUser = { result ->
                            currentUser = result
                            Log.d("USER", currentUser.toString())
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
                        navigation("userLocations", route = "locations") {
                            composable("userLocations") {

                            }
                            composable("bookedLocations") {

                            }
                        }
                        navigation("userInfo", route = "user") {
                            composable("userInfo") {

                            }
                            composable("edit") {

                            }
                            composable("settings") {

                            }
                        }
                    }
                }
            }
        }
    }
}
