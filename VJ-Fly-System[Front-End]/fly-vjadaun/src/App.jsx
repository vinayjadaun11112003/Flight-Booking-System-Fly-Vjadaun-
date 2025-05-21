import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from "./pages/Login";
import Register from "./pages/Register";
import Home from "./pages/Home";
import AdminPanel from "./pages/AdminPanel";
import ProtectedRoute from "./components/ProtectedRoute";
// Navigation Pages
import Booking from "./pages/navs/Booking";
import Search from "./pages/navs/Search";
import Flights from "./pages/navs/Flights";
import Payment from "./pages/navs/Payment";
import AdminBooking from './pages/adminNav/AdminBookings';
import AdminPayment from './pages/adminNav/AdminPayments';
import AdminFlights from './pages/adminNav/AdminFlights';
import AdminUsers from './pages/adminNav/AdminUsers';
import BookingForm from './pages/navs/BookingForm';



function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* Protected Home for USER and ADMIN */}
        <Route
          path="/home"
          element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          }
        />


        <Route
        path="/flights"
        element={
          <ProtectedRoute>
            <Flights />
          </ProtectedRoute>
        }
      />
      <Route
        path="/search"
        element={
          <ProtectedRoute>
            <Search />
          </ProtectedRoute>
        }
      />
      <Route
        path="/booking"
        element={
          <ProtectedRoute>
            <Booking />
          </ProtectedRoute>
        }
      />
      <Route
        path="/payment"
        element={
          <ProtectedRoute>
            <Payment />
          </ProtectedRoute>
        }
      />
       <Route
        path="/search/bookingForm/:id"
        element={
          <ProtectedRoute>
            <BookingForm />
          </ProtectedRoute>
        }
      />

        {/* Protected Admin Panel ONLY for ADMIN */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminPanel />
            </ProtectedRoute>
          }
        />
       
       <Route
        path="admin/bookings"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminBooking/>
          </ProtectedRoute>
        }
      />

        <Route
        path="admin/payments"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminPayment/>
          </ProtectedRoute>
        }
      />

        <Route
        path="admin/flights"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminFlights/>
          </ProtectedRoute>
        }
      />

        <Route
        path="admin/users"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminUsers/>
          </ProtectedRoute>
        }
      />
      </Routes>
    </Router>
  );
}

export default App;

