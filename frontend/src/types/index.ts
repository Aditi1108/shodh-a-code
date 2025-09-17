export interface User {
  id: number;
  username: string;
  fullName?: string;
  email: string;
  score: number;
  problemsSolved: number;
}

export interface Contest {
  id: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  isActive: boolean;
}

export interface Problem {
  id: number;
  title: string;
  contest?: Contest;
  description: string;
  inputFormat: string;
  outputFormat: string;
  constraints: string;
  points: number;
  timeLimit: number;
  memoryLimit: number;
  testCases?: TestCase[];
}

export interface TestCase {
  id: number;
  isHidden: boolean;
  input: string;
  expectedOutput: string;
  timeLimit?: number;
  memoryLimit?: number;
}

export type ProgrammingLanguage =
  | 'JAVA'
  | 'PYTHON3'
  | 'CPP'
  | 'JAVASCRIPT';

export type SubmissionStatus = 
  | 'PENDING' 
  | 'RUNNING' 
  | 'ACCEPTED' 
  | 'WRONG_ANSWER' 
  | 'TIME_LIMIT_EXCEEDED' 
  | 'MEMORY_LIMIT_EXCEEDED' 
  | 'RUNTIME_ERROR' 
  | 'COMPILATION_ERROR';

export interface Submission {
  id: string;
  user: User;
  problem: Problem;
  status: SubmissionStatus;
  language: ProgrammingLanguage;
  score: number;
  testCasesPassed: number;
  totalTestCases: number;
  submittedAt: string;
  executionTime?: number;
  code: string;
  output?: string;
  errorMessage?: string;
}

export interface SubmissionRequest {
  userId: number;
  problemId: number;
  code: string;
  language: ProgrammingLanguage;
}

export interface SubmissionResponse {
  submissionId: string;
  status: string;
}