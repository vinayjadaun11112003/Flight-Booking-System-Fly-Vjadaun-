import React, { useEffect, useState } from 'react';
import axios from 'axios';
import AdminHeader from '../../components/AdminHeader';
import Footer from '../../components/Footer';

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: '',
  });
  const [isEdit, setIsEdit] = useState(false);
  const [editId, setEditId] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  useEffect(() => {
    const filtered = users.filter(user =>
      user.name?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredUsers(filtered);
  }, [searchTerm, users]);

  const fetchUsers = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/admin1/getAllUsers", {
        headers: { Authorization: `Bearer ${token}` }
      });
      setUsers(res.data);
      setFilteredUsers(res.data);
    } catch (err) {
      console.error("Failed to fetch users", err);
    }
  };

  const handleAddOrUpdate = async () => {
    const token = localStorage.getItem("token");
    const url = isEdit
      ? `http://localhost:8080/admin1/updateUser/${editId}`
      : "http://localhost:8080/auth/register";

    const method = isEdit ? "put" : "post";

    try {
      await axios[method](url, formData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setShowForm(false);
      setFormData({ name: '', email: '', password: '', role: '' });
      alert(isEdit ? "User updated successfully" : "User added successfully");
      fetchUsers();
    } catch (err) {
      console.error("Failed to add/update user", err);
    }
  };

  const handleDelete = async (userId) => {
    if (!window.confirm("Are you sure you want to delete this user?")) return;

    const token = localStorage.getItem("token");
    try {
      await axios.delete(`http://localhost:8080/admin1/deleteUser/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("User deleted successfully");
      fetchUsers();
    } catch (err) {
      console.error("Failed to delete user", err);
    }
  };

  const openEditForm = (user) => {
    setFormData(user);
    setEditId(user.id);
    setIsEdit(true);
    setShowForm(true);
  };

  return (
    <>
      <AdminHeader />
      <div
        className="min-h-screen bg-cover bg-center px-6 py-12"
        style={{ backgroundImage: `url('/user.jpg')` }}
      >
        <div className=" bg-opacity-80 backdrop-blur-md p-6 rounded-2xl shadow-xl max-w-6xl mx-auto">
          <div className="flex flex-col sm:flex-row justify-between items-center gap-4 mb-6">
            <input
              type="text"
              placeholder="Search by user name..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full sm:w-1/2 px-4 py-2 border rounded-lg shadow-md focus:outline-none"
            />
            <button
              onClick={() => {
                setShowForm(true);
                setIsEdit(false);
                setFormData({ name: '', email: '', password: '', role: '' });
              }}
              className="bg-blue-700 hover:bg-blue-800 text-white px-5 py-2 rounded-xl transition"
            >
              + Add User
            </button>
          </div>

          <h1 className="text-3xl font-bold text-center text-red-700 mb-10">All USERS</h1>

          <div className="space-y-6">
            {filteredUsers.length > 0 ? (
              filteredUsers.map((user, index) => (
                <div key={index} className="bg-white p-6 rounded-xl shadow-md border-l-4 border-blue-500 flex flex-col md:flex-row justify-between items-start md:items-center">
                  <div>
                    <h2 className="text-xl font-semibold text-gray-800 mb-2">User #{user.id}</h2>
                    <p><strong>Name:</strong> {user.name}</p>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Password:</strong> {user.password}</p>
                    <p><strong>Role:</strong> {user.role}</p>
                  </div>
                  <div className="flex gap-3 mt-4 md:mt-0">
                    <button onClick={() => openEditForm(user)} className="bg-yellow-400 px-4 py-2 rounded-md hover:bg-yellow-500">
                      Edit
                    </button>
                    <button onClick={() => handleDelete(user.id)} className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600">
                      Delete
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-center text-gray-600">No users found.</p>
            )}
          </div>
        </div>

        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white rounded-xl p-8 w-full max-w-xl shadow-xl">
              <h2 className="text-2xl font-semibold mb-4">{isEdit ? "Update User" : "Add User"}</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <input className="border p-2 rounded" readOnly placeholder="Name" value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} />
                <input className="border p-2 rounded" readOnly placeholder="Email" value={formData.email || ''} onChange={e => setFormData({ ...formData, email: e.target.value })} />
                <input className="border p-2 rounded" readOnly placeholder="Password" value={formData.password || ''} onChange={e => setFormData({ ...formData, password: e.target.value })} />
                <input className="border p-2 rounded" placeholder="Role" value={formData.role || ''} onChange={e => setFormData({ ...formData, role: e.target.value })} />
              </div>

              <div className="mt-6 flex justify-end gap-4">
                <button className="bg-gray-300 px-4 py-2 rounded" onClick={() => setShowForm(false)}>Cancel</button>
                <button className="bg-green-600 text-white px-4 py-2 rounded" onClick={handleAddOrUpdate}>{isEdit ? "Update" : "Add"}</button>
              </div>
            </div>
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default AdminUsers;
