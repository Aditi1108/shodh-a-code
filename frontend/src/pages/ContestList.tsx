import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Clock, ArrowRight } from 'lucide-react';
import { contestApi } from '../services/api';
import type { Contest } from '../types';

export default function ContestList() {
  const [contests, setContests] = useState<Contest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  useEffect(() => {
    loadContests();
  }, []);
  
  const loadContests = async () => {
    try {
      const data = await contestApi.getAll();
      setContests(data);
    } catch (err: any) {
      console.error('Error loading contests:', err);
      if (err.code === 'ERR_NETWORK') {
        setError('Cannot connect to server. Please make sure the backend is running on http://localhost:8080');
      } else if (err.response?.status === 404) {
        setError('Contests endpoint not found');
      } else {
        setError(`Failed to load contests: ${err.message}`);
      }
    } finally {
      setLoading(false);
    }
  };
  
  const formatDateTime = (dateStr: string) => {
    return new Date(dateStr).toLocaleString();
  };
  
  const getContestStatus = (contest: Contest) => {
    const now = new Date();
    const start = new Date(contest.startTime);
    const end = new Date(contest.endTime);
    
    if (now < start) return { 
      text: 'Upcoming', 
      bgColor: 'bg-gradient-to-r from-blue-400 to-cyan-500 text-white'
    };
    if (now > end) return { 
      text: 'Ended', 
      bgColor: 'bg-gradient-to-r from-gray-400 to-gray-500 text-white'
    };
    return { 
      text: 'Active', 
      bgColor: 'bg-gradient-to-r from-green-400 to-emerald-500 text-white animate-pulse'
    };
  };
  
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-600 dark:text-gray-300">Loading contests...</div>
      </div>
    );
  }
  
  if (error) {
    return (
      <div className="container mx-auto px-6 py-12">
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-6">
          <h3 className="text-red-800 dark:text-red-400 font-semibold mb-2">Error Loading Contests</h3>
          <p className="text-red-600 dark:text-red-300">{error}</p>
          <button 
            onClick={() => window.location.reload()}
            className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }
  
  return (
    <div className="container mx-auto px-6 py-12">
      <div className="mb-12 text-center">
        <h1 className="text-5xl font-bold mb-4 bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
          Programming Contests
        </h1>
        <p className="text-xl text-gray-600 dark:text-gray-300">Compete, Learn, and Win Amazing Prizes!</p>
      </div>
      
      <div className="grid gap-6">
        {contests.map((contest) => {
          const status = getContestStatus(contest);
          return (
            <Link
              key={contest.id}
              to={`/contest/${contest.id}`}
              className="group bg-white dark:bg-gray-800 p-6 rounded-2xl shadow-lg hover:shadow-2xl transition-all border-2 border-transparent hover:border-purple-400 dark:hover:border-purple-500 transform hover:-translate-y-1"
            >
              <div className="flex justify-between items-start mb-6">
                <div className="flex-1">
                  <h2 className="text-2xl font-semibold text-gray-900 dark:text-white mb-2">
                    {contest.title}
                  </h2>
                  <p className="text-gray-600 dark:text-gray-400">
                    {contest.description}
                  </p>
                </div>
                <div className="ml-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${status.bgColor}`}>
                    {status.text}
                  </span>
                </div>
              </div>
              
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-6 text-sm text-gray-500 dark:text-gray-400">
                  <div className="flex items-center space-x-2">
                    <Calendar className="h-4 w-4" />
                    <span>{formatDateTime(contest.startTime)}</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Clock className="h-4 w-4" />
                    <span>{formatDateTime(contest.endTime)}</span>
                  </div>
                </div>
                <div className="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg font-medium group-hover:from-purple-600 group-hover:to-pink-600 transition-all">
                  View Contest
                  <ArrowRight className="inline ml-2 h-4 w-4 group-hover:translate-x-1 transition-transform" />
                </div>
              </div>
            </Link>
          );
        })}
        
        {contests.length === 0 && (
          <div className="text-center py-12 text-gray-600 dark:text-gray-300">
            No contests available at the moment.
          </div>
        )}
      </div>
    </div>
  );
}