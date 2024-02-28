package com.darkn0va.timber.composable

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewCozy
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.`fun`.capitalized
import com.darkn0va.timber.ui.theme.GreyBG
import com.darkn0va.timber.ui.theme.Purple20
import com.darkn0va.timber.ui.theme.Purple40
import com.darkn0va.timber.ui.theme.Purple80

@Composable
fun UserPage(
    navController: NavController,
    user: User,
) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Settings,
            "settings",
            modifier = Modifier.align(Alignment.End).size(24.dp).clickable(onClick = {
                Log.d("LOG", "SETTINGS")
            })
        )
        Icon(imageVector = Icons.Filled.Person, "person", Modifier.padding(top = 64.dp).size(64.dp))
        Text(text = "${user.firstName.capitalized()} ${user.lastName.capitalized()}", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Personal information
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Personal information", fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            Row(
                Modifier.clickable(onClick = {
                    Log.d("LOG", "EDIT")
                })
            ) {
                Icon(Icons.Outlined.BorderColor, "edit", Modifier.size(24.dp), tint = Purple40)
                Text("Edit", color = Purple40, fontSize = 18.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier
        ) {
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Mail, "email", Modifier.size(32.dp))
                Text(" Email")
                Spacer(Modifier.weight(1f))
                Text(text = user.email)
            }
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Smartphone, "phone", Modifier.size(32.dp))
                Text(" Phone")
                Spacer(Modifier.weight(1f))
                Text(text = "${user.countryPhoneCode} ${user.phoneNumber}")
            }
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Celebration, "cake", Modifier.size(32.dp))
                Text(" Date of birth")
                Spacer(Modifier.weight(1f))
                if (user.dateOfBirth != null) {
                    Text("${user.dateOfBirth.dayOfMonth}.${user.dateOfBirth.monthNumber}.${user.dateOfBirth.year}")
                } else {
                    Text("N/A")
                }
            }
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.LocationOn, "location", Modifier.size(32.dp))
                Text(" Location")
                Spacer(Modifier.weight(1f))
                Text(text = user.citizenShip.uppercase())
            }
        }


        Spacer(Modifier.height(24.dp))

        // Utilities
        Text("Utilities", Modifier.align(Alignment.Start), fontSize = 18.sp)
        Column(
            Modifier
        ) {
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp).clickable(onClick = {
                        navController.navigate("userLocations")
                    }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.ViewCozy, "My locations", Modifier.size(32.dp))
                Text(" My locations", color = Purple20)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.ChevronRight, "chevron right", tint = Purple40)
            }
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp).clickable(onClick = {
                        navController.navigate("createLocation")
                    }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.BorderColor, "create location", Modifier.size(32.dp))
                Text(" Create location", color = Purple20)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.ChevronRight, "chevron right", tint = Purple40)
            }
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp).clickable(onClick = {
                        Log.d("LOG", "LOGOUT")
//                    logout(navController)
                    }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, "logout", Modifier.size(32.dp))
                Text(" Log out", color = Color.Red)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.ChevronRight, "chevron right", tint = Purple40)
            }
            Row(
                modifier = Modifier.padding(2.dp).fillMaxWidth().background(GreyBG).clip(RoundedCornerShape(16.dp))
                    .padding(4.dp).clickable(onClick = {
                        Log.d("LOG", "DELETE ACCOUNT")
                    }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Delete, "delete account", Modifier.size(32.dp))
                Text(" Delete Account", color = Color.Red)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.ChevronRight, "chevron right", tint = Color.Red)
            }
        }
    }
}

fun logout(navController: NavController) {
    navController.navigate("login")
}