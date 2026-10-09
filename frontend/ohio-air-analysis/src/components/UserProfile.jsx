import {getUsers, saveUser} from "../services/userService.js";
import {useState} from "react";

function UserProfile() {
    const [user, setUser] = useState({
        name: "",
        email: ""
    });

    const [users, setUsers] = useState([]);

    const handleSave = async () => {
        try {
            const savedUser = await saveUser(user);
            console.log("Saved user:", savedUser);
            alert("Profile saved successfully!");
        } catch (error) {
            console.error("Error saving user:", error);
            alert("Failed to save profile.");
        }
    };

    const handleFetch = async () => {
        try {
            const fetchedUsers = await getUsers();
            setUsers(fetchedUsers);
        } catch (error) {
            console.error("Error fetching users:", error);
            alert("Failed to fetch profile.");
        }
    };

    return (
        <div>
            <h2>User Profile</h2>

            <input
                type="text"
                placeholder="Name"
                value={user.name}
                onChange={(e) =>
                    setUser({ ...user, name: e.target.value })
                }
            />

            <input
                type="email"
                placeholder="Email"
                value={user.email}
                onChange={(e) =>
                    setUser({ ...user, email: e.target.value })
                }
            />

            <div>
                <button onClick={handleSave}>
                    Save Profile
                </button>

                <button onClick={handleFetch}>
                    Fetch Profile
                </button>
            </div>

            <h3>Profiles</h3>

            {users.map((user) => (
                <div key={user.id}>
                    <p>Name: {user.name}</p>
                    <p>Email: {user.email}</p>
                </div>
            ))}
        </div>
    );
}

export default UserProfile;