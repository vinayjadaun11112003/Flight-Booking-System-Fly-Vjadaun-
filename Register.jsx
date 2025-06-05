import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";

const Register = () => {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setMsg("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMsg("");

    const { password, confirmPassword } = form;

    const passwordRegex = /^(?=(?:.*\d){3,})(?=.*[A-Z])(?=.*[\W_]).{8,}$/;

    if (!passwordRegex.test(password)) {
      setMsg("❌ Password must be at least 8 characters, include 1 uppercase, 1 special char, and 3 digits.");
      setLoading(false);
      return;
    }

    if (password !== confirmPassword) {
      setMsg("❌ Password and Confirm Password do not match.");
      setLoading(false);
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: form.name,
          email: form.email,
          password: form.password,
          role: "USER",
        }),
      });

      const data = await res.json();

      if (res.ok) {
        setMsg("✅ Registered Successfully!");
        setForm({ name: "", email: "", password: "", confirmPassword: "" });
        setTimeout(() => navigate("/"), 1500);
      } else {
        setMsg(data.message || "❌ Registration failed.");
      }
    } catch (error) {
      setMsg("❌ Server error!");
      console.error(error);
    }

    setLoading(false);
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: "url('/Home.jpg')" }}
    >
      <div className="backdrop-blur-md rounded-3xl shadow-2xl p-10 w-[90%] max-w-md border border-white/20">
        <h2 className="text-3xl font-bold text-gray-400 text-center mb-6">
          Register
        </h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            name="name"
            placeholder="Username"
            value={form.name}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 rounded-xl bg-white/30 text-black placeholder-black/70 focus:outline-none focus:ring-2 focus:ring-blue-300"
          />
          <input
            type="email"
            name="email"
            placeholder="Email"
            value={form.email}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 rounded-xl bg-white/30 text-black placeholder-black/70 focus:outline-none focus:ring-2 focus:ring-blue-300"
          />

          {/* Password input with eye toggle */}
          <div className="relative">
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              placeholder="Password"
              value={form.password}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 pr-12 rounded-xl bg-white/30 text-black placeholder-black/70 focus:outline-none focus:ring-2 focus:ring-blue-300"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-2.5 text-black"
            >
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>

          {/* Confirm Password input with eye toggle */}
          <div className="relative">
            <input
              type={showConfirmPassword ? "text" : "password"}
              name="confirmPassword"
              placeholder="Confirm Password"
              value={form.confirmPassword}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 pr-12 rounded-xl bg-white/30 text-black placeholder-black/70 focus:outline-none focus:ring-2 focus:ring-blue-300"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-3 top-2.5 text-black"
            >
              {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-2 rounded-xl bg-gradient-to-r from-purple-500 to-indigo-500 hover:from-purple-600 hover:to-indigo-600 text-white font-semibold transition"
          >
            {loading ? "Registering..." : "Register"}
          </button>
        </form>

        <p className="text-center mt-6 text-white/80">
          Already have an account?{" "}
          <span
            onClick={() => navigate("/")}
            className="text-blue-300 hover:underline cursor-pointer"
          >
            Login
          </span>
        </p>

        {msg && (
          <p
            className={`text-center mt-4 font-semibold ${
              msg.startsWith("✅") ? "text-green-300" : "text-red-300"
            }`}
          >
            {msg}
          </p>
        )}
      </div>
    </div>
  );
};

export default Register;
