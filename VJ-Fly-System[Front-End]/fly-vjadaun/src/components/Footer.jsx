import React from "react";

const Footer = () => {
  return (
    <footer className="w-full bg-gray-900 text-gray-300">
      <div className="max-w-screen-xl mx-auto px-4 py-4 flex flex-col md:flex-row justify-between items-center text-sm">
        <div className="text-center md:text-left mb-2 md:mb-0">
          <h2 className="text-white font-semibold text-base">Fly-VJadaun Admin</h2>
          <p className="text-gray-400">© {new Date().getFullYear()} All rights reserved.</p>
        </div>
        <div className="flex flex-wrap gap-4 justify-center md:justify-end">
          <a href="/admin" className="hover:text-white transition">Dashboard</a>
          <a href="/admin/users" className="hover:text-white transition">Users</a>
          <a href="/admin/flights" className="hover:text-white transition">Flights</a>
          <a href="/admin/bookings" className="hover:text-white transition">Bookings</a>
          <a href="/admin/payments" className="hover:text-white transition">Payments</a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
