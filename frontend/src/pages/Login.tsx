import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi } from '../services/api';
import { Rocket, User, Mail, Lock, UserCircle } from 'lucide-react';

interface LoginProps {
  setUser: (user: any) => void;
}

export default function Login({ setUser }: LoginProps) {
  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState('');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      if (isRegister) {
        // For registration, send fullName along with username and email
        const user = await userApi.register(username, email, fullName);
        // Store user in localStorage and state
        localStorage.setItem('user', JSON.stringify(user));
        setUser(user);
        navigate('/');
      } else {
        // For login, check if user exists
        const user = await userApi.checkUser(username);
        // Store user in localStorage and state
        localStorage.setItem('user', JSON.stringify(user));
        setUser(user);
        navigate('/');
      }
    } catch (err: any) {
      if (err.response?.status === 404) {
        setError('User not found. Please register first.');
      } else if (err.response?.data) {
        setError(err.response.data);
      } else if (err.code === 'ERR_NETWORK') {
        setError('Cannot connect to server. Please make sure the backend is running.');
      } else {
        setError('An error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-600 to-secondary-600 flex items-center justify-center px-4">
      <div className="max-w-md w-full">
        {/* Logo and Title */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-white rounded-full mb-4 shadow-lg">
            <Rocket className="h-10 w-10 text-primary-600" />
          </div>
          <h1 className="text-3xl font-bold text-white">Shodh-a-Code</h1>
          <p className="text-white/80 mt-2">Real-time Coding Contest Platform</p>
        </div>

        {/* Login/Register Form */}
        <div className="bg-white rounded-2xl shadow-xl p-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6 text-center">
            {isRegister ? 'Create Account' : 'Welcome Back'}
          </h2>
          
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Full Name Field - Only for Registration */}
            {isRegister && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  <UserCircle className="inline h-4 w-4 mr-1" />
                  Full Name
                </label>
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
                  placeholder="Enter your full name"
                  required={isRegister}
                />
              </div>
            )}

            {/* Username Field */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                <User className="inline h-4 w-4 mr-1" />
                Username
              </label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
                placeholder="Enter your username"
                required
              />
            </div>
            
            {/* Email Field - Only for Registration */}
            {isRegister && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  <Mail className="inline h-4 w-4 mr-1" />
                  Email
                </label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-primary-500 focus:ring-4 focus:ring-primary-100 transition-all"
                  placeholder="Enter your email"
                  required={isRegister}
                />
              </div>
            )}
            
            {/* Error Message */}
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-2 rounded-lg text-sm">
                {error}
              </div>
            )}
            
            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 px-4 bg-gradient-to-r from-primary-600 to-secondary-600 text-white font-semibold rounded-xl hover:shadow-lg transform transition-all duration-200 hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Processing...' : isRegister ? 'Create Account' : 'Login'}
            </button>
          </form>
          
          {/* Toggle Login/Register */}
          <div className="mt-6 text-center">
            <span className="text-gray-600">
              {isRegister ? 'Already have an account?' : "Don't have an account?"}
            </span>
            <button
              onClick={() => {
                setIsRegister(!isRegister);
                setError('');
                // Reset form fields
                setUsername('');
                setEmail('');
                setFullName('');
              }}
              className="ml-2 text-primary-600 font-semibold hover:text-primary-700 hover:underline"
            >
              {isRegister ? 'Login' : 'Register'}
            </button>
          </div>
        </div>

        {/* Demo Credentials Info */}
        {!isRegister && (
          <div className="mt-4 text-center text-white/80 text-sm">
            <p>Demo: Use any username to login</p>
          </div>
        )}
      </div>
    </div>
  );
}