import axios from "axios";

export async function getUsers() {
    const response = await axios.get(
        "/api/users/getUsers"
    );

    return response.data;
}

export async function saveUser(user) {
    const response = await axios.post(
        "/api/users/save",
        user
    );

    return response.data;
}