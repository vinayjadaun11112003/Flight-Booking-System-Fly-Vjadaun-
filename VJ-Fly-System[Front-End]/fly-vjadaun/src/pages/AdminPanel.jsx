import React, { useEffect, useState } from "react";
import axios from "axios";
import AdminHeader from "../components/AdminHeader";
import Footer from "../components/Footer";

const AdminDashboard = () => {
  const [userCount, setUserCount] = useState(0);
  const [flightCount, setFlightCount] = useState(0);
  const [bookingCount, setBookingCount] = useState(0);
  const [paymentCount, setPaymentCount] = useState(0);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem("token"); // if using JWT stored in localStorage

        const headers = {
          Authorization: `Bearer ${token}`,
        };

        const [users, flights, bookings, payments] = await Promise.all([
          axios.get("http://localhost:8080/admin1/getAllUsers", { headers }),
          axios.get("http://localhost:8080/admin2/getAllFlights", { headers }),
          axios.get("http://localhost:8080/admin3/getAllBookings", { headers }),
          axios.get("http://localhost:8080/admin4/getAllPayments", { headers }),
        ]);

        setUserCount(users.data.length);
        setFlightCount(flights.data.length);
        setBookingCount(bookings.data.length);
        setPaymentCount(payments.data.length);
      } catch (error) {
        console.error("Error fetching dashboard data", error);
      }
    };

    fetchData();
  }, []);

  return (
    <>
      <AdminHeader />
      <div
        className="min-h-screen bg-cover bg-center bg-fixed"
        style={{ backgroundImage: "url('/admin.jpg')" }}
      >
        <div className="bg-opacity-50 min-h-screen py-10 px-6">
          {/* Header */}
          <div className="text-center mb-4">
            <h1 className="text-4xl font-bold text-white">Fly-VJadaun Admin Dashboard</h1>
            <p className="text-gray-300 mt-2">Welcome, ADMIN! This is your control center.</p>
          </div>

          {/* Developer Info */}
          <div className="text-center mb-10">
            <p className="text-sm text-gray-300">
              <span className="font-medium">Developed by:</span>{" "}
              <span className="text-blue-400 font-semibold">Vinay Jadaun</span>
            </p>
          </div>

          {/* Count Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <div className="bg-white shadow-xl rounded-2xl p-6 border-t-4 border-blue-500 h-48 flex flex-col justify-between hover:scale-105 transform transition duration-300">
              <div>
                <h2 className="text-lg font-semibold text-gray-700">Total Users</h2>
              </div>
              <p className="text-5xl font-bold text-blue-700">{userCount}</p>
            </div>

            <div className="bg-white shadow-xl rounded-2xl p-6 border-t-4 border-green-500 h-48 flex flex-col justify-between hover:scale-105 transform transition duration-300">
              <div>
                <h2 className="text-lg font-semibold text-gray-700">Total Flights</h2>
              </div>
              <p className="text-5xl font-bold text-green-600">{flightCount}</p>
            </div>

            <div className="bg-white shadow-xl rounded-2xl p-6 border-t-4 border-yellow-500 h-48 flex flex-col justify-between hover:scale-105 transform transition duration-300">
              <div>
                <h2 className="text-lg font-semibold text-gray-700">Total Bookings</h2>
              </div>
              <p className="text-5xl font-bold text-yellow-600">{bookingCount}</p>
            </div>

            <div className="bg-white shadow-xl rounded-2xl p-6 border-t-4 border-red-500 h-48 flex flex-col justify-between hover:scale-105 transform transition duration-300">
              <div>
                <h2 className="text-lg font-semibold text-gray-700">Total Payments</h2>
              </div>
              <p className="text-5xl font-bold text-red-600">{paymentCount}</p>
            </div>
          </div>

          {/* Developer Info Section */}
          <div className="max-w-5xl mx-auto mt-10 p-6 rounded-2xl bg-white shadow-md flex flex-col sm:flex-row items-center gap-6 border-l-4 border-blue-500">
            {/* Optional Profile Pic */}
            {/* <img src="your-profile-url.jpg" alt="Vinay Jadaun" className="w-24 h-24 rounded-full shadow-md border-2 border-blue-600" /> */}

            <div className="text-center sm:text-left">
              <h2 className="text-2xl font-bold text-blue-800">Vinay Jadaun</h2>
              <p className="text-sm text-gray-700 font-medium mb-1">MERN Stack Developer | Full-stack Freelancer</p>
              <p className="text-gray-600 text-sm leading-relaxed">
                Based in Bhopal, India — building scalable web solutions with React, Node, MongoDB, and Express.
                Known for projects like <span className="text-indigo-600 font-semibold">Vride</span> and <span className="text-green-600 font-semibold">DrDhaivatShah.com</span>.
                Passionate about clean UI, real-time apps, and problem-solving.
              </p>

              <div className="mt-3 flex flex-wrap items-center gap-4 text-sm">
                <a href="https://vinayjadaun-com.vercel.app" target="_blank" className="text-blue-600 hover:underline">
                  🌐 Portfolio
                </a>
                <a href="mailto:vinayjadaun11112003@gmail.com" className="text-blue-600 hover:underline">
                  📧 Email
                </a>
                <a href="tel:+918962671738" className="text-blue-600 hover:underline">
                  📞 +91 89626 71738
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default AdminDashboard;
