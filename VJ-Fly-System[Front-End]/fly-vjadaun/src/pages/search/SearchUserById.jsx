import React, { useState } from "react";
import axios from "axios";

const SearchUserById = () => {
  const [searchId, setSearchId] = useState("");
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const token = localStorage.getItem("token");
  const handleSearch = async () => {
    try {
      const response =  await axios.get(`http://localhost:8080/admin1/getUserById/${searchId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUser(response.data);
      setError("");
    } catch (err) {
      setUser(null);
      setError("User not found!");
    }
  };

  return (
    <div className="w-full px-6 mt-10 flex flex-col items-center">
      <div className="flex flex-col sm:flex-row items-center gap-4 w-full max-w-xl mb-6">
        <input
          type="text"
          value={searchId}
          onChange={(e) => setSearchId(e.target.value)}
          placeholder="Enter User ID to search"
          className="w-full px-4 py-2 rounded-md border border-gray-300 shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          onClick={handleSearch}
          className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
        >
          Search
        </button>
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      {user && (
        <div className="bg-white shadow-md rounded-xl p-6 w-full max-w-xl">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">User Details</h3>
          <p><span className="font-semibold">User ID :</span> {user.id}</p>
          <p><span className="font-semibold">Name :</span> {user.name}</p>
          <p><span className="font-semibold">E-Mail :</span> {user.email}</p>
          <p><span className="font-semibold">Password :</span> {user.password}</p>
          <p><span className="font-semibold">Role :</span> {user.role}</p>
        </div>
      )}
    </div>
  );
};

export default SearchUserById;
