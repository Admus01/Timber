import {User, Convert} from "../utils/User";
import axios from "axios";

const localHost = axios.create({
    baseURL: "http://10.0.1.29:8000/",
    headers: {
        "Access-Control-Allow-Origin": "*",
        "Content-Type": "application/json",
    }
})
const prod = axios.create({})

export async function postUser(user: User) {
    try {
        const response = await localHost.post("/register", Convert.toJson(user));
        console.log(response);
    }
    catch (e) {
        console.log("máš tam error retarde");
        console.log(e);
    }
}

export async function getSlash() {
    try {
        const response = await localHost.get("/");
        console.log(response);
    }
    catch (e) {
        console.log("máš tam error retarde");
        console.log(e);
    }
}