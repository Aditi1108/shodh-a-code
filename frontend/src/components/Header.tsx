import { Link, useLocation } from 'react-router-dom';
import { Moon, Sun, User, Trophy, FileCode, Sparkles } from 'lucide-react';
import { useStore } from '../store/useStore';

export function Header() {
  const { user, isDarkMode, toggleDarkMode } = useStore();
  const location = useLocation();
  
  const isActive = (path: string) => {
    return location.pathname === path;
  };
  
  return (
    <header className="bg-gradient-to-r from-violet-600 via-purple-600 to-indigo-600 text-white shadow-xl">
      <div className="container mx-auto px-6">
        <div className="flex items-center justify-between h-20">
          <Link to="/" className="flex items-center space-x-3">
            <div className="flex items-center">
              <div className="w-12 h-12 bg-white rounded-xl flex items-center justify-center shadow-lg transform rotate-3 hover:rotate-6 transition-transform">
                <Sparkles className="h-7 w-7 text-purple-600" />
              </div>
              <div className="ml-3">
                <span className="text-2xl font-bold">
                  Shodh-a-Code
                </span>
                <div className="text-xs text-purple-200">Master Competitive Programming</div>
              </div>
            </div>
          </Link>
          
          <nav className="flex items-center space-x-6">
            <Link 
              to="/contests" 
              className={`flex items-center space-x-2 px-5 py-2.5 rounded-lg transition-all ${
                isActive('/contests')
                  ? 'bg-white/20 backdrop-blur-sm text-white shadow-lg'
                  : 'text-white/90 hover:bg-white/10'
              }`}
            >
              <Trophy className="h-4 w-4" />
              <span className="font-medium">Contests</span>
            </Link>
            <button
              onClick={toggleDarkMode}
              className="p-2.5 rounded-lg bg-white/10 hover:bg-white/20 transition-all backdrop-blur-sm"
            >
              {isDarkMode ? (
                <Sun className="h-5 w-5 text-yellow-300" />
              ) : (
                <Moon className="h-5 w-5 text-white" />
              )}
            </button>
            
            {user ? (
              <div className="flex items-center space-x-2 px-4 py-2 bg-white/10 backdrop-blur-sm rounded-lg">
                <div className="bg-gradient-to-br from-yellow-400 to-orange-500 p-1.5 rounded-full">
                  <User className="h-4 w-4 text-white" />
                </div>
                <span className="font-medium">
                  {user.username}
                </span>
              </div>
            ) : (
              <div className="flex items-center space-x-3">
                <Link 
                  to="/login" 
                  className="text-white/90 hover:text-white transition-colors font-medium"
                >
                  Login
                </Link>
                <Link 
                  to="/login" 
                  className="px-6 py-2.5 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-lg font-semibold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all"
                >
                  Get Started
                </Link>
              </div>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
}