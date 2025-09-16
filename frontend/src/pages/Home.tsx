import { Link, useNavigate } from 'react-router-dom';
import { Trophy, Code, Clock, Calendar } from 'lucide-react';
import { useEffect, useState } from 'react';
import { contestApi } from '../services/api';
import { useStore } from '../store/useStore';

const Home = ({ user }: { user: any }) => {
  const [activeContestsList, setActiveContestsList] = useState<any[]>([]);
  const [upcomingContests, setUpcomingContests] = useState<any[]>([]);
  const [joiningContest, setJoiningContest] = useState<number | null>(null);
  const navigate = useNavigate();
  const { joinContest, isContestJoined } = useStore();
  
  useEffect(() => {
    loadDashboardData();
  }, [user]);
  
  const loadDashboardData = async () => {
    try {
      // Load contests
      const contests = await contestApi.getAll();
      const now = new Date();
      
      // Get active contests
      const active = contests.filter(c => {
        const start = new Date(c.startTime);
        const end = new Date(c.endTime);
        return now >= start && now <= end;
      });
      setActiveContestsList(active);
      
      // Get upcoming contests
      const upcoming = contests
        .filter(c => new Date(c.startTime) > now)
        .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime())
        .slice(0, 2);
      setUpcomingContests(upcoming);
      
    } catch (err) {
      console.error('Error loading dashboard data:', err);
    }
  };

  const handleJoinContest = async (contestId: number) => {
    if (!user) {
      alert('Please login to join the contest');
      return;
    }

    setJoiningContest(contestId);
    
    try {
      await contestApi.joinContest(user.username, contestId);
      joinContest(contestId);
      navigate(`/contest/${contestId}`);
    } catch (err) {
      console.error('Failed to join contest:', err);
      alert('Failed to join contest. Please try again.');
    } finally {
      setJoiningContest(null);
    }
  };
  
  const stats = [
    { icon: Trophy, label: 'Total Score', value: user?.score || 0, color: 'text-yellow-500 bg-yellow-100' },
    { icon: Code, label: 'Problems Solved', value: user?.problemsSolved || 0, color: 'text-green-500 bg-green-100' },
    { icon: Clock, label: 'Active Contests', value: activeContestsList.length, color: 'text-blue-500 bg-blue-100' },
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-primary-600 to-secondary-600 rounded-3xl p-8 text-white">
        <h1 className="text-3xl font-bold mb-2">Welcome back, {user?.username || 'Coder'}! 👋</h1>
        <p className="text-lg opacity-90">Ready to solve some coding challenges?</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {stats.map((stat, index) => (
          <div key={index} className="card p-6 hover:scale-105 transition-transform bg-white rounded-2xl shadow-lg">
            <div className={`inline-flex p-3 rounded-xl ${stat.color} mb-4`}>
              <stat.icon className="h-6 w-6" />
            </div>
            <div className="text-2xl font-bold text-gray-800">{stat.value}</div>
            <div className="text-sm text-gray-600">{stat.label}</div>
          </div>
        ))}
      </div>

      {/* Active Contests */}
      {activeContestsList.length > 0 && (
        <div className="card p-6 bg-white rounded-2xl shadow-lg">
          <h2 className="text-xl font-bold text-gray-800 mb-4">🔥 Active Contests</h2>
          <div className="space-y-3">
            {activeContestsList.map((contest) => (
              <div key={contest.id} className="p-4 bg-gradient-to-r from-green-50 to-blue-50 rounded-lg border border-green-200">
                <div className="flex justify-between items-center">
                  <div>
                    <div className="font-semibold text-gray-800">{contest.title}</div>
                    <div className="text-sm text-gray-600">
                      Ends: {new Date(contest.endTime).toLocaleString()}
                    </div>
                    <div className="text-sm text-gray-500">
                      {contest.problems?.length || 0} problems available
                    </div>
                  </div>
                  {isContestJoined(contest.id) ? (
                    <Link 
                      to={`/contest/${contest.id}`}
                      className="px-4 py-2 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-all"
                    >
                      View Contest
                    </Link>
                  ) : (
                    <button
                      onClick={() => handleJoinContest(contest.id)}
                      disabled={joiningContest === contest.id || !user}
                      className="px-4 py-2 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {joiningContest === contest.id ? 'Joining...' : 'Join Now'}
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Upcoming Contests */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="card p-6 bg-white rounded-2xl shadow-lg">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Upcoming Contests</h2>
          <div className="space-y-3">
            {upcomingContests.length > 0 ? (
              upcomingContests.map((contest, index) => (
                <Link 
                  key={contest.id} 
                  to={`/contest/${contest.id}`} 
                  className="block p-3 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg hover:shadow-md transition-all"
                >
                  <div className="font-medium text-gray-800">{contest.title}</div>
                  <div className="text-sm text-gray-600">
                    Starts: {new Date(contest.startTime).toLocaleString()}
                  </div>
                  <div className="text-sm text-gray-500">
                    {contest.problems?.length || 0} problems
                  </div>
                </Link>
              ))
            ) : (
              <div className="text-gray-500 text-center py-4">
                No upcoming contests scheduled.
              </div>
            )}
            
            {upcomingContests.length < 2 && (
              <Link to="/contests" className="block p-3 bg-gray-50 rounded-lg hover:shadow-md transition-all text-center">
                <div className="font-medium text-primary-600">View All Contests →</div>
              </Link>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;