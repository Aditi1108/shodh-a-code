import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Layout from './components/Layout';
import Home from './pages/Home';
import Login from './pages/Login';
import Contest from './pages/Contest';
import ContestList from './pages/ContestList';
import Problem from './pages/Problem';
import ProblemList from './pages/ProblemList';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in (from localStorage)
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  // Protected Route Component
  const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
    if (loading) {
      return (
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-xl text-gray-600">Loading...</div>
        </div>
      );
    }
    
    if (!user) {
      return <Navigate to="/login" replace />;
    }
    
    return <>{children}</>;
  };

  return (
    <Router>
      <Routes>
        {/* Public Route - Login */}
        <Route path="/login" element={
          user ? <Navigate to="/" replace /> : <Login setUser={setUser} />
        } />
        
        {/* Protected Routes */}
        <Route path="/" element={
          <ProtectedRoute>
            <Layout user={user} setUser={setUser} />
          </ProtectedRoute>
        }>
          <Route index element={<Home user={user} />} />
          <Route path="contests" element={<ContestList />} />
          <Route path="contest/:id" element={<Contest user={user} />} />
          <Route path="problems" element={<ProblemList />} />
          <Route path="problem/:id" element={<Problem user={user} />} />
        </Route>
        
        {/* Catch all - redirect to login */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;