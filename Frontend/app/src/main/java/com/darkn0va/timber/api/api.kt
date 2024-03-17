package com.darkn0va.timber.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.darkn0va.timber.api.data.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import kotlin.time.Duration.Companion.seconds

private const val TIMEOUT = 30_000L

@OptIn(ExperimentalSerializationApi::class)
val ktorHttpClient = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            explicitNulls = false
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT
        connectTimeoutMillis = TIMEOUT
        socketTimeoutMillis = TIMEOUT
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v("Ktor =>", message)
            }
        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
            Log.d("HTTP status:", "${response.status.value}")
        }
    }
    defaultRequest {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

val supabase = createSupabaseClient(
    supabaseUrl = "https://tqowzcawaycltkneiaoe.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRxb3d6Y2F3YXljbHRrbmVpYW9lIiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODY2NDE5NTAsImV4cCI6MjAwMjIxNzk1MH0.tIxevlKr_a7YUNJI89InI3APhKpI9dE7vmw5mY_AYvk"
) {
    install(Storage) {
        transferTimeout = TIMEOUT.seconds
    }
    install(Auth) {
        alwaysAutoRefresh = false
        autoLoadFromStorage = false
    }
}

class ImageAPI(private val client: SupabaseClient) {
    suspend fun getImage(imageKey: String): Bitmap {
        val bucket = supabase.storage.from("images")
        val bytearray = bucket.downloadPublic(imageKey)
        return BitmapFactory.decodeByteArray(bytearray, 0, bytearray.size).asImageBitmap().asAndroidBitmap()
    }

    suspend fun saveImage(img: Bitmap, locationUUID: String, imgName: String) {
        val bucket = supabase.storage.from("images")
        val androidBitmap: Bitmap = img
        val outputStream = ByteArrayOutputStream()
        androidBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 92, outputStream)
        // img.asAndroidBitmap()
        bucket.upload("$locationUUID/$imgName.webp", outputStream.toByteArray(), upsert = false)
    }
}

suspend fun testAPI(client: HttpClient): String {
    return client.get {
        url {
            host = "172.20.110.230"
            port = 8089
        }
    }.body<String>().toString()
}

class LocationAPI(private val client: HttpClient) {
    suspend fun locationSearch(pageIndex: Number, addressInformation: String): List<Location>? {
        val response = client.post {
            url {
                host = Location.URL
                port = Location.PORT
                appendEncodedPathSegments("search")
            }
            setBody(
                """
                {
                    "page_index": $pageIndex,
                    "address_information": "${addressInformation.lowercase()}"
                }
                """.trimIndent()
            )
        }.body<String>()

        val locationResponse = Json.decodeFromString<LocationResponse>(response)
        return locationResponse.locations
    }

    suspend fun createLocation(newLocation: Location): String {
        return client.post {
            url {
                host = Location.URL
                port = Location.PORT
                appendEncodedPathSegments("create_location")
            }
            setBody(
                newLocation
            )
        }.body<LocationUUID>().locationUUID
    }

    suspend fun updateLocationImage(locationUUID: String, imgName: String) {
        val currentLocationUUID = LocationUUID(locationUUID)
        val locationUUIDNew = client.patch {
            url {
                host = Location.URL
                port = Location.PORT
                appendEncodedPathSegments("update_location", currentLocationUUID.locationUUID)
            }
            setBody(
                """
                {
                    "image1": "$locationUUID/$imgName.webp"
                }
                """.trimIndent()
            )
        }.body<String>()
        Log.d("UPDATE", locationUUIDNew)
    }

    suspend fun getUserLocations(userUUID: String): List<Location>? {
        val currentUserUUID = UserUUID(userUUID)
        val response = client.get {
            url {
                host = Location.URL
                port = Location.PORT
                appendEncodedPathSegments("location_by_user_uuid", currentUserUUID.userUUID)
            }
        }.body<String>()

        val locationResponse = Json.decodeFromString<LocationResponse>(response)
        return locationResponse.locations
    }

    suspend fun createBooking(booking: Booking): String {
        val response = client.post {
            url {
                host = Booking.URL
                port = Booking.PORT
                appendEncodedPathSegments("create_booking")
            }
            setBody(
                booking
            )
        }.body<String>()
        return response
    }

    suspend fun getBookedLocations(userUUID: String): List<Booking>? {
        val currentUserUUID = UserUUID(userUUID)
        val response = client.get {
            url {
                host = Booking.URL
                port = Booking.PORT
                appendEncodedPathSegments("booking_by_user_uuid", currentUserUUID.userUUID)
            }
        }.body<String>()

        val bookingResponse = Json.decodeFromString<BookingResponse>(response)
        return bookingResponse.bookings
    }

    suspend fun getLocation(locationUUID: String): Location {
        val currentLocationUUID = LocationUUID(locationUUID)
        return client.get {
            url {
                host = Location.URL
                port = Location.PORT
                appendEncodedPathSegments("location", currentLocationUUID.locationUUID)
            }
        }.body()
    }
}

class UserAPI(private val client: HttpClient) {

    suspend fun performHandshake(idToken: String): User {
        validateClient(idToken)

        return User(
            "tmmnhlxzpj1eightldxhjnone97", "Filip", "Valentiny", "valentinyfilip@protonmail.cz", "", "+420", 737015152,
            "2004-07-25".toLocalDate(), "cz"
        )
    }

    private suspend fun validateClient(idToken: String): HttpResponse {
        return client.get {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("validate_client", idToken)
            }
        }.body()
    }

    suspend fun performHandshakeDebug(idToken: String, user: User): User {
        val validatedToken = validateClientDebug(idToken).removeSurrounding("\"")
        val loginResponse = loginUser(validatedToken)

        val userLogged: User = if (loginResponse.status == HttpStatusCode.OK) {
            val userUUID = loginResponse.body<UserUUID>()
            Log.d("LOG", userUUID.toString())
            getUserData(userUUID)
        } else {
            val userUUID = registerUser(validatedToken, user).body<UserUUID>()
            Log.d("LOG", userUUID.toString())
            getUserData(userUUID)
        }

        return userLogged
    }

    private suspend fun validateClientDebug(idToken: String): String {
        return client.get {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("validate_client_debug", idToken)
            }
        }.body()
    }

    private suspend fun registerUser(idToken: String, user: User): HttpResponse {
        val newUser = User(
            null,
            user.firstName,
            user.lastName,
            user.email,
            idToken,
            user.countryPhoneCode,
            user.phoneNumber,
            user.dateOfBirth,
            user.citizenShip
        )
        return client.post {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("register")
            }
            setBody(
                newUser
            )
        }
    }

    private suspend fun loginUser(idToken: String): HttpResponse {
        return client.post {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("login")
            }
            headers {
                append(HttpHeaders.Authorization, idToken)
            }
        }
    }

    suspend fun getUserData(userUUID: UserUUID): User {
        return client.get {
            url {
                host = User.URL
                port = User.PORT
                appendEncodedPathSegments("user", userUUID.userUUID)
            }
        }.body()
    }
}