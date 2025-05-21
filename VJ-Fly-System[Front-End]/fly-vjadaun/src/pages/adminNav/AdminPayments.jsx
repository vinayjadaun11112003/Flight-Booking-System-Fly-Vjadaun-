import { useEffect, useState } from 'react';
import axios from 'axios';
import AdminHeader from '../../components/AdminHeader';
import './Admin.css';
import Footer from '../../components/Footer';

const AdminPayment = () => {
  const [flights, setFlights] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    sessionId: '',
    status: '',
    currency: '',
    amount: '',
    // createdAt: '',
  });

  useEffect(() => {
    fetchFlights();
  }, []);

  useEffect(() => {
    const filtered = flights.filter(user =>
      user.status?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredUsers(filtered);
  }, [searchTerm, flights]);

  const fetchFlights = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/admin4/getAllPayments", {
        headers: { Authorization: `Bearer ${token}` }
      });
      setFlights(res.data);
      setFilteredUsers(res.data);
    } catch (err) {
      console.error("Failed to fetch flights", err);
    }
  };

  const handleFormSubmit = async () => {
    const token = localStorage.getItem("token");

    try {
      await axios.post("http://localhost:8080/admin4/addNewPayment", formData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setShowForm(false);
      setFormData({
        sessionId: '',
        status: '',
        currency: '',
        amount: '',
        // createdAt: '',
      });
      alert("Payment added to the system successfully");
      fetchFlights();
    } catch (err) {
      console.error("Failed to add payment", err);
    }
  };

  const handleDelete = async (flightId) => {
    const confirmDelete = window.confirm("Are you sure you want to delete this payment?");
    if (!confirmDelete) return;

    const token = localStorage.getItem("token");
    try {
      await axios.delete(`http://localhost:8080/admin4/deleteBySessionId/${flightId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchFlights();
      alert("Payment Successfully Deleted");
    } catch (err) {
      console.error("Failed to delete payment", err);
    }
  };

  const openEditForm = (flight) => {
    setFormData(flight); // Set the form data to the flight details
    setShowForm(true); // Show the form for editing
  };

  return (
    <>
      <div className="relative min-h-screen">
        {/* Background Image */}
        <div className="absolute inset-0 bg-cover bg-center opacity-90" style={{ backgroundImage: "url('/adminflight.jpg')" }}></div>

        <div className="relative z-10">
          <AdminHeader />
          
          <div className="px-8 py-10">
            <div className="flex justify-between items-center mb-8">
              <input
                type="text"
                placeholder="Search by payment status..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="px-4 py-2 border rounded-lg w-full max-w-md shadow"
              />
              <button
                onClick={() => {
                  setFormData({
                    sessionId: '',
                    status: '',
                    currency: '',
                    amount: '',
                    // createdAt: '',
                  });
                  setShowForm(true);
                }}
                className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-full shadow-md"
              >
                Add Payment
              </button>
            </div>

            <h2 className="text-3xl font-bold mb-6">Manage Payments</h2>

            {/* Form Section */}
            {showForm && (
              <div className="bg-white p-8 rounded-lg shadow-lg mb-8">
                <h3 className="text-2xl font-semibold">{formData.sessionId ? 'Edit Payment' : 'Add Payment'}</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6">
                  {Object.keys(formData).map((field) => (
                    <input
                      key={field}
                      type="text"
                      name={field}
                      placeholder={field}
                      value={formData[field]}
                      onChange={(e) => setFormData({ ...formData, [field]: e.target.value })}
                      className="px-4 py-2 rounded-lg border border-gray-300 shadow-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                    />
                  ))}
                </div>

                <div className="flex justify-end gap-4 mt-6">
                  <button
                    onClick={() => setShowForm(false)}
                    className="px-6 py-2 rounded-full bg-gray-400 text-white hover:bg-gray-500"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleFormSubmit}
                    className="px-6 py-2 rounded-full bg-blue-600 text-white hover:bg-blue-700"
                  >
                    {formData.sessionId ? 'Update' : 'Add'}
                  </button>
                </div>
              </div>
            )}

            {/* List of Payments */}
            <div className="space-y-4">
              {filteredUsers.length > 0 ? (
                filteredUsers.map((flight) => (
                  <div
                    key={flight.id}
                    className="flex justify-between items-center p-6 rounded-xl shadow-lg bg-white hover:scale-[1.02] transition-all border border-gray-200"
                  >
                    <div className="space-y-2">
                      <h4 className="text-xl font-semibold break-all">{flight.sessionId}</h4>
                      <p><strong>Status:</strong> {flight.status}</p>
                      <p><strong>Amount:</strong> ₹{flight.amount}</p>
                      <p><strong>Currency:</strong> {flight.currency}</p>
                      <p><strong>Created At:</strong> {flight.createdAt}</p>
                    </div>

                    <div className="flex flex-col gap-4">
                      <button
                        onClick={() => openEditForm(flight)} // Open edit form with selected payment data
                        className="px-4 py-2 bg-yellow-400 hover:bg-yellow-500 text-white rounded-full shadow-md"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(flight.id)}
                        className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-full shadow-md"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-center text-gray-600">No payments found</p>
              )}
            </div>
          </div>
        </div>
      </div>

      <Footer />
    </>
  );
};

export default AdminPayment;
