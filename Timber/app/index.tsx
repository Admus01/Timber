import {Button, Text, View} from 'react-native';
import {CLIENT_ID_WEB} from "@env";

import {GoogleSignin, GoogleSigninButton} from '@react-native-google-signin/google-signin';
import {signIn, silentSignIn} from "../functions/signin";
import {useEffect, useState} from "react";
import {defaultUser, User} from "../utils/User";

GoogleSignin.configure({
    offlineAccess: true,
    webClientId: CLIENT_ID_WEB,
});

export default function Page() {
    useEffect(() => {
        silentSignIn().then((user) => {
            user ? setUser(user) : null;
        });
    }, []);

    const [user, setUser] = useState<User>(defaultUser);

    return (
        <View>
            @env.PRODUCTION ? {

            <View>
                <Text>DEVELOPMENT</Text>
                <Button title="LOG IN" onPress={() => {
                    console.log("Ahoj, slovo!");
                }}></Button>
            </View>

        } : null

            <GoogleSigninButton
                size={GoogleSigninButton.Size.Wide}
                color={GoogleSigninButton.Color.Dark}
                onPress={
                    () => {
                        signIn().then((user) => {
                            user ? setUser(user) : null;
                        });
                    }
                }
                disabled={false}
            />;
        </View>
    )
}
