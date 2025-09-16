import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ChevronRight, Code2, Trophy, Clock } from 'lucide-react';
import { contestApi } from '../services/api';
import type { Problem, Contest } from '../types';

export default function ProblemList() {
  const [problems, setProblems] = useState<Problem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  useEffect(() => {
    loadProblems();
  }, []);
  
  const loadProblems = async () => {
    try {
      const contests = await contestApi.getAll();
      const allProblems: Problem[] = [];
      
      // Extract problems from contests (they're already included in the response)
      for (const contest of contests) {
        if (contest.problems && contest.problems.length > 0) {
          // Add contest reference to each problem
          const problemsWithContest = contest.problems.map(problem => ({
            ...problem,
            contest: {
              id: contest.id,
              title: contest.title
            }
          }));
          allProblems.push(...problemsWithContest);
        }
      }
      
      setProblems(allProblems);
    } catch (err: any) {
      console.error('Error loading problems:', err);
      if (err.code === 'ERR_NETWORK') {
        setError('Cannot connect to server. Please make sure the backend is running on http://localhost:8080');
      } else {
        setError(`Failed to load problems: ${err.message}`);
      }
    } finally {
      setLoading(false);
    }
  };
  
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-pulse">
          <Code2 className="h-12 w-12 text-indigo-500 mb-4 mx-auto" />
          <div className="text-gray-600">Loading problems...</div>
        </div>
      </div>
    );
  }
  
  if (error) {
    return (
      <div className="container mx-auto px-6 py-12">
        <div className="bg-red-50 border border-red-200 rounded-lg p-6">
          <h3 className="text-red-800 font-semibold mb-2">Error Loading Problems</h3>
          <p className="text-red-600">{error}</p>
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
    <div>
      <div className="mb-8">
        <h1 className="text-4xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent mb-2">
          Practice Problems
        </h1>
        <p className="text-gray-600">
          Sharpen your skills with problems from all contests
        </p>
      </div>
      
      <div className="bg-white rounded-xl shadow-xl overflow-hidden border border-gray-200">
        <table className="w-full">
          <thead className="bg-gradient-to-r from-indigo-50 to-purple-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Title
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Contest
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Points
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Action
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {problems.map((problem) => (
              <tr key={problem.id} className="hover:bg-gradient-to-r hover:from-indigo-50/50 hover:to-purple-50/50 transition-colors">
                <td className="px-6 py-4">
                  <div className="flex items-center space-x-2">
                    <Code2 className="h-4 w-4 text-indigo-500" />
                    <span className="text-sm font-medium text-gray-900">
                      {problem.title}
                    </span>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm text-gray-600">
                    {problem.contest?.title || 'Practice'}
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="flex items-center space-x-1">
                    <Trophy className="h-4 w-4 text-yellow-500" />
                    <span className="text-sm font-semibold text-gray-700">
                      {problem.points}
                    </span>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <Link
                    to={`/problem/${problem.id}`}
                    className="inline-flex items-center space-x-1 px-3 py-1 bg-gradient-to-r from-indigo-500 to-purple-600 text-white rounded-lg hover:from-indigo-600 hover:to-purple-700 transition-all transform hover:scale-105"
                  >
                    <span>Solve</span>
                    <ChevronRight className="h-4 w-4" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        {problems.length === 0 && (
          <div className="text-center py-12 text-gray-600">
            No problems available at the moment.
          </div>
        )}
      </div>
    </div>
  );
}