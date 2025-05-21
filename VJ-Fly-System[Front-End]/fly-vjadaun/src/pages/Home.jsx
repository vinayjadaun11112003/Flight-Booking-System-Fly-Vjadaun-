import React from "react";
import { Link } from "react-router-dom";
import { FaPlaneDeparture, FaMapMarkerAlt } from "react-icons/fa";
import Header from "../components/Header";

const hotDestinations = [
  { city: "Goa", image: "/goa.jpg" },
  { city: "Delhi", image: "/delhi.jpg" },
  { city: "Mumbai", image: "/mumbai.jpg" },
  { city: "Bangalore", image: "/banglore.jpg" },
  { city: "Dubai", image: "/dubai.jpg" },
  { city:"America", image: "/america.jpg"},
  { city: "Chine", image: "/china.jpg" },
  { city:"Russia", image: "/russia.jpg"},
  { city: "London", image: "/london.jpg" },
  { city:"France", image: "/france.jpg"}
];

const Home = () => {
  return (
    <>
    <Header/>

    <div
      className="min-h-screen bg-cover bg-center"
      style={{ backgroundImage: "url('/Home.jpg')" }}
    >
      <div className="bg-black/60 min-h-screen w-full text-white flex flex-col">

        {/* Hero Section */}
        <section className="flex flex-col justify-center items-center text-center px-6 pt-24 md:pt-36">
          <div className="max-w-4xl w-full bg-white/10 backdrop-blur-xl rounded-3xl shadow-xl p-10 md:p-16 text-white">
            <h1 className="text-4xl md:text-5xl font-bold leading-tight mb-4">
              Explore The Skies with <span className="text-blue-400">VJ-FLY</span>
            </h1>
            <p className="text-lg md:text-xl text-gray-200 mb-6">
              Seamless booking. Stunning destinations. Start your journey today.
            </p>

            <Link
              to="/search"
              className="inline-flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 px-6 py-3 rounded-lg text-lg font-semibold transition"
            >
              <FaPlaneDeparture />
              Search Flights
            </Link>
          </div>

          {/* Hot Destinations */}
          <div className="mt-12 w-full max-w-6xl px-4">
            <h2 className="text-2xl md:text-3xl font-semibold mb-6 text-white text-center">
              🔥 Hot Destinations
            </h2>
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 m-5 gap-4">
              {hotDestinations.map((dest) => (
                <div
                  key={dest.city}
                  className="relative rounded-xl overflow-hidden shadow-lg hover:scale-105 transform transition"
                >
                  <img
                    src={dest.image}
                    alt={dest.city}
                    className="w-full h-32 md:h-40 object-cover brightness-90"
                  />
                  <div className="absolute inset-0 bg-black/30 flex items-end p-3">
                    <h3 className="text-white text-lg font-semibold flex items-center gap-2">
                      <FaMapMarkerAlt /> {dest.city}
                    </h3>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Portfolio Section */}
        <section className="bg-white text-gray-800 py-16 px-6 md:px-20 text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-4">👋 Meet Vinay Jadaun</h2>
          <p className="text-lg max-w-2xl mx-auto mb-6">
            I'm a passionate freelance web developer from Bhopal, specializing in building modern, secure, and scalable apps. From microservices to mobile — I do it all.
          </p>

          <div className="grid gap-6 md:grid-cols-3 max-w-6xl mx-auto my-10">
            <div className="bg-blue-50 p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-2">💻 Frontend Development</h3>
              <p>React, React Native, responsive UI, modern UX, cross-platform apps</p>
            </div>
            <div className="bg-blue-50 p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-2">⚙️ Backend & Microservices</h3>
              <p>Spring Boot, Spring Cloud, REST APIs, JWT Auth, Microservices Architecture</p>
            </div>
            <div className="bg-blue-50 p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-2">📡 Messaging & Cloud Native</h3>
              <p>RabbitMQ integration, API Gateway, service discovery, secure & scalable systems</p>
            </div>
          </div>

          <a
            href="https://vinayjadaun-com.vercel.app/"
            target="_blank"
            rel="noopener noreferrer"
            className="mt-6 inline-block bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg text-lg font-semibold transition duration-200"
          >
            Hire Me
          </a>
        </section>

        {/* Footer */}
        <footer className="bg-gray-900 text-white text-sm text-center py-4">
          © 2025 Vinay Jadaun | Built with ❤️ in Bhopal
        </footer>
      </div>
    </div>    </>
  );
};

export default Home;
