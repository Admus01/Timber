package com.darkn0va.timber.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.darkn0va.timber.api.data.Location
import com.darkn0va.timber.api.LocationAPI
import com.darkn0va.timber.api.ktorHttpClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@Composable
fun Search(navController: NavController) {
    val api = LocationAPI(ktorHttpClient)
    val state = remeberSearchState()

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                state.open()
            }
        ) {
            Text("Test search")
        }
    }

    LaunchedEffect(state.opened) {
        if (state.opened) {
            api.locationSearch(1, "Karviná")
        }
    }
}

class SearchViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // from api
    private val _locations = MutableStateFlow(listOf<Location>())
    // history of clicked searches
    private val history = MutableStateFlow(listOf<Location>())
    // what is shown
    @OptIn(FlowPreview::class)
    val locations = searchText
        .debounce(500L)
        .onEach { _isSearching.update { true } }
        .combine(_locations) { text, locations ->
            if (text.isBlank()) {
                history
            } else {

            }
        }


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}

class SearchState {
    var opened by mutableStateOf(false)
        private set

    fun open() {
        opened = true
    }

    internal fun close() {
        opened = false
    }
}

@Composable
fun remeberSearchState(): SearchState {
    return remember { SearchState() }
}

