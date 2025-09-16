import axios from 'axios';
import type { User, Contest, Problem, Submission, SubmissionRequest, SubmissionResponse, ProgrammingLanguage } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const userApi = {
  getAllUsers: async (): Promise<User[]> => {
    const { data } = await api.get('/users');
    return data;
  },
  
  getUser: async (id: number): Promise<User> => {
    const { data } = await api.get(`/users/${id}`);
    return data;
  },
  
  register: async (username: string, email: string, fullName?: string): Promise<User> => {
    const { data } = await api.post('/users/register', { username, email, fullName });
    return data;
  },
  
  checkUser: async (username: string): Promise<User> => {
    const { data } = await api.get(`/users/check/${username}`);
    return data;
  },
};

export const contestApi = {
  getAll: async (): Promise<Contest[]> => {
    const { data } = await api.get('/contests');
    return data;
  },
  
  getById: async (id: number): Promise<Contest> => {
    const { data } = await api.get(`/contests/${id}`);
    return data;
  },
  
  getProblems: async (contestId: number): Promise<Problem[]> => {
    const { data } = await api.get(`/contests/${contestId}/problems`);
    return data;
  },
  
  getLeaderboard: async (contestId: number): Promise<any> => {
    const { data } = await api.get(`/contests/${contestId}/leaderboard`);
    return data;
  },
  
  joinContest: async (username: string, contestId: number): Promise<any> => {
    const { data } = await api.post('/contests/join', { username, contestId });
    return data;
  },
};

export const problemApi = {
  getAll: async (): Promise<Problem[]> => {
    const { data } = await api.get('/problems');
    return data;
  },
  
  getById: async (id: number): Promise<Problem> => {
    const { data } = await api.get(`/problems/${id}`);
    return data;
  },
};

export const submissionApi = {
  submit: async (request: SubmissionRequest): Promise<SubmissionResponse> => {
    const { data } = await api.post('/submissions', request);
    return data;
  },
  
  getById: async (id: string): Promise<Submission> => {
    const { data } = await api.get(`/submissions/${id}`);
    return data;
  },
  
  getLatest: async (userId: number, problemId: number): Promise<Submission> => {
    const { data } = await api.get(`/submissions/user/${userId}/problem/${problemId}/latest`);
    return data;
  },
  
  getUserProblemSubmissions: async (userId: number, problemId: number): Promise<Submission[]> => {
    const { data } = await api.get(`/submissions/user/${userId}/problem/${problemId}`);
    return data;
  },
  
  getUserContestSubmissions: async (userId: number, contestId: number): Promise<Submission[]> => {
    const { data } = await api.get(`/submissions/user/${userId}/contest/${contestId}`);
    return data;
  },
  
  getSupportedLanguages: async (): Promise<ProgrammingLanguage[]> => {
    const { data } = await api.get('/submissions/languages');
    return data;
  },
};