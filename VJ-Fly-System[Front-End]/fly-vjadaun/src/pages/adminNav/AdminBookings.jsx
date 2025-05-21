import { useEffect, useState } from 'react';
import axios from 'axios';
import AdminHeader from '../../components/AdminHeader';
import Footer from '../../components/Footer';
import './Admin.css';

const AdminBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [filteredBookings, setFilteredBookings] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState(initialFormData());
  const [isEdit, setIsEdit] = useState(false);
  const [editId, setEditId] = useState(null);

  function initialFormData() {
    return {
      userId: '',
      flightId: '',
      email: '',
      name: '',
      age: '',
      address: '',
      fromCity: '',
      toCity: '',
      departure: '', // Date input
      departureTime: '', // Time input
      arrival: '', // Date input
      arrivalTime: '', // Time input
      type: '',
      paymentAmount: '',
      paymentId: '',
    };
  }

  useEffect(() => {
    fetchBookings();
  }, []);

  useEffect(() => {
    const filtered = bookings.filter(booking =>
      booking.name?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredBookings(filtered);
  }, [searchTerm, bookings]);

  const fetchBookings = async () => {
    const token = localStorage.getItem('token');
    try {
      const res = await axios.get('http://localhost:8080/admin3/getAllBookings', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setBookings(res.data);
      setFilteredBookings(res.data);
    } catch (error) {
      console.error('Failed to fetch bookings', error);
    }
  };

  const handleAddOrUpdate = async () => {
    const token = localStorage.getItem('token');
    const url = isEdit
      ? `http://localhost:8080/admin3/updateById/${editId}`
      : 'http://localhost:8080/admin3/addBooking';
    const method = isEdit ? 'put' : 'post';

    try {
      await axios[method](url, {
        ...formData,
        id: isEdit ? editId : undefined,
      }, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert(isEdit ? 'Successfully updated booking' : 'Booking added successfully');
      setFormData(initialFormData());
      setShowForm(false);
      setIsEdit(false);
      setEditId(null);
      fetchBookings();
    } catch (error) {
      console.error('Failed to add/update booking', error);
    }
  };

  const handleDelete = async (bookingId) => {
    if (!window.confirm('Are you sure you want to delete this booking?')) return;

    const token = localStorage.getItem('token');
    try {
      await axios.delete(`http://localhost:8080/admin3/deleteById/${bookingId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert('Booking successfully deleted');
      fetchBookings();
    } catch (error) {
      console.error('Failed to delete booking', error);
    }
  };

  const openEditForm = (booking) => {
    setFormData(booking);
    setEditId(booking.id);
    setIsEdit(true);
    setShowForm(true);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <>
      <AdminHeader />
      <div className="min-h-screen bg-cover bg-center bg-no-repeat" style={{ backgroundImage: "url('/admin.jpg')" }}>
        <div className="backdrop-blur-md min-h-screen p-8 md:px-20">
          <div className="flex flex-col md:flex-row justify-between items-center mb-8">
            <input
              type="text"
              placeholder="🔍 Search bookings by name..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="px-4 py-2 w-full md:w-1/3 rounded-2xl border border-gray-300 shadow-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
            <button
              onClick={() => {
                setFormData(initialFormData());
                setIsEdit(false);
                setShowForm(true);
              }}
              className="bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white px-6 py-2 rounded-full shadow-lg mt-4 md:mt-0"
            >
              + Add Booking
            </button>
          </div>

          <h1 className="text-4xl font-extrabold text-center text-blue-800 mb-12 drop-shadow-lg">
            ✈️ Manage Flight Bookings
          </h1>

          {/* FORM */}
          {showForm && (
            <div className="bg-white p-8 rounded-3xl shadow-2xl mb-10">
              <h2 className="text-2xl font-bold mb-6">{isEdit ? 'Update Booking' : 'Add Booking'}</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {Object.keys(formData).map((field) => (
                  <input
                    key={field}
                    name={field}
                    value={formData[field]}
                    onChange={handleInputChange}
                    placeholder={field}
                    type={
                      field === 'departure' || field === 'arrival'
                        ? 'date' // Date input for departure and arrival
                        : field === 'departureTime' || field === 'arrivalTime'
                        ? 'time' // Time input for departure and arrival time
                        : 'text' // Default text input for other fields
                    }
                    className="px-4 py-2 rounded-lg border border-gray-300 shadow focus:outline-none focus:ring-2 focus:ring-blue-400"
                  />
                ))}
              </div>
              <div className="flex justify-end gap-4 mt-6">
                <button
                  onClick={() => {
                    setShowForm(false);
                    setIsEdit(false);
                    setFormData(initialFormData());
                  }}
                  className="px-6 py-2 rounded-full bg-gray-400 text-white hover:bg-gray-500"
                >
                  Cancel
                </button>
                <button
                  onClick={handleAddOrUpdate}
                  className="px-6 py-2 rounded-full bg-blue-600 text-white hover:bg-blue-700"
                >
                  {isEdit ? 'Update' : 'Add'}
                </button>
              </div>
            </div>
          )}

          {/* LIST */}
          <div className="flex flex-col gap-6">
            {filteredBookings.length > 0 ? (
              filteredBookings.map((booking, index) => (
                <div
                  key={index}
                  className="flex flex-col md:flex-row items-center justify-between gap-6 p-6 rounded-3xl bg-white shadow-2xl hover:scale-[1.02] transition-all border border-gray-300"
                >
                  <div className="flex-1 space-y-1">
                    <h2 className="text-2xl font-semibold text-blue-700">{booking.name}</h2>
                    <p><strong>Booking ID:</strong> {booking.id}</p>
                    <p><strong>User ID:</strong> {booking.userId}</p>
                    <p><strong>Flight ID:</strong> {booking.flightId}</p>
                    <p><strong>From:</strong> {booking.fromCity} → <strong>To:</strong> {booking.toCity}</p>
                    <p><strong>Departure:</strong> {booking.departure} at {booking.departureTime}</p>
                    <p><strong>Arrival:</strong> {booking.arrival} at {booking.arrivalTime}</p>
                  </div>
                  <div className="flex-1 space-y-1 text-sm text-gray-700 break-all">
                    <p><strong>Email:</strong> {booking.email}</p>
                    <p><strong>Age:</strong> {booking.age}</p>
                    <p><strong>Type:</strong> {booking.type}</p>
                    <p><strong>Payment:</strong> ₹{booking.paymentAmount}</p>
                    <p className="break-words"><strong>Payment ID:</strong> {booking.paymentId}</p>
                  </div>
                  <div className="flex flex-col gap-2">
                    <button
                      onClick={() => openEditForm(booking)}
                      className="px-4 py-2 bg-yellow-400 hover:bg-yellow-500 text-white rounded-full shadow-md"
                    >
                      Update
                    </button>
                    <button
                      onClick={() => handleDelete(booking.id)}
                      className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-full shadow-md"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-center text-gray-600">No bookings found.</p>
            )}
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default AdminBookings;
