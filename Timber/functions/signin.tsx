// import statusCodes along with GoogleSignin
import {GoogleSignin, statusCodes} from '@react-native-google-signin/google-signin';
import {User} from "../utils/User";

// Somewhere in your code
export async function signIn(): Promise<User | undefined> {
    try {
        await GoogleSignin.hasPlayServices();
        return await GoogleSignin.signIn();
    } catch (error: any) {
        if (error.code === statusCodes.SIGN_IN_CANCELLED) {
            // user cancelled the login flow
            console.log("User cancelled the login flow");
        } else if (error.code === statusCodes.IN_PROGRESS) {
            // operation (e.g. sign in) is in progress already
            console.log("Operation (e.g. sign in) is in progress already");
        } else if (error.code === statusCodes.PLAY_SERVICES_NOT_AVAILABLE) {
            // play services not available or outdated
            console.log("Play services not available or outdated");
        } else {
            // some other error happened
            console.log("Some other error happened");
        }
    }
}

export async function silentSignIn(): Promise<User | undefined> {
    try {
        return await GoogleSignin.signInSilently();
    } catch (error: any) {
        if (error.code === statusCodes.SIGN_IN_REQUIRED) {
            // user has not signed in yet
            console.log("User has not signed in yet");
        } else if (error.code === statusCodes.SIGN_IN_CANCELLED) {
            // user cancelled the login flow
            console.log("User cancelled the login flow");
        } else if (error.code === statusCodes.IN_PROGRESS) {
            // operation (e.g. sign in) is in progress already
            console.log("Operation (e.g. sign in) is in progress already");
        } else if (error.code === statusCodes.PLAY_SERVICES_NOT_AVAILABLE) {
            // play services not available or outdated
            console.log("Play services not available or outdated");
        } else {
            // some other error happened
            console.log("Some other error happened");
        }
    }
}