package com.darkn0va.timber

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.darkn0va.timber.api.data.User
import com.darkn0va.timber.api.ktorHttpClient
import com.darkn0va.timber.api.userAPI
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/*
fun Login(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (booleanResource(R.bool.enable_debug)) {
            Text(
                text = "DEBUG MODE"
            )
            Button(
                onClick = {
                    navController.navigate("app")
                }
            ) {
                Text(
                    text = "Login with debug"
                )
            }
        } else {
            Button(
                onClick = {

                }
            ) {
                Text(
                    text = "Login"
                )
            }
        }
    }
}*/

@Composable
fun Login(
    navController: NavController,
    onLoggedUser: (User) -> Unit,
) {
    val state = rememberOneTapSignInState()
    val api = userAPI(ktorHttpClient)
    val debug = booleanResource(R.bool.enable_debug)
    var tokenId by remember { mutableStateOf("") }

    OneTapSignInWithGoogle(
        state = state,
        clientId = stringResource(R.string.client_id),
        onTokenIdReceived = { result ->
            tokenId = result
            Log.d("LOG", result)
        },
        onDialogDismissed = { message ->
            Log.d("LOG", message)
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (debug) {
            Text(
                text = "DEBUG MODE"
            )
            Button(
                onClick = {
                    tokenId = "GQF61goqTPtSPEhPPgb8djLGFFLh"
                }
            ) {
                Text(
                    text = "Login with debug"
                )
            }
        } else {
            Button(
                onClick = {
                    state.open()
                }
            ) {
                Text(
                    text = "Login"
                )
            }
        }
    }

    LaunchedEffect(tokenId) {
        if (tokenId != "") {
            if (debug) {
                val user: User = User(null, "Filip", "Valentiny", "valentinyfilip@protonmail.cz", null, "+420", 737015152, Date("2004-07-25"), "cz")
                val currentUser = withContext(Dispatchers.IO) {
                    api.performHandshakeDebug(tokenId, user)
                }
                onLoggedUser(currentUser)
            } else {
                val user: User = User("", "Filip", "Valentiny", "valentinyfilip@protonmail.cz", null, "+420", 737015152, Date(2004725), "cz")
                val currentUser = withContext(Dispatchers.IO) {
                    api.perform_handshake(tokenId)
                }
                onLoggedUser(currentUser)
            }
            navController.navigate("app")
        }
    }
}

//@Composable
//fun CredentialSignIn(
//    state: OneTapSignInState,
//    clientId: String,
//    rememberAccount: Boolean = true,
//    nonce: String? = null,
//    onTokenIdReceived: (String) -> Unit,
//    onDialogDismissed: (String) -> Unit,
//) {
//    val googleIdOption = GetGoogleIdOption.Builder()
//        .setFilterByAuthorizedAccounts(true)
//        .setServerClientId(clientId)
//        .build()
//    val request: GetCredentialRequest = Builder()
//        .addGetCredentialOption(googleIdOption)
//        .build()
//    val credentialManager = CredentialManager.create(context)
//
//    LaunchedEffect(key1 = state.opened) {
//        if (state.opened) {
//            try {
//                val result = credentialManager.getCredential(
//                    request = request,
//                    context = activityContext,
//                )
//                handleSignIn(result)
//            } catch (e: GetCredentialException) {
//                handleFailure(e)
//            }
//        }
//    }
//}
//
//fun handleSignIn(result: GetCredentialRequest) {
//    val credential = result.credential
//
//    when (credential) {
//        is PublicKeyCredential -> {
//            // Share responseJson such as a GetCredentialResponse on your server to
//            // validate and authenticate
//            responseJson = credential.authenticationResponseJson
//        }
//
//        is PasswordCredential -> {
//            // Send ID and password to your server to validate and authenticate.
//            val username = credential.id
//            val password = credential.password
//        }
//
//        is CustomCredential -> {
//            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                try {
//                    // Use googleIdTokenCredential and extract id to validate and
//                    // authenticate on your server.
//                    val googleIdTokenCredential = GoogleIdTokenCredential
//                        .createFrom(credential.data)
//                } catch (e: GoogleIdTokenParsingException) {
//                    Log.e(TAG, "Received an invalid google id token response", e)
//                }
//            } else {
//                // Catch any unrecognized custom credential type here.
//                Log.e(TAG, "Unexpected type of credential")
//            }
//        }
//
//        else -> {
//            // Catch any unrecognized credential type here.
//            Log.e(TAG, "Unexpected type of credential")
//        }
//    }
//}

@Composable
fun OneTapSignInWithGoogle(
    state: OneTapSignInState,
    clientId: String,
    rememberAccount: Boolean = true,
    nonce: String? = null,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
) {
    val activity = LocalContext.current as Activity
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val oneTapClient = Identity.getSignInClient(activity)
                val credentials = oneTapClient.getSignInCredentialFromIntent(result.data)
                val tokenId = credentials.googleIdToken
                if (tokenId != null) {
                    onTokenIdReceived(tokenId)
                    state.close()
                }
            } else {
                onDialogDismissed("Dialog Closed.")
                state.close()
            }
        } catch (e: ApiException) {
            Log.e(TAG, "${e.message}")
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    onDialogDismissed("Dialog Canceled.")
                    state.close()
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    onDialogDismissed("Network Error.")
                    state.close()
                }

                else -> {
                    onDialogDismissed(e.message.toString())
                    state.close()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.opened) {
        if (state.opened) {
            signIn(
                activity = activity,
                clientId = clientId,
                rememberAccount = rememberAccount,
                nonce = nonce,
                launchActivityResult = { intentSenderRequest ->
                    Log.d("LOG", "launched")
                    activityLauncher.launch(intentSenderRequest)
                },
                onError = {
                    onDialogDismissed(it)
                    state.close()
                }
            )
        }
    }
}

private fun signIn(
    activity: Activity,
    clientId: String,
    rememberAccount: Boolean,
    nonce: String?,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    Log.d("LOG", "signIp")
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setNonce(nonce)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(rememberAccount)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            } catch (e: Exception) {
                onError(e.message.toString())
                Log.e(TAG, "${e.message}")
            }
        }
        .addOnFailureListener {
            signUp(
                activity = activity,
                clientId = clientId,
                nonce = nonce,
                launchActivityResult = launchActivityResult,
                onError = onError
            )
            Log.e(TAG, "${it.message}")
        }
}

private fun signUp(
    activity: Activity,
    clientId: String,
    nonce: String?,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    Log.d("LOG", "signUp")
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setNonce(nonce)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            } catch (e: Exception) {
                onError(e.message.toString())
                Log.e(TAG, "${e.message}")
            }
        }
        .addOnFailureListener {
            onError("Google Account not Found.")
            Log.e(TAG, "${it.message}")
        }
}

class OneTapSignInState {
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
fun rememberOneTapSignInState(): OneTapSignInState {
    return remember { OneTapSignInState() }
}
