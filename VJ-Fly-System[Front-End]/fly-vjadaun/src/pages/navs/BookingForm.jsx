import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { useUser } from "../../components/userContext";
import Header from "../../components/Header";

const BookingForm = () => {
  const { id } = useParams(); // Flight ID from route
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user"));

  useEffect(() => {
    if (user) {
      setFormData(prev => ({
        ...prev,
        userId: user.id,
        email: user.email
      }));
    }
  }, [user]);

  const [formData, setFormData] = useState({
    userId: '',
    flightId: id,
    name: "",
    age: "",
    address: "",
    email: "",
    fromCity: "",
    toCity: "",
    departure: "",
    departureTime: "",
    arrival: "",
    arrivalTime: "",
    type: "business",
    paymentAmount: 0,
  });

  useEffect(() => {
    const fetchFlight = async () => {
      if (id === null) return;
      try {
        const res = await axios.get(`http://localhost:8080/Search/getFlightById/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = res.data;

        setFormData((prev) => ({
          ...prev,
          flightId: data.flightId,
          fromCity: data.fromCity,
          toCity: data.toCity,
          departure: data.departureDate,
          arrival: data.arrivalDate,
          departureTime: data.departureTime,
          arrivalTime: data.arrivalTime,
          paymentAmount: (formData.type === "economyFare" ? data.fareRequest.economyFare : data.fareRequest.businessFare)
        }));
      } catch (error) {
        console.error("Failed to fetch flight details:", error);
      }
    };

    fetchFlight();
  }, [id, formData.type]);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("http://localhost:8080/BookingService/addNewBooking", formData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const sessionUrl = res.data.sessionUrl;
      console.log(res.data)
      window.location.href = sessionUrl;
    } catch (err) {
      console.error("Booking failed:", err);
      alert("Booking failed!");
    }
  };

  return (
    <>
      <Header />
      <div className="min-h-screen bg-cover bg-center" style={{ backgroundImage: `url('/bookingform.jpg')` }}>
        <div className="flex justify-center items-center min-h-screen  bg-opacity-50">
          <div className="max-w-4xl w-full p-8 bg-white rounded-xl shadow-lg border border-gray-200">
            <h2 className="text-3xl font-bold text-center text-blue-700 mb-6">Flight Booking Form</h2>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="text"
                  name="userId"
                  placeholder="User ID"
                  value={formData.userId}
                  onChange={handleChange}
                  className="w-full p-4 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
                <input
                  type="text"
                  name="flightId"
                  value={formData.flightId}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="email"
                  name="email"
                  placeholder="E-Mail"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full p-4 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  readOnly
                />
                <input
                  type="text"
                  name="name"
                  placeholder="Full Name"
                  value={formData.name}
                  onChange={handleChange}
                  className="w-full p-4 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="number"
                  name="age"
                  placeholder="Age"
                  value={formData.age}
                  onChange={handleChange}
                  className="w-full p-4 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
                <input
                  type="text"
                  name="address"
                  placeholder="Address"
                  value={formData.address}
                  onChange={handleChange}
                  className="w-full p-4 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="text"
                  name="fromCity"
                  value={formData.fromCity}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
                <input
                  type="text"
                  name="toCity"
                  value={formData.toCity}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="date"
                  name="departureDate"
                  value={formData.departure}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
                <input
                  type="time"
                  name="departureTime"
                  value={formData.departureTime}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="date"
                  name="arrivalDate"
                  value={formData.arrival}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
                <input
                  type="time"
                  name="arrivalTime"
                  value={formData.arrivalTime}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
              </div>
              <div className="mt-4">
                <select
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                  className="w-full p-4 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Select Fare Type</option>
                  <option value="economyFare">Economy</option>
                  <option value="businessFare">Business</option>
                </select>
              </div>
              <div className="mt-4">
                <input
                  type="text"
                  name="paymentAmount"
                  value={`${formData.paymentAmount} ₹`}
                  readOnly
                  className="w-full p-4 border border-gray-300 rounded-lg bg-gray-100"
                />
              </div>
              <div className="mt-6">
                <button
                  type="submit"
                  className="w-full py-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  Confirm Booking
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
};

export default BookingForm;
