import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "../components/userContext";

export default function Login() {
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState(""); // Error message state
  const navigate = useNavigate();
  const { setUser } = useUser();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setError(""); // Clear error on input change
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const res = await fetch("http://localhost:8080/auth/token", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (res.ok) {
        const data = await res.json();
        localStorage.setItem("token", data.token);

        const userRes = await fetch(
          `http://localhost:8080/admin1/getUserByName/${form.username}`,
          {
            headers: { Authorization: `Bearer ${data.token}` },
          }
        );

        if (userRes.ok) {
          const userData = await userRes.json();
          localStorage.setItem("user", JSON.stringify(userData));
          setUser(userData);
          navigate("/home");
        } else {
          setError("❌ Failed to fetch user info.");
        }
      } else {
        const errorData = await res.json();
        setError(`❌ Login failed: ${errorData.message || "Invalid credentials"}`);
      }
    } catch (error) {
      console.error("Login error:", error);
      setError("❌ Server error. Please try again.");
    }
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: "url('/flight.jpg')" }}
    >
      <div className="backdrop-blur-md rounded-3xl shadow-2xl p-10 w-[90%] max-w-md border border-white/20">
        <h2 className="text-3xl font-bold text-white text-center mb-6">
          Login to Your Account
        </h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            name="username"
            value={form.username}
            onChange={handleChange}
            placeholder="Username"
            required
            className="w-full px-4 py-2 rounded-xl bg-white/30 text-white placeholder-white/70 focus:outline-none focus:ring-2 focus:ring-blue-300"
          />
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            placeholder="Password"
            required
            className="w-full px-4 py-2 rounded-xl bg-white/30 text-white placeholder-white/70 focus:outline-none focus:ring-2 focus:ring-blue-300"
          />
          <button
            type="submit"
            className="w-full py-2 rounded-xl bg-gradient-to-r from-green-400 to-blue-500 hover:from-green-500 hover:to-blue-600 text-white font-semibold transition"
          >
            Login
          </button>
        </form>

        {error && (
          <p className="text-red-300 text-center font-semibold mt-4">
            {error}
          </p>
        )}

        <p className="text-center mt-6 text-white/80">
          Don&apos;t have an account?{" "}
          <span
            onClick={() => navigate("/register")}
            className="text-blue-300 hover:underline cursor-pointer"
          >
            Register
          </span>
        </p>
      </div>
    </div>
  );
}
