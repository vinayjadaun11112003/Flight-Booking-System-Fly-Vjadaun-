import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import Header from "../../components/Header";

const SearchFlights = () => {
  const navigate = useNavigate();
  const [searchData, setSearchData] = useState({
    fromCity: "",
    toCity: "",
    departureDate: "",
  });

  const [flights, setFlights] = useState([]);
  const [error, setError] = useState("");

  const handleBookNow = (flight) => {
    console.log("Booking flight:", flight);
    // redirect to booking page
    navigate(`/search/bookingForm/${flight.flightId}`); // if you're using react-router-dom
  };

  const handleChange = (e) => {
    setSearchData({ ...searchData, [e.target.name]: e.target.value });
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    try {
      const res = await axios.post(
        "http://localhost:8080/Search/search",
        searchData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setFlights(res.data);
      console.log(res.data);
      setError("");
    } catch (err) {
      setFlights([]);
      setError("No flights found or server error.");
    }
  };

  const formatTime = (timeString) => {
    const date = new Date(`1970-01-01T${timeString}`);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true });
  };

  return (
    <>
      <Header />
      <div
        className="min-h-screen bg-cover bg-center flex flex-col items-center justify-center"
        style={{ backgroundImage: `url('/search.jpg')` }}
      >
        <div className="text-center mb-10">
          <h1 className="text-4xl md:text-5xl font-extrabold text-white drop-shadow-lg mb-2">
            Welcome to Fly-VJadaun ✈️
          </h1>
          <p className="text-white text-lg font-medium drop-shadow-md">
            Search and book your next destination in seconds
          </p>
        </div>

        <div className="bg-white shadow-xl rounded-2xl p-8 w-full max-w-3xl">
          <h2 className="text-2xl font-bold text-blue-700 text-center mb-6">Search Flights</h2>

          <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <input
              type="text"
              name="fromCity"
              placeholder="From City"
              value={searchData.fromCity}
              onChange={handleChange}
              className="border border-gray-300 p-3 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
              required
            />
            <input
              type="text"
              name="toCity"
              placeholder="To City"
              value={searchData.toCity}
              onChange={handleChange}
              className="border border-gray-300 p-3 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
              required
            />
            <input
              type="date"
              name="departureDate"
              value={searchData.departureDate}
              onChange={handleChange}
              className="border border-gray-300 p-3 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
              required
            />
            <div className="md:col-span-3 text-center">
              <button
                type="submit"
                className="mt-2 px-6 py-3 bg-blue-600 text-white rounded-xl font-semibold shadow hover:bg-blue-700 transition"
              >
                🔍 Search Flights
              </button>
            </div>
          </form>

          {error && <p className="text-red-500 mt-4 text-center">{error}</p>}
        </div>

        {/* Flight results if available */}
        {flights.length > 0 && (
          <div className="mt-12 w-full max-w-5xl space-y-6">
            <h3 className="text-2xl font-bold text-white mb-4 text-center">Search Results</h3>

            {flights.map((flight, idx) => (
              <div
                key={idx}
                className="bg-white shadow-lg rounded-xl p-6 flex flex-col md:flex-row justify-between items-start md:items-center hover:shadow-xl transition-all border border-gray-100"
              >
                <div className="flex flex-col md:flex-row md:items-center md:space-x-6 w-full md:w-3/5">
                  <div>
                    <h4 className="text-xl font-bold text-indigo-700">{flight.flightName}</h4>
                    <p className="text-sm text-gray-500">Flight ID: {flight.flightId}</p>
                  </div>

                  <div className="flex items-center mt-4 md:mt-0">
                    <div className="text-center">
                      <p className="text-lg font-semibold text-gray-800">{flight.fromCity}</p>
                      <p className="text-sm text-gray-500">{formatTime(flight.departureTime)}</p>
                    </div>
                    <span className="mx-4 text-gray-400 text-xl">→</span>
                    <div className="text-center">
                      <p className="text-lg font-semibold text-gray-800">{flight.toCity}</p>
                      <p className="text-sm text-gray-500">{formatTime(flight.arrivalTime)}</p>
                    </div>
                  </div>
                </div>

                <div className="flex flex-col items-end space-y-1 mt-4 md:mt-0 text-right">
                  <p className="text-sm text-gray-600">
                    <strong>Departure:</strong> {flight.departureDate}
                  </p>
                  <p className="text-sm text-gray-600">
                    <strong>Arrival:</strong> {flight.arrivalDate}
                  </p>
                  <p className="text-sm text-green-600 font-semibold">
                    Economy: ₹{flight?.fareRequest?.economyFare || "N/A"}
                  </p>
                  <p className="text-sm text-blue-600 font-semibold">
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
    </>
  );
};

export default SearchFlights;
