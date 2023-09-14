export interface User {
    user: {
        id: string;
        name: string | null;
        email: string;
        photo: string | null;
        familyName: string | null;
        givenName: string | null;
    };
    scopes?: string[];
    idToken: string | null;
    /**
     * Not null only if a valid webClientId and offlineAccess: true was
     * specified in configure().
     */
    serverAuthCode: string | null;
}

export const defaultUser: User = {
    user: {
        id: "",
        name: null,
        email: "",
        photo: null,
        familyName: null,
        givenName: null,
    },
    idToken: null,
    serverAuthCode: null,
}

export class Convert {
    public static toUser(json: string): User {
        return JSON.parse(json);
    }
    public static toJson(value: User): string {
        return JSON.stringify(value);
    }
}