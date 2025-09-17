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
  Target,
  FileText,
  CheckCircle,
  XCircle,
  AlertCircle
} from 'lucide-react';
import { contestApi } from '../services/api';
import { useStore } from '../store/useStore';
import type { Contest as ContestType } from '../types';

interface LeaderboardEntry {
  rank: number;
  userId: number;
  username: string;
  fullName: string;
  score: number;
  problemsSolved: number;
  lastSubmission?: string;
}

interface UserSubmission {
  id: string;
  problemId: number;
  problemTitle: string;
  problemPoints: number;
  language: string;
  status: string;
  score: number;
  testCasesPassed: number;
  totalTestCases: number;
  submittedAt: string;
  code: string;
  isTestRun: boolean;
  executionTime?: number;
}

export default function Contest({ user }: { user: any }) {
  const { id } = useParams<{ id: string }>();
  const { isContestJoined, joinContest } = useStore();
  
  // Component state
  const [contest, setContest] = useState<ContestType | null>(null);
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<'problems' | 'leaderboard' | 'submissions'>('problems');
  const [joining, setJoining] = useState(false);
  const [hasJoined, setHasJoined] = useState(false);
  const [checkingJoinStatus, setCheckingJoinStatus] = useState(true);
  const [userSubmissions, setUserSubmissions] = useState<UserSubmission[]>([]);
  const [loadingSubmissions, setLoadingSubmissions] = useState(false);
  const [selectedSubmission, setSelectedSubmission] = useState<UserSubmission | null>(null);
  
  useEffect(() => {
    if (id) {
      loadContestData(parseInt(id));
      if (user) {
        checkJoinStatus(parseInt(id), user.id);
      } else {
        setCheckingJoinStatus(false);
      }
    }
  }, [id, user]);

  useEffect(() => {
    if (activeTab === 'submissions' && id && user && hasJoined) {
      loadUserSubmissions(parseInt(id), user.id);
    }
  }, [activeTab, id, user, hasJoined]);

  // Load and poll leaderboard when on leaderboard tab
  useEffect(() => {
    if (activeTab === 'leaderboard' && id && contest) {
      // Load leaderboard immediately when switching to tab
      loadLeaderboard(parseInt(id));

      // Poll every 15 seconds while on leaderboard tab
      const intervalId = setInterval(() => {
        loadLeaderboard(parseInt(id));
      }, 15000); // 15 seconds

      return () => clearInterval(intervalId);
    }
  }, [activeTab, id, contest]);

  
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

  const loadUserSubmissions = async (contestId: number, userId: number) => {
    setLoadingSubmissions(true);
    try {
      const response = await fetch(`http://localhost:8080/api/submissions/user/${userId}/contest/${contestId}`);
      if (response.ok) {
        const submissions = await response.json();
        // Filter out test runs and enrich with problem titles and points
        const finalSubmissions = submissions
          .filter((s: any) => !s.isTestRun)
          .map((s: any) => {
            const problem = contest?.problems?.find(p => p.id === s.problem.id);
            return {
              ...s,
              problemTitle: problem?.title || 'Unknown Problem',
              problemPoints: problem?.points || 100
            };
          });
        setUserSubmissions(finalSubmissions);
      }
    } catch (err) {
      console.error('Error loading user submissions:', err);
    } finally {
      setLoadingSubmissions(false);
    }
  };

  const checkJoinStatus = async (contestId: number, userId: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/contests/${contestId}/participants/${userId}`);
      if (response.ok) {
        const joined = await response.json();
        setHasJoined(joined);
      }
    } catch (err) {
      console.error('Error checking join status:', err);
    } finally {
      setCheckingJoinStatus(false);
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

      // Update join status
      setHasJoined(true);

      // Refresh leaderboard after joining
      loadLeaderboard(contest.id);
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
            {hasJoined && status.color === 'green' && (
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
            {user && hasJoined && (
              <button
                onClick={() => setActiveTab('submissions')}
                className={`px-6 py-3 text-sm font-medium border-b-2 transition-colors ${
                  activeTab === 'submissions'
                    ? 'border-indigo-500 text-indigo-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                }`}
              >
                <FileText className="inline h-4 w-4 mr-2" />
                Your Submissions
              </button>
            )}
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
          ) : activeTab === 'leaderboard' ? (
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
                            <span className="font-semibold text-gray-900">{entry.score}</span>
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
          ) : activeTab === 'submissions' ? (
            <div className="space-y-4">
              {loadingSubmissions ? (
                <div className="text-center py-8">
                  <div className="animate-pulse">Loading submissions...</div>
                </div>
              ) : userSubmissions.length > 0 ? (
                <>
                  <div className="overflow-x-auto">
                    <table className="w-full">
                      <thead>
                        <tr className="border-b border-gray-200">
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Problem
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Language
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Status
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Score
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Test Cases
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Submitted
                          </th>
                          <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Actions
                          </th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-200">
                        {userSubmissions.map((submission) => (
                          <tr key={submission.id} className="hover:bg-gray-50">
                            <td className="px-4 py-3">
                              <span className="font-medium text-gray-900">{submission.problemTitle}</span>
                            </td>
                            <td className="px-4 py-3">
                              <span className="px-2 py-1 bg-gray-100 text-gray-700 text-xs font-medium rounded">
                                {submission.language}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <div className="flex items-center">
                                {submission.status === 'ACCEPTED' ? (
                                  <CheckCircle className="h-4 w-4 text-green-500 mr-1" />
                                ) : submission.status === 'WRONG_ANSWER' ? (
                                  <XCircle className="h-4 w-4 text-red-500 mr-1" />
                                ) : (
                                  <AlertCircle className="h-4 w-4 text-yellow-500 mr-1" />
                                )}
                                <span className={`text-sm ${
                                  submission.status === 'ACCEPTED' ? 'text-green-600' :
                                  submission.status === 'WRONG_ANSWER' ? 'text-red-600' :
                                  'text-yellow-600'
                                }`}>
                                  {submission.status.replace(/_/g, ' ')}
                                </span>
                              </div>
                            </td>
                            <td className="px-4 py-3">
                              <span className="font-semibold text-gray-900">
                                {submission.score}/{submission.problemPoints}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <span className="text-gray-600">
                                {submission.testCasesPassed}/{submission.totalTestCases}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <span className="text-sm text-gray-500">
                                {new Date(submission.submittedAt).toLocaleString()}
                              </span>
                            </td>
                            <td className="px-4 py-3">
                              <button
                                onClick={() => setSelectedSubmission(submission)}
                                className="text-indigo-600 hover:text-indigo-800 text-sm font-medium"
                              >
                                View Code
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {/* Code Modal */}
                  {selectedSubmission && (
                    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
                      <div className="bg-white rounded-lg p-6 max-w-4xl w-full max-h-[80vh] overflow-y-auto">
                        <div className="flex justify-between items-start mb-4">
                          <div>
                            <h3 className="text-lg font-semibold text-gray-900">
                              Submission Details
                            </h3>
                            <p className="text-sm text-gray-600">
                              {selectedSubmission.problemTitle} - {selectedSubmission.language}
                            </p>
                          </div>
                          <button
                            onClick={() => setSelectedSubmission(null)}
                            className="text-gray-400 hover:text-gray-600"
                          >
                            <XCircle className="h-6 w-6" />
                          </button>
                        </div>

                        <div className="mb-4 flex items-center space-x-4 text-sm">
                          <span className={`flex items-center ${
                            selectedSubmission.status === 'ACCEPTED' ? 'text-green-600' :
                            selectedSubmission.status === 'WRONG_ANSWER' ? 'text-red-600' :
                            'text-yellow-600'
                          }`}>
                            {selectedSubmission.status === 'ACCEPTED' ? (
                              <CheckCircle className="h-4 w-4 mr-1" />
                            ) : selectedSubmission.status === 'WRONG_ANSWER' ? (
                              <XCircle className="h-4 w-4 mr-1" />
                            ) : (
                              <AlertCircle className="h-4 w-4 mr-1" />
                            )}
                            {selectedSubmission.status.replace(/_/g, ' ')}
                          </span>
                          <span className="text-gray-600">
                            Score: {selectedSubmission.score}/{selectedSubmission.problemPoints}
                          </span>
                          <span className="text-gray-600">
                            Test Cases: {selectedSubmission.testCasesPassed}/{selectedSubmission.totalTestCases}
                          </span>
                          {selectedSubmission.executionTime && (
                            <span className="text-gray-600">
                              Time: {selectedSubmission.executionTime}ms
                            </span>
                          )}
                        </div>

                        <div className="bg-gray-900 rounded-lg p-4 overflow-x-auto">
                          <pre className="text-sm text-gray-300 font-mono">
                            <code>{selectedSubmission.code}</code>
                          </pre>
                        </div>
                      </div>
                    </div>
                  )}
                </>
              ) : (
                <div className="text-center py-8 text-gray-500">
                  You haven't made any submissions for this contest yet.
                </div>
              )}
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
}