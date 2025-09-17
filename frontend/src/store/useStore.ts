import { create } from 'zustand';
import type { User, ProgrammingLanguage } from '../types';

interface AppState {
  user: User | null;
  selectedLanguage: ProgrammingLanguage;
  joinedContests: Set<number>;

  setSelectedLanguage: (language: ProgrammingLanguage) => void;
  joinContest: (contestId: number) => void;
  isContestJoined: (contestId: number) => boolean;
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
  selectedLanguage: 'JAVA',
  joinedContests: getJoinedContestsFromStorage(),

  setSelectedLanguage: (language) => set({ selectedLanguage: language }),
  joinContest: (contestId) => set((state) => {
    const newJoinedContests = new Set(state.joinedContests);
    newJoinedContests.add(contestId);
    saveJoinedContestsToStorage(newJoinedContests);
    return { joinedContests: newJoinedContests };
  }),
  isContestJoined: (contestId) => get().joinedContests.has(contestId),
}));