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
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import com.darkn0va.timber.api.data.NavigationBottomItems
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.composable.*
import com.darkn0va.timber.ui.theme.TimberTheme
import io.github.jan.supabase.coil.CoilIntegration
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

class MainActivity : ComponentActivity() {

//    public val client = createSupabaseClient(
//        supabaseUrl = stringResource(R.string.SUPABASE_URL),
//        supabaseKey = stringResource(R.string.SUPABASE_ANON_KEY)
//    ) {
//        //...
//        install(Storage) {
//            //your config
//        }
//        install(CoilIntegration)
//    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
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
//                var topBarSate by rememberSaveable {
//                    mutableStateOf(false)
//                }

                val navBackStateEntry by navController.currentBackStackEntryAsState()

                bottomBarState = when (navBackStateEntry?.destination?.route) {
                    "login" -> {
                        false
                    }

                    else -> {
                        true
                    }
                }
//                topBarSate = when (navBackStateEntry?.destination?.route) {
//                    "userLocations" -> {
//                        true
//                    }
//
//                    "bookedLocations" -> {
//                        true
//                    }
//
//                    else -> {
//                        false
//                    }
//                }

                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
//                        topBar = {
//                            if (topBarSate) {
//                                NavigationBar {
//                                    NavigationTopItems.forEachIndexed { index, item ->
//                                        NavigationBarItem(
//                                            selected = selectedTopItemIndex == index,
//                                            onClick = {
//                                                selectedTopItemIndex = index
//                                                navController.navigate(item.route)
//                                            },
//                                            label = {
//                                                Text(text = item.title)
//                                            },
//                                            icon = { },
//                                            modifier = Modifier.background(Color.Transparent)
//                                        )
//                                    }
//                                }
//                            }
//                        },
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
                        composable("location/{locationID}") { backStackEntry ->
                            backStackEntry.arguments?.getString("locationID")?.let { Location(navController, it) }
                        }
                        composable("bookings") {
                            currentUser.userUUID?.let { it1 -> BookedLocations(it1, navController) }
                        }
                        navigation("userInfo", route = "user") {
                            composable("userInfo") {
                                UserPage(navController = navController, user = currentUser)
                            }
                            composable("createLocation") {
                                CreateLocation(navController)
                            }
                            composable("userLocations") {
                                currentUser.userUUID?.let { it1 -> UserLocations(it1, navController) }
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
