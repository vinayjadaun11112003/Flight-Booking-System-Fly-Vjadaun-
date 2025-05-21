import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";

const AdminHeader = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false); // State to toggle the menu visibility
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  const navItems = [
    { path: "/admin", label: "Dashboard" },
    { path: "/admin/users", label: "Users" },
    { path: "/admin/flights", label: "Flights" },
    { path: "/admin/bookings", label: "Booking" },
    { path: "/admin/payments", label: "Payment" },
  ];

  return (
    <header className="bg-gradient-to-r from-indigo-500 to-blue-500 text-white px-6 py-4 flex justify-between items-center shadow-lg flex-wrap">
      {/* Logo */}
      <h1 className="text-3xl font-bold text-white">Fly-VJadaun Admin</h1>

      {/* Hamburger Icon for Mobile */}
      <button
        className="block md:hidden text-3xl"
        onClick={() => setIsMenuOpen(!isMenuOpen)}
      >
        &#9776; {/* Hamburger icon */}
      </button>

      {/* Navigation Menu */}
      <nav
        className={`${
          isMenuOpen ? "flex" : "hidden"
        } flex-wrap md:flex md:justify-between md:items-center gap-6 mt-4 md:mt-0`}
      >
        {navItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={`text-lg hover:text-blue-300 ${
              location.pathname === item.path
                ? "text-blue-900 font-semibold"
                : ""
            } transition-colors duration-300`}
          >
            {item.label}
          </Link>
        ))}
        <button
          onClick={handleLogout}
          className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-full transition-colors duration-300 mt-4 md:mt-0"
        >
          Logout
        </button>
      </nav>
    </header>
  );
};

export default AdminHeader;
