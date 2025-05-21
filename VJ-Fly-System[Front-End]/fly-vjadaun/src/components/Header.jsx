import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  const navItems = [
    { path: "/home", label: "Home" },
    { path: "/flights", label: "Flights" },
    { path: "/search", label: "Search-Flight" },
    { path: "/booking", label: "Bookings" },
    { path: "/payment", label: "Payments" },
  ];

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/");
  };

  return (
    <header className="bg-blue-600 text-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto flex justify-between items-center p-4">
        {/* Logo */}
        <div className="text-2xl font-bold tracking-wide flex items-center gap-2">
          ✈️ <span className="hidden sm:inline">FLY-VJADAUN</span>
        </div>

        {/* Hamburger icon for mobile */}
        <div className="sm:hidden">
          <button onClick={() => setMenuOpen(!menuOpen)}>
            <svg
              className="w-6 h-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              {menuOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>

        {/* Desktop Nav */}
        <nav className="hidden sm:flex space-x-6 items-center">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`hover:text-yellow-300 transition ${
                location.pathname === item.path ? "text-yellow-300 font-semibold" : ""
              }`}
            >
              {item.label}
            </Link>
          ))}
          <button
            onClick={handleLogout}
            className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md shadow text-sm font-semibold"
          >
            Logout
          </button>
        </nav>
      </div>

      {/* Mobile Menu */}
      {menuOpen && (
        <div className="sm:hidden bg-blue-500 px-4 pb-4">
          <nav className="flex flex-col space-y-3">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                onClick={() => setMenuOpen(false)}
                className={`block py-2 border-b border-blue-300 hover:text-yellow-200 ${
                  location.pathname === item.path ? "text-yellow-200 font-semibold" : ""
                }`}
              >
                {item.label}
              </Link>
            ))}
            <button
              onClick={handleLogout}
              className="mt-3 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md shadow text-sm"
            >
              Logout
            </button>
          </nav>
        </div>
      )}
    </header>
  );
};

export default Header;
