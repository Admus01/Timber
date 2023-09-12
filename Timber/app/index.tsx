import {Button, Text, View} from 'react-native';
import {Convert, User} from "../utils/User";
import {CLIENT_ID} from "@env";

// log in   using google auth

const access_token: string = "1/fFAGRNJru1FTz70BzhT3Zg";

const user: User = Convert.toUser("{" +
    "  \"first_name\": \"Adolf\"," +
    "  \"last_name\": \"Hitler\"," +
    "  \"berear\": \"1/fFAGRNJru1FTz70BzhT3Zg\"," +
    "  \"email\": \"smrt_zidum@gmail.com\"," +
    "  \"country_phone_code\": \"88\"," +
    "  \"phone_number\": \"691 234 5678\"," +
    "  \"date_of_birth\": \"1889-04-20\"," +
    "  \"citizenship\": \"German\"}");

export default function Page() {
    return (
        <View>
            <Text>Hello from index</Text>
            <Button title="LOG IN" onPress={() => {
                console.log("Ahoj, prdel!");
                console.log(access_token);
            }} ></Button>
        </View>
    )
}
