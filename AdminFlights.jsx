import { useEffect, useState } from 'react';
import axios from 'axios';
import AdminHeader from '../../components/AdminHeader';
import Footer from '../../components/Footer';

const DEFAULT_FORM_DATA = {
  flightName: '',
  fromCity: '',
  toCity: '',
  departureDate: '',
  arrivalDate: '',
  departureTime: '',
  arrivalTime: '',
  seats: '1',
  fareRequest: {
    economyFare: '',
    businessFare: '',
  },
};

const AdminFlights = () => {
  const [flights, setFlights] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredFlights, setFilteredFlights] = useState([]);
  const [formData, setFormData] = useState(DEFAULT_FORM_DATA);
  const [showForm, setShowForm] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const [editId, setEditId] = useState(null);

  useEffect(() => {
    fetchFlights();
  }, []);

  useEffect(() => {
    const filtered = flights.filter((flight) =>
      flight.flightName?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredFlights(filtered);
  }, [searchTerm, flights]);

  const fetchFlights = async () => {
    const token = localStorage.getItem('token');
    try {
      const { data } = await axios.get('http://localhost:8080/admin2/getAllFlights', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFlights(data);
      setFilteredFlights(data);
    } catch (err) {
      console.error('Failed to fetch flights:', err);
    }
  };

  const handleAddOrUpdate = async () => {
    const token = localStorage.getItem('token');
    const url = isEdit
      ? `http://localhost:8080/admin2/updateFlight/${editId}`
      : 'http://localhost:8080/admin2/addNewFlight';
    const method = isEdit ? 'put' : 'post';

    try {
      await axios[method](url, formData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setFormData(DEFAULT_FORM_DATA);  // Reset form data after submission
      setShowForm(false);  // Close the form
      fetchFlights();  // Fetch the updated flights list
      alert(isEdit ? 'Flight updated successfully!' : 'Flight added successfully!');
    } catch (err) {
     
      alert("Incorrect amount or arrival date entered")
      console.error('Failed to add/update flight:', err);
    }
  };

  const handleDelete = async (flightId) => {
    if (!window.confirm('Are you sure you want to delete this flight?')) return;

    const token = localStorage.getItem('token');
    try {
      await axios.delete(`http://localhost:8080/admin2/delete/${flightId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchFlights();
      alert('Flight deleted successfully!');
    } catch (err) {
      console.error('Failed to delete flight:', err);
    }
  };

  const openEditForm = (flight) => {
    setFormData({
      flightName: flight.flightName,
      fromCity: flight.fromCity,
      toCity: flight.toCity,
      departureDate: flight.departureDate,
      arrivalDate: flight.arrivalDate,
      departureTime: flight.departureTime,
      arrivalTime: flight.arrivalTime,
      seats: flight.seats,
      fareRequest: {
        economyFare: flight.fareRequest.economyFare,
        businessFare: flight.fareRequest.businessFare,
      },
    });
    setEditId(flight.flightId);
    setIsEdit(true);
    setShowForm(true);
  };

  const formatTime = (time) => {
    return new Date(`1970-01-01T${time}`).toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true,
    });
  };

  return (
    <>
      <AdminHeader />
      <div
        className="min-h-screen bg-cover bg-center bg-no-repeat"
        style={{ backgroundImage: "url('/adminflight.jpg')" }}
      >
        <div className=" bg-opacity-90 min-h-screen px-4 md:px-16 py-10">
          <div className="flex flex-col md:flex-row justify-between items-center mb-6 gap-4">
            <input
              type="text"
              placeholder="Search by flight name..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg w-full md:w-1/3 shadow"
            />
            <button
              onClick={() => {
                setShowForm(true);
                setIsEdit(false);
                setFormData(DEFAULT_FORM_DATA);
              }}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
            >
              + Add Flight
            </button>
          </div>

          <h2 className="text-3xl font-bold text-center text-white mb-8">Manage Flights</h2>

          <div className="space-y-6">
            {filteredFlights.length ? (
              filteredFlights.map((flight) => (
                <div
                  key={flight.flightId}
                  className="bg-white rounded-xl shadow-lg border border-gray-200 p-6 flex flex-col md:flex-row justify-between items-start md:items-center gap-4"
                >
                  <div className="flex-1">
                    <h3 className="text-xl font-semibold mb-2">{flight.flightName}</h3>
                    <div className="text-gray-700 grid grid-cols-2 md:grid-cols-3 gap-2">
                      <p><strong>From:</strong> {flight.fromCity}</p>
                      <p><strong>To:</strong> {flight.toCity}</p>
                      <p><strong>Departure:</strong> {flight.departureDate} - {formatTime(flight.departureTime)}</p>
                      <p><strong>Arrival:</strong> {flight.arrivalDate} - {formatTime(flight.arrivalTime)}</p>
                      <p><strong>Economy:</strong> ₹{flight.fareRequest?.economyFare}</p>
                      <p><strong>Business:</strong> ₹{flight.fareRequest?.businessFare}</p>
                    </div>
                  </div>
                  <div className="flex gap-3">
                    <button
                      onClick={() => openEditForm(flight)}
                      className="bg-yellow-400 hover:bg-yellow-500 px-4 py-2 rounded"
                    >
                      Update
                    </button>
                    <button
                      onClick={() => handleDelete(flight.flightId)}
                      className="bg-red-500 text-white hover:bg-red-600 px-4 py-2 rounded"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-center text-gray-600">No flights found.</p>
            )}
          </div>
        </div>
      </div>

      {/* Add or Edit Flight Form */}
      {showForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white rounded-xl p-6 w-1/2 shadow-xl">
            <h2 className="text-xl font-semibold mb-4">{isEdit ? 'Update Flight' : 'Add New Flight'}</h2>

            {/* Form Fields */}
            <div className="space-y-4">
              <input
                className="w-full p-2 border border-gray-300 rounded"
                placeholder="Flight Name"
                value={formData.flightName}
                onChange={(e) => setFormData({ ...formData, flightName: e.target.value })}
              />
              <input
                className="w-full p-2 border border-gray-300 rounded"
                placeholder="From City"
                value={formData.fromCity}
                onChange={(e) => setFormData({ ...formData, fromCity: e.target.value })}
              />
              <input
                className="w-full p-2 border border-gray-300 rounded"
                placeholder="To City"
                value={formData.toCity}
                onChange={(e) => setFormData({ ...formData, toCity: e.target.value })}
              />
              <input
                className="w-full p-2 border border-gray-300 rounded"
                type="date"
                placeholder="Departure Date"
                value={formData.departureDate}
                onChange={(e) => setFormData({ ...formData, departureDate: e.target.value })}
              />
              <input
                className="w-full p-2 border border-gray-300 rounded"
                type="date"
                placeholder="Arrival Date"
                value={formData.arrivalDate}
                onChange={(e) => setFormData({ ...formData, arrivalDate: e.target.value })}
              />
              <input
                className="w-full p-2 border border-gray-300 rounded"
                type="time"
                placeholder="Departure Time"
                value={formData.departureTime}
                onChange={(e) => setFormData({ ...formData, departureTime: e.target.value })}
              />
              <input
                className="w-full p-2 border border-gray-300 rounded"
                type="time"
                placeholder="Arrival Time"
                value={formData.arrivalTime}
                onChange={(e) => setFormData({ ...formData, arrivalTime: e.target.value })}
              />
         
              <input
                className="w-full p-2 border border-gray-300 rounded"
                placeholder="Economy Fare"
                value={formData.fareRequest.economyFare}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    fareRequest: { ...formData.fareRequest, economyFare: e.target.value },
                  })
                }
              />
              <input
                className="w-full p-2 border border-gray-300 rounded"
                placeholder="Business Fare"
                value={formData.fareRequest.businessFare}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    fareRequest: { ...formData.fareRequest, businessFare: e.target.value },
                  })
                }
              />
            </div>

            <div className="mt-4 flex justify-end gap-3">
              <button
                className="bg-gray-400 px-4 py-2 rounded"
                onClick={() => setShowForm(false)}
              >
                Cancel
              </button>
              <button
                className="bg-blue-600 text-white px-4 py-2 rounded"
                onClick={handleAddOrUpdate}
              >
                {isEdit ? 'Update Flight' : 'Add Flight'}
              </button>
            </div>
          </div>
        </div>
      )}

      <Footer />
    </>
  );
};

export default AdminFlights;
