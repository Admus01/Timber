package com.darkn0va.timber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.darkn0va.timber.ui.theme.TimberTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimberTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("login") {
                        Login(navController = navController)
                    }
                    navigation("home", route = "app") {
                        navigation("home", route = "home") {

                        }
                        navigation("search", route = "search") {
                            composable("search") {

                            }
                        }
                        navigation("locations", route = "locations") {
                            composable("userLocations") {

                            }
                            composable("bookedLocations") {

                            }
                        }
                        navigation("userInfo", route = "user") {
                            composable("info") {

                            }
                            composable("edit") {

                            }
                        }
                    }
                }

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TimberTheme {
        Greeting("Android")
    }
}