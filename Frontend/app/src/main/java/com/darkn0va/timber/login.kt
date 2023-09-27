package com.darkn0va.timber

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv {
    directory = "src/main/assets"
    filename = "env"
}

@Composable
fun Login(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (dotenv["DEBUG"] == "true") {
            Text(
                text = "DEBUG MODE"
            )
            Button(
                onClick = {
                    navController.navigate("home")
                }
            ) {
                Text(
                    text = "Login with debug"
                )
            }
        }
        Button(
            onClick = {
                navController.navigate("home")
            }
        ) {
            Text(
                text = "Login"
            )
        }
    }
}