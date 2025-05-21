import { useEffect, useState } from 'react';
import axios from 'axios';
import Header from '../../components/Header'; // your normal user header
import Footer from '../../components/Footer';

const UserPayments = () => {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // ✅ Parse user and get ID from localStorage
  const user = JSON.parse(localStorage.getItem('user'));
  const userId = user.id;

  useEffect(() => {
    const fetchPayments = async () => {
      const token = localStorage.getItem('token');
      try {
        const res = await axios.get(`http://localhost:8080/BookingService/getPayments/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPayments(res.data);
      } catch (err) {
        setError('Failed to fetch payments');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    if (userId) fetchPayments();
  }, [userId]);

  return (
    <>
   <Header />
<div
  className="min-h-screen p-6 bg-cover bg-center bg-no-repeat"
  style={{ backgroundImage: "url('/search.jpg')" }}
>
  <div className=" bg-opacity-80 rounded-xl p-4 shadow-lg">
    <h1 className="text-4xl font-bold text-center text-white mb-8">💳 Your Payments</h1>

    {loading ? (
      <p className="text-center text-gray-600">Loading payments...</p>
    ) : error ? (
      <p className="text-center text-red-600">{error}</p>
    ) : payments.length === 0 ? (
      <p className="text-center text-gray-600">No payments found.</p>
    ) : (
      <div className="max-w-4xl mx-auto space-y-6">
        {payments.map((payment, index) => (
          <div
            key={index}
            className="bg-white rounded-xl shadow-lg p-6 border border-gray-200 hover:shadow-xl transition"
          >
            <h3 className="text-xl font-semibold text-black mb-2 break-all">
              Payment ID : <div className="text-blue-900">{payment.sessionId}</div>
            </h3>
            <p><strong>Amount : </strong> ₹{payment.amount}</p>
            <p><strong>Currency : </strong> {payment.currency}</p>
            <p><strong>Status : </strong> {payment.status}</p>
            <p><strong>Paid On : </strong> {new Date(payment.createdAt).toLocaleString()}</p>
          </div>
        ))}
      </div>
    )}
  </div>
</div>
<Footer />

    </>
  );
};

export default UserPayments;
