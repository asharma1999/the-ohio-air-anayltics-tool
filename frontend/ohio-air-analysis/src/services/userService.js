import axios from "axios";

export async function getUsers() {
    const response = await axios.get(
        "http://localhost:8080/api/users/getUsers"
    );

    return response.data;
}

export async function saveUser(user) {
    const response = await axios.post(
        "http://localhost:8080/api/users/save",
        user
    );

    return response.data;
}