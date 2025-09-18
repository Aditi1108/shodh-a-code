import { useState, useEffect } from 'react';
import { Trophy, Medal, Award, User } from 'lucide-react';

interface LeaderboardEntry {
  rank: number;
  userId: number;
  username: string;
  fullName: string;
  score: number;
  problemsSolved: number;
  lastSubmission?: string;
}

interface LeaderboardProps {
  contestId: number;
}

export default function Leaderboard({ contestId }: LeaderboardProps) {
  const [entries, setEntries] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadLeaderboard();
    // Poll leaderboard every 20 seconds
    const interval = setInterval(loadLeaderboard, 20000);
    return () => clearInterval(interval);
  }, [contestId]);

  const loadLeaderboard = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/contests/${contestId}/leaderboard`);
      if (response.ok) {
        const data = await response.json();
        setEntries(data);
      }
    } catch (error) {
      console.error('Failed to load leaderboard:', error);
    } finally {
      setLoading(false);
    }
  };

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return <Trophy className="h-5 w-5 text-yellow-500" />;
      case 2:
        return <Medal className="h-5 w-5 text-gray-400" />;
      case 3:
        return <Award className="h-5 w-5 text-yellow-800" />;
      default:
        return <span className="text-gray-600 dark:text-gray-400 font-medium">{rank}</span>;
    }
  };

  const getRankClass = (rank: number) => {
    switch (rank) {
      case 1:
        return 'bg-gradient-to-r from-yellow-50 to-yellow-100 dark:from-yellow-900/20 dark:to-yellow-800/20 border-yellow-300 dark:border-yellow-700';
      case 2:
        return 'bg-gradient-to-r from-gray-50 to-gray-100 dark:from-gray-900/20 dark:to-gray-800/20 border-gray-300 dark:border-gray-700';
      case 3:
        return 'bg-gradient-to-r from-yellow-50/50 to-yellow-100/50 dark:from-yellow-900/10 dark:to-yellow-800/10 border-yellow-700/30 dark:border-yellow-800/30';
      default:
        return 'bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700';
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-600 dark:text-gray-300">Loading leaderboard...</div>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
      <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
        <Trophy className="h-6 w-6 text-yellow-500" />
        Contest Leaderboard
      </h2>

      <div className="space-y-2">
        <div className="grid grid-cols-12 gap-4 px-4 py-2 text-sm font-medium text-gray-600 dark:text-gray-400 border-b dark:border-gray-700">
          <div className="col-span-1">Rank</div>
          <div className="col-span-3">Participant</div>
          <div className="col-span-2 text-center">Problems</div>
          <div className="col-span-2 text-center">Score</div>
          <div className="col-span-4 text-right">Last Submission</div>
        </div>

        {entries.length === 0 ? (
          <div className="text-center py-8 text-gray-500 dark:text-gray-400">
            No submissions yet. Be the first to solve a problem!
          </div>
        ) : (
          entries.map((entry) => (
            <div
              key={entry.userId}
              className={`grid grid-cols-12 gap-4 px-4 py-3 rounded-lg border transition-all ${getRankClass(entry.rank)}`}
            >
              <div className="col-span-1 flex items-center">
                {getRankIcon(entry.rank)}
              </div>
              <div className="col-span-3 flex items-center gap-2">
                <User className="h-4 w-4 text-gray-400" />
                <div className="font-medium text-gray-900 dark:text-white">
                  {entry.username}
                </div>
              </div>
              <div className="col-span-2 text-center flex items-center justify-center">
                <span className="text-gray-700 dark:text-gray-300">
                  {entry.problemsSolved}
                </span>
              </div>
              <div className="col-span-2 text-center flex items-center justify-center">
                <span className="font-semibold text-gray-900 dark:text-white">
                  {entry.score}
                </span>
              </div>
              <div className="col-span-4 text-right flex items-center justify-end text-sm text-gray-500 dark:text-gray-400">
                {entry.lastSubmission || 'No submissions'}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}