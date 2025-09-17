import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Clock, Database, ChevronRight, Play, Send, CheckCircle, XCircle, AlertCircle, Loader, Trophy, ArrowLeft, FileText } from 'lucide-react';
import { contestApi, submissionApi } from '../services/api';
import { useStore } from '../store/useStore';
import Leaderboard from '../components/Leaderboard';
import type { Problem as ProblemType, ProgrammingLanguage, SubmissionResponse, Contest } from '../types';

export default function Problem({ user }: { user: any }) {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [problem, setProblem] = useState<ProblemType | null>(null);
  const [contest, setContest] = useState<Contest | null>(null);
  const [code, setCode] = useState('');
  const [codeInitialized, setCodeInitialized] = useState(false);
  const [languages, setLanguages] = useState<ProgrammingLanguage[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [pollingStatus, setPollingStatus] = useState(false);
  const [activeTab, setActiveTab] = useState<'problem' | 'leaderboard'>('problem');
  const pollingIntervalRef = useRef<NodeJS.Timer | null>(null);

  const { selectedLanguage, setSelectedLanguage } = useStore();

  // Reset code when language changes
  const handleLanguageChange = (language: ProgrammingLanguage) => {
    setSelectedLanguage(language);
    setCodeInitialized(false);
    setCode(''); // Clear current code to trigger template
  };
  
  useEffect(() => {
    if (id) {
      loadProblem(parseInt(id));
      loadLanguages();
    }
    
    // Cleanup polling on component unmount
    return () => {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current);
      }
    };
  }, [id]);

  // Set default template based on selected language
  useEffect(() => {
    if (!codeInitialized) {
      if (selectedLanguage === 'JAVA') {
      setCode(`import java.util.Scanner;

/**
 * IMPORTANT:
 * 1. Your main class MUST be named 'Solution' (not Main, not FizzBuzz, etc.)
 * 2. Do NOT change the class name or it will result in compilation error
 * 3. You can create additional methods and classes if needed
 * 4. Input is read from System.in (use Scanner as shown below)
 * 5. Output should be printed to System.out (use System.out.println())
 */
public class Solution {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read input here
        // Example: int n = scanner.nextInt();

        // Call your solution method
        // Example: solve(n);

        scanner.close();
    }

    // Add your solution method(s) here
    // Example:
    // public static void solve(int n) {
    //     // Your logic here
    // }
}`);
        setCodeInitialized(true);
      } else if (selectedLanguage === 'PYTHON3') {
        setCode(`# Read input using input()
# Example: n = int(input())

# Write your solution here
# Example:
# def solve(n):
#     # Your logic here
#     pass

# Call your solution
# solve(n)
`);
        setCodeInitialized(true);
      } else if (selectedLanguage === 'CPP') {
        setCode(`#include <iostream>
using namespace std;

int main() {
    // Read input
    // Example: int n; cin >> n;

    // Write your solution here

    return 0;
}`);
        setCodeInitialized(true);
      } else if (selectedLanguage === 'C') {
        setCode(`#include <stdio.h>

int main() {
    // Read input
    // Example: int n; scanf("%d", &n);

    // Write your solution here

    return 0;
}`);
        setCodeInitialized(true);
      } else if (selectedLanguage === 'JAVASCRIPT') {
        setCode(`// For Node.js environment
// Read input using readline or process.stdin

const readline = require('readline');
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.on('line', (line) => {
    // Process input line
    // Example: const n = parseInt(line);

    // Write your solution here

    rl.close();
});`);
        setCodeInitialized(true);
      }
    }
  }, [selectedLanguage, codeInitialized]);

  const loadProblem = async (problemId: number) => {
    try {
      // First find which contest contains this problem
      const contests = await contestApi.getAll();
      let contestFound = false;
      
      for (const contestItem of contests) {
        if (contestItem.problems) {
          const problemExists = contestItem.problems.find((p: any) => p.id === problemId);
          if (problemExists) {
            contestFound = true;
            setContest(contestItem);
            break;
          }
        }
      }
      
      if (contestFound) {
        // Now fetch the full problem details from the problems endpoint
        const response = await fetch(`http://localhost:8080/api/problems/${problemId}`);
        if (response.ok) {
          const problemData = await response.json();
          // Map sampleTestCases to testCases for compatibility
          if (problemData.sampleTestCases) {
            problemData.testCases = problemData.sampleTestCases;
          }
          setProblem(problemData);
        } else {
          setError('Problem not found');
        }
      } else {
        setError('Problem not found in any contest');
      }
    } catch (err) {
      setError('Failed to load problem');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };
  
  const loadLanguages = async () => {
    try {
      const langs = await submissionApi.getSupportedLanguages();
      setLanguages(langs);
    } catch (err) {
      console.error('Failed to load languages:', err);
    }
  };
  
  const handleRunCode = async () => {
    if (!user || !problem) {
      setError('Please login to run code');
      return;
    }

    setSubmitting(true);
    setSubmissionResult(null);
    setError('');

    try {
      const result = await submissionApi.run({
        userId: user.id,
        problemId: problem.id,
        code,
        language: selectedLanguage,
      });

      // Start polling for submission status
      startPollingSubmissionStatus(result.submissionId);
    } catch (err: any) {
      const errorMessage = err.response?.data || err.message || 'Failed to run code';
      setError(errorMessage);
      console.error(err);
      setSubmitting(false);
    }
  };

  const handleSubmitCode = async () => {
    if (!user || !problem) {
      setError('Please login to submit');
      return;
    }

    setSubmitting(true);
    setSubmissionResult(null);
    setError('');

    try {
      const result = await submissionApi.submit({
        userId: user.id,
        problemId: problem.id,
        code,
        language: selectedLanguage,
      });
      
      // Start polling for submission status
      startPollingSubmissionStatus(result.submissionId);
    } catch (err: any) {
      const errorMessage = err.response?.data || err.message || 'Failed to submit code';
      setError(errorMessage);
      console.error(err);
      setSubmitting(false);
    }
  };
  
  const startPollingSubmissionStatus = (submissionId: string) => {
    setPollingStatus(true);
    
    // Clear any existing polling interval
    if (pollingIntervalRef.current) {
      clearInterval(pollingIntervalRef.current);
    }
    
    // Initial status fetch
    fetchSubmissionStatus(submissionId);
    
    // Poll every 2 seconds
    pollingIntervalRef.current = setInterval(() => {
      fetchSubmissionStatus(submissionId);
    }, 2000);
  };
  
  const fetchSubmissionStatus = async (submissionId: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/submissions/${submissionId}`);
      if (!response.ok) throw new Error('Failed to fetch submission status');
      
      const submission = await response.json();
      setSubmissionResult(submission);
      
      // Check if we've reached a definitive state (not PENDING or RUNNING)
      const definitiveStates = ['ACCEPTED', 'WRONG_ANSWER', 'TIME_LIMIT_EXCEEDED', 
                               'MEMORY_LIMIT_EXCEEDED', 'RUNTIME_ERROR', 
                               'COMPILATION_ERROR', 'PARTIALLY_ACCEPTED'];
      
      if (definitiveStates.includes(submission.status)) {
        // Stop polling
        if (pollingIntervalRef.current) {
          clearInterval(pollingIntervalRef.current);
          pollingIntervalRef.current = null;
        }
        setPollingStatus(false);
        setSubmitting(false);
      }
    } catch (err) {
      console.error('Error fetching submission status:', err);
      // Continue polling even if there's an error
    }
  };
  
  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ACCEPTED':
        return <CheckCircle className="h-5 w-5 text-green-500" />;
      case 'WRONG_ANSWER':
      case 'RUNTIME_ERROR':
      case 'COMPILATION_ERROR':
      case 'TIME_LIMIT_EXCEEDED':
      case 'MEMORY_LIMIT_EXCEEDED':
        return <XCircle className="h-5 w-5 text-red-500" />;
      case 'PARTIALLY_ACCEPTED':
        return <AlertCircle className="h-5 w-5 text-yellow-500" />;
      case 'PENDING':
      case 'RUNNING':
        return <Loader className="h-5 w-5 text-blue-500 animate-spin" />;
      default:
        return null;
    }
  };
  
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACCEPTED':
        return 'bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800';
      case 'WRONG_ANSWER':
      case 'RUNTIME_ERROR':
      case 'COMPILATION_ERROR':
      case 'TIME_LIMIT_EXCEEDED':
      case 'MEMORY_LIMIT_EXCEEDED':
        return 'bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-800';
      case 'PARTIALLY_ACCEPTED':
        return 'bg-yellow-50 dark:bg-yellow-900/20 border-yellow-200 dark:border-yellow-800';
      case 'PENDING':
      case 'RUNNING':
        return 'bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800';
      default:
        return 'bg-gray-50 dark:bg-gray-900/20 border-gray-200 dark:border-gray-800';
    }
  };
  
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-600 dark:text-gray-300">Loading problem...</div>
      </div>
    );
  }
  
  if (!problem) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-red-600 dark:text-red-400">Problem not found</div>
      </div>
    );
  }
  
  return (
    <div className="space-y-6">
      {/* Navigation Header */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate(`/contest/${contest?.id}`)}
              className="flex items-center gap-2 text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white transition-colors"
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Back to Contest</span>
            </button>
            {contest && (
              <div className="text-gray-500 dark:text-gray-400">
                <ChevronRight className="h-4 w-4 inline" />
                <span className="ml-2 font-medium">{contest.title}</span>
              </div>
            )}
          </div>

          <div className="flex gap-2">
            <button
              onClick={() => setActiveTab('problem')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'problem'
                  ? 'bg-indigo-600 text-white'
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
              }`}
            >
              <FileText className="h-4 w-4" />
              Problem
            </button>
            <button
              onClick={() => setActiveTab('leaderboard')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors flex items-center gap-2 ${
                activeTab === 'leaderboard'
                  ? 'bg-indigo-600 text-white'
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
              }`}
            >
              <Trophy className="h-4 w-4" />
              Leaderboard
            </button>
          </div>
        </div>
      </div>

      {/* Content based on active tab */}
      {activeTab === 'problem' ? (
        <div className="grid lg:grid-cols-2 gap-6">
          <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
              {problem.title}
            </h1>
        
        <div className="flex items-center space-x-4 mb-6 text-sm text-gray-600 dark:text-gray-400">
          <div className="flex items-center space-x-1">
            <Clock className="h-4 w-4" />
            <span>{problem.timeLimit}ms</span>
          </div>
          <div className="flex items-center space-x-1">
            <Database className="h-4 w-4" />
            <span>{problem.memoryLimit}MB</span>
          </div>
          <div>
            <span className="font-medium">{problem.points} points</span>
          </div>
        </div>
        
        <div className="prose dark:prose-invert max-w-none">
          <section className="mb-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
              Description
            </h3>
            <p className="text-gray-700 dark:text-gray-300 whitespace-pre-wrap">
              {problem.description}
            </p>
          </section>
          
          <section className="mb-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
              Input Format
            </h3>
            <p className="text-gray-700 dark:text-gray-300 whitespace-pre-wrap">
              {problem.inputFormat}
            </p>
          </section>
          
          <section className="mb-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
              Output Format
            </h3>
            <p className="text-gray-700 dark:text-gray-300 whitespace-pre-wrap">
              {problem.outputFormat}
            </p>
          </section>
          
          {problem.constraints && (
            <section className="mb-6">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                Constraints
              </h3>
              <p className="text-gray-700 dark:text-gray-300 whitespace-pre-wrap">
                {problem.constraints}
              </p>
            </section>
          )}
          
          {problem.testCases && problem.testCases.filter(tc => !tc.isHidden).length > 0 && (
            <section>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                Sample Test Cases
              </h3>
              {problem.testCases
                .filter(tc => !tc.isHidden)
                .map((testCase, index) => (
                  <div key={testCase.id} className="mb-4 bg-gray-50 dark:bg-gray-700 p-4 rounded">
                    <div className="mb-2">
                      <strong className="text-gray-900 dark:text-white">Input {index + 1}:</strong>
                      <pre className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                        {testCase.input}
                      </pre>
                    </div>
                    <div>
                      <strong className="text-gray-900 dark:text-white">Output {index + 1}:</strong>
                      <pre className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                        {testCase.expectedOutput}
                      </pre>
                    </div>
                  </div>
                ))}
            </section>
          )}
        </div>
      </div>
      
      <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
            Code Editor
          </h2>
          <select
            value={selectedLanguage}
            onChange={(e) => handleLanguageChange(e.target.value as ProgrammingLanguage)}
            className="px-3 py-1 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
          >
            {languages.map((lang) => (
              <option key={lang} value={lang}>
                {lang}
              </option>
            ))}
          </select>
        </div>
        
        <div className="mb-4">
          <textarea
            value={code}
            onChange={(e) => setCode(e.target.value)}
            className="w-full h-96 px-3 py-2 font-mono text-sm border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-900 dark:text-gray-100"
            placeholder="Write your code here..."
          />
        </div>
        
        {error && (
          <div className="mb-4 text-red-600 dark:text-red-400 text-sm">
            {error}
          </div>
        )}
        
        {submissionResult && (
          <div className={`mb-4 p-4 rounded-lg border ${getStatusColor(submissionResult.status)}`}>
            <div className="flex items-center space-x-2 mb-2">
              {getStatusIcon(submissionResult.status)}
              <span className="font-semibold text-gray-900 dark:text-white">
                {submissionResult.status.replace(/_/g, ' ')}
              </span>
            </div>
            
            {submissionResult.score !== undefined && submissionResult.score !== null && submissionResult.score > 0 && (
              <p className="text-sm text-gray-700 dark:text-gray-300">
                Score: {submissionResult.score}/{problem.points}
              </p>
            )}
            
            {submissionResult.testCasesPassed !== undefined && submissionResult.totalTestCases !== undefined && (
              <p className="text-sm text-gray-700 dark:text-gray-300">
                Test Cases: {submissionResult.testCasesPassed}/{submissionResult.totalTestCases} passed
              </p>
            )}
            
            {submissionResult.executionTime && (
              <p className="text-sm text-gray-700 dark:text-gray-300">
                Execution Time: {submissionResult.executionTime}ms
              </p>
            )}
            
            {submissionResult.errorMessage && (
              <div className="mt-2 p-2 bg-red-100 dark:bg-red-900/30 rounded">
                <p className="text-sm text-red-800 dark:text-red-200 font-mono">
                  {submissionResult.errorMessage}
                </p>
              </div>
            )}
            
            {submissionResult.output && (
              <div className="mt-2 p-2 bg-gray-100 dark:bg-gray-800 rounded">
                <p className="text-sm text-gray-800 dark:text-gray-200 font-mono whitespace-pre-wrap">
                  {submissionResult.output}
                </p>
              </div>
            )}
            
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-2">
              Submission ID: {submissionResult.id || submissionResult.submissionId}
            </p>
          </div>
        )}
        
        <div className="flex space-x-3">
          <button
            onClick={handleRunCode}
            disabled={submitting || !code.trim() || !user}
            className="flex items-center space-x-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            title="Test against sample test cases only"
          >
            <Play className="h-4 w-4" />
            <span>{submitting ? 'Running...' : 'Run Code'}</span>
          </button>
          
          <button
            onClick={handleSubmitCode}
            disabled={submitting || !code.trim() || !user}
            className="flex items-center space-x-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            title="Submit for evaluation against all test cases"
          >
            <Send className="h-4 w-4" />
            <span>{submitting ? 'Submitting...' : 'Submit Code'}</span>
          </button>
        </div>
        
        {!user && (
          <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
            Please login to submit your solution
          </p>
        )}
      </div>
    </div>
      ) : (
        /* Leaderboard Tab */
        contest && <Leaderboard contestId={contest.id} />
      )}
    </div>
  );
}