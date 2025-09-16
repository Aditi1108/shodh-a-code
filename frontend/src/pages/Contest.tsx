import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { 
  Trophy, 
  Clock,
  Code2,
  Medal,
  Calendar,
  ChevronRight,
  Users,
  Target
} from 'lucide-react';
import { contestApi } from '../services/api';
import { useStore } from '../store/useStore';
import type { Contest as ContestType } from '../types';

interface LeaderboardEntry {
  username: string;
  problemsSolved: number;
  totalScore: number;
}

export default function Contest({ user }: { user: any }) {
  const { id } = useParams<{ id: string }>();
  const { isContestJoined, joinContest } = useStore();
  
  // State
  const [contest, setContest] = useState<ContestType | null>(null);
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<'problems' | 'leaderboard'>('problems');
  const [joining, setJoining] = useState(false);
  
  // Get join status from store
  const hasJoined = contest ? isContestJoined(contest.id) : false;
  
  useEffect(() => {
    if (id) {
      loadContestData(parseInt(id));
      loadLeaderboard(parseInt(id));
    }
  }, [id]);
  
  
  const loadContestData = async (contestId: number) => {
    try {
      const data = await contestApi.getById(contestId);
      setContest(data);
    } catch (err: any) {
      console.error('Error loading contest:', err);
      setError('Failed to load contest');
    } finally {
      setLoading(false);
    }
  };
  
  const loadLeaderboard = async (contestId: number) => {
    try {
      const data = await contestApi.getLeaderboard(contestId);
      setLeaderboard(data);
    } catch (err) {
      console.error('Error loading leaderboard:', err);
    }
  };
  
  const handleJoinContest = async () => {
    if (!user || !contest) {
      setError('Please login to join the contest');
      return;
    }
    
    setJoining(true);
    setError('');
    
    try {
      await contestApi.joinContest(user.username, contest.id);
      
      // Update the store to mark this contest as joined
      joinContest(contest.id);
      
      // Add user to leaderboard with 0 score if not already there
      const userInLeaderboard = leaderboard.find(entry => entry.username === user.username);
      if (!userInLeaderboard) {
        setLeaderboard([...leaderboard, {
          username: user.username,
          problemsSolved: 0,
          totalScore: 0
        }]);
      }
    } catch (err: any) {
      console.error('Join contest error:', err);
      setError(err.response?.data || err.message || 'Failed to join contest');
    } finally {
      setJoining(false);
    }
  };
  
  const getContestStatus = () => {
    if (!contest) return { text: 'Loading', color: 'gray' };
    
    const now = new Date();
    const start = new Date(contest.startTime);
    const end = new Date(contest.endTime);
    
    if (now < start) return { text: 'Upcoming', color: 'blue' };
    if (now > end) return { text: 'Ended', color: 'gray' };
    return { text: 'Active', color: 'green' };
  };
  
  const formatDateTime = (dateStr: string) => {
    return new Date(dateStr).toLocaleString();
  };
  
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-pulse">
          <Trophy className="h-12 w-12 text-indigo-500 mb-4 mx-auto" />
          <div className="text-gray-600">Loading contest...</div>
        </div>
      </div>
    );
  }
  
  if (error || !contest) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6">
        <h3 className="text-red-800 font-semibold mb-2">Error</h3>
        <p className="text-red-600">{error || 'Contest not found'}</p>
      </div>
    );
  }
  
  const status = getContestStatus();
  
  return (
    <div className="space-y-6">
      {/* Contest Header */}
      <div className="bg-white rounded-2xl shadow-lg p-6">
        <div className="flex justify-between items-start mb-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{contest.title}</h1>
            <p className="text-gray-600">{contest.description}</p>
          </div>
          <div className="flex items-center space-x-4">
            {status.color === 'green' && !hasJoined && user && (
              <button
                onClick={handleJoinContest}
                disabled={joining}
                className="px-4 py-2 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 transition-all disabled:opacity-50"
              >
                {joining ? 'Joining...' : 'Join Contest'}
              </button>
            )}
            {hasJoined && (
              <span className="px-4 py-2 bg-blue-100 text-blue-800 font-medium rounded-lg">
                ✓ Joined
              </span>
            )}
            <span className={`px-4 py-2 rounded-full text-sm font-medium ${
              status.color === 'green' ? 'bg-green-100 text-green-800' :
              status.color === 'blue' ? 'bg-blue-100 text-blue-800' :
              'bg-gray-100 text-gray-800'
            }`}>
              {status.text}
            </span>
          </div>
        </div>
        
        <div className="flex items-center space-x-6 text-sm text-gray-500">
          <div className="flex items-center space-x-2">
            <Calendar className="h-4 w-4" />
            <span>Start: {formatDateTime(contest.startTime)}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Clock className="h-4 w-4" />
            <span>End: {formatDateTime(contest.endTime)}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Target className="h-4 w-4" />
            <span>{contest.problems?.length || 0} Problems</span>
          </div>
          <div className="flex items-center space-x-2">
            <Users className="h-4 w-4" />
            <span>{leaderboard.length} Participants</span>
          </div>
        </div>
      </div>
      
      {/* Tabs */}
      <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
        <div className="border-b border-gray-200">
          <div className="flex">
            <button
              onClick={() => setActiveTab('problems')}
              className={`px-6 py-3 text-sm font-medium border-b-2 transition-colors ${
                activeTab === 'problems'
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              <Code2 className="inline h-4 w-4 mr-2" />
              Problems
            </button>
            <button
              onClick={() => setActiveTab('leaderboard')}
              className={`px-6 py-3 text-sm font-medium border-b-2 transition-colors ${
                activeTab === 'leaderboard'
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              <Trophy className="inline h-4 w-4 mr-2" />
              Leaderboard
            </button>
          </div>
        </div>
        
        {/* Tab Content */}
        <div className="p-6">
          {activeTab === 'problems' ? (
            <div className="space-y-4">
              {contest.problems && contest.problems.length > 0 ? (
                contest.problems.map((problem, index) => (
                  <div key={problem.id} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
                    <div className="flex justify-between items-center">
                      <div className="flex items-center space-x-4">
                        <span className="text-gray-500 font-mono text-sm">#{index + 1}</span>
                        <h3 className="text-lg font-semibold text-gray-900">{problem.title}</h3>
                        <span className="px-3 py-1 bg-yellow-100 text-yellow-700 text-sm font-medium rounded-full">
                          {problem.points} pts
                        </span>
                      </div>
                      <Link
                        to={`/problem/${problem.id}`}
                        className="inline-flex items-center space-x-1 px-4 py-2 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-lg hover:from-indigo-600 hover:to-purple-700 transition-all"
                      >
                        <span>Solve</span>
                        <ChevronRight className="h-4 w-4" />
                      </Link>
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-gray-500 text-center py-8">No problems available for this contest.</p>
              )}
            </div>
          ) : (
            <div className="space-y-4">
              {/* Leaderboard */}
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-gray-200">
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Rank
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Username
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Problems Solved
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Score
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {leaderboard.length > 0 ? (
                      leaderboard.map((entry, index) => (
                        <tr key={index} className="hover:bg-gray-50">
                          <td className="px-4 py-3">
                            <div className="flex items-center">
                              {index < 3 ? (
                                <Medal className={`h-5 w-5 mr-2 ${
                                  index === 0 ? 'text-yellow-500' :
                                  index === 1 ? 'text-gray-400' :
                                  'text-orange-600'
                                }`} />
                              ) : null}
                              <span className="font-medium text-gray-900">#{index + 1}</span>
                            </div>
                          </td>
                          <td className="px-4 py-3">
                            <span className={`font-medium ${
                              entry.username === user?.username ? 'text-indigo-600' : 'text-gray-900'
                            }`}>
                              {entry.username}
                              {entry.username === user?.username && ' (You)'}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            <span className="text-gray-600">{entry.problemsSolved}</span>
                          </td>
                          <td className="px-4 py-3">
                            <span className="font-semibold text-gray-900">{entry.totalScore}</span>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan={4} className="px-4 py-8 text-center text-gray-500">
                          No submissions yet. Be the first to solve a problem!
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}