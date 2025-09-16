import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

// Initialize dark mode on app load
const isDark = localStorage.getItem('darkMode') === 'true' || 
              (!localStorage.getItem('darkMode') && window.matchMedia('(prefers-color-scheme: dark)').matches);
if (isDark) {
  document.documentElement.classList.add('dark');
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)