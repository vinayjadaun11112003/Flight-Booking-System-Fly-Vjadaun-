import React, { useEffect, useState } from "react";
import axios from "axios";
import Header from "../../components/Header";

const Booking = () => {
  const [bookings, setBookings] = useState([]);
  const [checkinInputs, setCheckinInputs] = useState({});
  const [checkinResults, setCheckinResults] = useState({});

  useEffect(() => {
    const fetchBookings = async () => {
      const user = JSON.parse(localStorage.getItem("user"));
      const token = localStorage.getItem("token");
      if (!user?.id) return alert("User not logged in");

      try {
        const res = await axios.get(
          "http://localhost:8080/BookingService/getAllBookings",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const userBookings = res.data.filter(
          (booking) => booking.userId === user.id
        );
        setBookings(userBookings);
      } catch (err) {
        console.error("Failed to fetch bookings", err);
      }
    };

    fetchBookings();
  }, []);

  const handleCheckIn = async (bookingId) => {
    const seatNo = checkinInputs[bookingId.id];
    if (!seatNo || !/^[1-9][0-9]?[A-D]$/.test(seatNo.toUpperCase())) {
      alert("Please enter a valid seat number like 1A, 2B, 10C...");
      return;
    }

    try {
      const token = localStorage.getItem("token");
      const user = JSON.parse(localStorage.getItem("user"));
      const res = await axios.post(
        `http://localhost:8080/CheckinService/CheckIn/${bookingId.flightId}/${bookingId.id}/${user.email}/${seatNo.toUpperCase()}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setCheckinResults((prev) => ({
        ...prev,
        [bookingId.id]: res.data,
      }));
    } catch (err) {
      console.error("Check-in failed", err);
      setCheckinResults((prev) => ({
        ...prev,
        [bookingId.id]: "❌ Check-in failed. Try again.",
      }));
    }
  };

  const handleCancelBooking = async (bookingId) => {
    const token = localStorage.getItem("token");
    try {
      await axios.delete(
        `http://localhost:8080/BookingService/deleteById/${bookingId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert("Booking cancelled successfully ✅");
      setBookings((prev) => prev.filter((b) => b.id !== bookingId));
    } catch (err) {
      console.error("Booking cancellation failed ❌", err);
      alert("Failed to cancel booking. Please try again.");
    }
  };

  const handleInputChange = (bookingId, value) => {
    setCheckinInputs((prev) => ({
      ...prev,
      [bookingId]: value,
    }));
  };

  return (
    <>
      <Header />
      <div
        className="min-h-screen bg-cover bg-center py-10 px-4"
        style={{ backgroundImage: "url('/booking.jpg')" }}
      >
        <div className="max-w-5xl mx-auto bg-white/80 backdrop-blur-md rounded-2xl shadow-xl p-6">
          <h2 className="text-3xl font-bold text-center text-indigo-800 mb-8">
            Your Bookings
          </h2>

          {bookings.length === 0 ? (
            <p className="text-center text-gray-700">No bookings found.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {bookings.map((booking, idx) => (
                <div
                  key={idx}
                  className="bg-white/90 shadow-md rounded-xl p-6 space-y-3 border border-gray-200 hover:shadow-lg transition-all"
                >
                  <h3 className="text-xl font-bold text-indigo-700">
                    ✈️ {booking.fromCity} → {booking.toCity}
                  </h3>

                  <div className="text-sm text-gray-700">
                    <p>
                      <strong>Passenger:</strong> {booking.name} ({booking.age} yrs)
                    </p>
                    <p>
                      <strong>Address:</strong> {booking.address}
                    </p>
                    <p>
                      <strong>Flight ID:</strong> {booking.flightId}
                    </p>
                  </div>

                  <div className="text-sm text-gray-700 mt-2">
                    <p>
                      <strong>Departure:</strong> {booking.departure || "N/A"}{" "}
                      <span className="ml-2 text-blue-600 font-medium">
                        {booking.departureTime || "N/A"}
                      </span>
                    </p>
                    <p>
                      <strong>Arrival:</strong> {booking.arrival || "N/A"}{" "}
                      <span className="ml-2 text-green-600 font-medium">
                        {booking.arrivalTime || "N/A"}
                      </span>
                    </p>
                  </div>

                  <p className="text-sm text-gray-700">
                    <strong>Travel Class:</strong>{" "}
                    <span className="text-blue-700 font-semibold">
                      {booking.type || "N/A"}
                    </span>
                  </p>

                  <p className="text-sm text-gray-700 break-all">
                    <strong>Payment ID:</strong>{" "}
                    <span className="text-gray-800">
                      {booking.paymentId || "N/A"}
                    </span>
                  </p>

                  <p className="text-sm text-green-700 font-bold">
                    Amount Paid: ₹{booking.paymentAmount || "0"}
                  </p>

                  {/* 🔘 Check-In Panel */}
                  <div className="mt-4 space-y-2">
                    <input
                      type="text"
                      placeholder="Seat No (e.g. 1A)"
                      value={checkinInputs[booking.id] || ""}
                      onChange={(e) =>
                        handleInputChange(booking.id, e.target.value)
                      }
                      className="border border-gray-300 rounded p-2 w-full"
                    />
                    <button
                      onClick={() => handleCheckIn(booking)}
                      className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700"
                    >
                      ✅ Check-In
                    </button>
                    {checkinResults[booking.id] && (
                      <p className="text-sm text-green-600 font-semibold mt-2">
                        {checkinResults[booking.id]}
                      </p>
                    )}

                    {/* ❌ Cancel Button */}
                    <button
                      onClick={() => handleCancelBooking(booking.id)}
                      className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 mt-2"
                    >
                      ❌ Cancel Booking
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Booking;
