// kretén

import axios from "axios";

export interface User {
    firstName: string;
    lastName: string;
    email: string;
    bearer: string;
    countryCode?: string;
    phoneNumber?: string;
    dateOfBirth: Date;
    citizenship?: string;
}

export class Convert {
    public static toUser(json: string): User {
        return JSON.parse(json);
    }
    public static toJson(value: User): string {
        return JSON.stringify(value);
    }
}

export async function registerUser(user: User) {
    try {

    }
    catch (e) {
        console.log("máš tam error retarde");
        console.log(e);
    }
}