package com.darkn0va.timber.composable
//
//import android.graphics.Bitmap
//import android.util.Log
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.darkn0va.timber.api.data.Location
//import com.darkn0va.timber.api.getImage
//import com.darkn0va.timber.`fun`.capitalized
//import com.darkn0va.timber.ui.theme.GreyBG
//import kotlinx.coroutines.runBlocking
//
//@Composable
//fun Locations(locations: List<Location>, navController: NavController) {
//    LazyColumn(
//        modifier = Modifier.fillMaxWidth().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        items(locations) { item ->
//            var image: Bitmap? = null
//            runBlocking {
//                try {
//                    image = getImage(item.image1)
//                } catch (_: Throwable) {
//                }
//            }
//            Column(
//                modifier = Modifier.fillMaxWidth().padding(2.dp).background(GreyBG).padding(4.dp).clickable(onClick = {
//                    navController.navigate("location/${item.locationUUID}")
//                }),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Row(
//                    modifier = Modifier,
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    image?.asImageBitmap()?.let {
//                        Image(
//                            bitmap = it,
//                            contentDescription = null
//                        )
//                    }
//                }
//
//                Row(
//                    modifier = Modifier,
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(text = item.name)
//                    Spacer(modifier = Modifier.weight(1f))
//                    Column(
//                        modifier = Modifier.fillMaxWidth().padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        if (item.addressState.isNullOrBlank()) {
//                            Text(text = "${item.addressCountry.capitalized()}, ${item.addressState}")
//                            Text(text = item.addressCity.capitalized())
//                        } else {
//                            Text(text = item.addressCountry.capitalized())
//                            Text(text = item.addressCity.capitalized())
//                        }
//                    }
//                }
//            }
//        }
//    }
//}