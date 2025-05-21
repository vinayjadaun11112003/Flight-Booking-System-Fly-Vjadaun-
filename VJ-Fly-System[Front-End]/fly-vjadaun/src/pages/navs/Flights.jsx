import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import Header from "../../components/Header";

const AllFlights = () => {
  const [flights, setFlights] = useState([]);
  const navigate = useNavigate();

  const handleBookNow = (flight) => {
    navigate(`/search/bookingForm/${flight.flightId}`);
  };

  useEffect(() => {
    const fetchFlights = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get("http://localhost:8080/Search/getAllFlights", {
          headers: { Authorization: `Bearer ${token}` }
        });
        setFlights(res.data);
      } catch (err) {
        console.error("Error fetching flights", err);
      }
    };

    fetchFlights();
  }, []);

  const formatTime = (timeStr) => {
    if (!timeStr) return "N/A";
    const date = new Date(`1970-01-01T${timeStr}`);
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", hour12: true });
  };

  return (
    <>
      <Header />
      <div
        className="min-h-screen bg-cover bg-center py-10 px-4"
        style={{ backgroundImage: "url('/flight.jpg')" }}
      >
        <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg p-6 max-w-6xl mx-auto">
          <h1 className="text-3xl md:text-4xl font-bold text-indigo-800 mb-8 text-center">
            ✈️ All Flights – Fly-VJadaun
          </h1>

          {flights.length === 0 ? (
            <p className="text-center text-gray-600">No flights found.</p>
          ) : (
            <div className="space-y-6">
              {flights.map((flight, index) => (
                <div
                  key={index}
                  className="bg-white/90 shadow-md rounded-xl p-6 flex flex-col md:flex-row justify-between items-center hover:shadow-lg transition-all border border-gray-200"
                >
                  {/* Left Section */}
                  <div className="flex flex-col md:flex-row items-center md:space-x-12 w-full md:w-3/4">
                    <div className="mb-4 md:mb-0">
                      <h2 className="text-xl font-bold text-indigo-700">{flight.flightName}</h2>
                      <p className="text-sm text-gray-500">ID: {flight.flightId}</p>
                      <p className="text-sm text-gray-600">Type: {flight.type || "N/A"}</p>
                    </div>

                    <div className="flex items-center gap-6">
                      <div className="text-center">
                        <p className="text-lg font-bold">{flight.fromCity}</p>
                        <p className="text-sm text-gray-500">{formatTime(flight.departureTime)}</p>
                        <p className="text-xs text-gray-500">{flight.departureDate}</p>
                      </div>
                      <span className="text-gray-400 text-2xl font-bold">→</span>
                      <div className="text-center">
                        <p className="text-lg font-bold">{flight.toCity}</p>
                        <p className="text-sm text-gray-500">{formatTime(flight.arrivalTime)}</p>
                        <p className="text-xs text-gray-500">{flight.arrivalDate}</p>
                      </div>
                    </div>
                  </div>

                  {/* Right Section */}
                  <div className="flex flex-col items-end space-y-1 w-full md:w-1/4 mt-4 md:mt-0 text-right">
                    <p className="text-sm text-green-700 font-semibold">
                      Economy: ₹{flight?.fareRequest?.economyFare || "N/A"}
                    </p>
                    <p className="text-sm text-blue-700 font-semibold">
                      Business: ₹{flight?.fareRequest?.businessFare || "N/A"}
                    </p>
                    <button
                      onClick={() => handleBookNow(flight)}
                      className="mt-2 px-5 py-2 bg-indigo-600 text-white rounded-md text-sm font-semibold hover:bg-indigo-700 shadow-md transition"
                    >
                      Book Now
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

export default AllFlights;
