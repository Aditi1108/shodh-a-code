import { create } from 'zustand';
import type { User, Contest, Problem, ProgrammingLanguage } from '../types';

interface AppState {
  user: User | null;
  currentContest: Contest | null;
  currentProblem: Problem | null;
  selectedLanguage: ProgrammingLanguage;
  isDarkMode: boolean;
  joinedContests: Set<number>;
  
  setUser: (user: User | null) => void;
  setCurrentContest: (contest: Contest | null) => void;
  setCurrentProblem: (problem: Problem | null) => void;
  setSelectedLanguage: (language: ProgrammingLanguage) => void;
  toggleDarkMode: () => void;
  joinContest: (contestId: number) => void;
  isContestJoined: (contestId: number) => boolean;
  clearJoinedContests: () => void;
}

// Helper functions for localStorage persistence
const getJoinedContestsFromStorage = (): Set<number> => {
  try {
    const stored = localStorage.getItem('joinedContests');
    if (stored) {
      const contestIds = JSON.parse(stored);
      return new Set(contestIds);
    }
  } catch (error) {
    console.warn('Failed to parse joined contests from localStorage:', error);
  }
  return new Set();
};

const saveJoinedContestsToStorage = (contestIds: Set<number>): void => {
  try {
    localStorage.setItem('joinedContests', JSON.stringify(Array.from(contestIds)));
  } catch (error) {
    console.warn('Failed to save joined contests to localStorage:', error);
  }
};

export const useStore = create<AppState>((set, get) => ({
  user: null,
  currentContest: null,
  currentProblem: null,
  selectedLanguage: 'JAVA',
  isDarkMode: localStorage.getItem('darkMode') === 'true' || 
              (!localStorage.getItem('darkMode') && window.matchMedia('(prefers-color-scheme: dark)').matches),
  joinedContests: getJoinedContestsFromStorage(),
  
  setUser: (user) => set({ user }),
  setCurrentContest: (contest) => set({ currentContest: contest }),
  setCurrentProblem: (problem) => set({ currentProblem: problem }),
  setSelectedLanguage: (language) => set({ selectedLanguage: language }),
  toggleDarkMode: () => set((state) => {
    const newMode = !state.isDarkMode;
    localStorage.setItem('darkMode', String(newMode));
    if (newMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    return { isDarkMode: newMode };
  }),
  joinContest: (contestId) => set((state) => {
    const newJoinedContests = new Set(state.joinedContests);
    newJoinedContests.add(contestId);
    saveJoinedContestsToStorage(newJoinedContests);
    return { joinedContests: newJoinedContests };
  }),
  isContestJoined: (contestId) => get().joinedContests.has(contestId),
  clearJoinedContests: () => set(() => {
    const emptySet = new Set<number>();
    saveJoinedContestsToStorage(emptySet);
    return { joinedContests: emptySet };
  }),
}));