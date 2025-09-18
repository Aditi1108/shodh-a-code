# Shodh-a-Code Frontend

React-based user interface for the competitive programming contest platform.

## 🎨 Tech Stack

- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **Code Editor**: Native HTML textarea
- **HTTP Client**: Native Fetch API
- **Routing**: React Router v6
- **Icons**: Lucide React

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/        # Reusable UI components
│   │   ├── Header.tsx     # Navigation header
│   │   ├── Layout.tsx     # Page layout wrapper
│   │   └── Leaderboard.tsx # Leaderboard display
│   ├── pages/             # Page components
│   │   ├── Contest.tsx    # Contest details & problems
│   │   ├── ContestList.tsx # All contests listing
│   │   ├── Home.tsx       # Dashboard
│   │   ├── Login.tsx      # User login
│   │   └── Problem.tsx    # Problem solving interface
│   ├── services/          # API service layer
│   │   └── api.ts         # Backend API calls
│   ├── store/             # Global state management
│   │   └── useStore.ts    # Zustand store
│   └── types/             # TypeScript definitions
│       └── index.ts       # Shared types
├── public/                # Static assets
├── package.json
├── tailwind.config.js     # Tailwind configuration
├── tsconfig.json          # TypeScript configuration
└── vite.config.ts         # Vite configuration
```

## 🚀 Key Features

### User Interface
- **Responsive Design**: Mobile-friendly layout
- **Real-time Updates**: Live submission status and leaderboard
- **Clean Editor**: Simple textarea-based code editor
- **Tab Navigation**: Problems, Leaderboard, Your Submissions views

### Code Editor
- **Multi-language Support**: Java, Python3, C++, JavaScript
- **Simple Textarea**: Plain text editor with monospace font
- **Templates**: Pre-loaded language templates with helpful comments

### Contest Features
- **Contest Listing**: View all active contests
- **Problem List**: Problems shown within contest page
- **Submission History**: Track all your attempts
- **Live Leaderboard**: Auto-refreshes every 15 seconds when viewing

## 🔧 Configuration

### Environment Setup
The application runs on port 5177 by default:
```bash
npm run dev -- --port 5177
```

### API Configuration
Backend API URL is configured in `services/api.ts`:
```typescript
const API_BASE_URL = 'http://localhost:8080/api'
```

## 🏃 Running the Frontend

### Prerequisites
- Node.js 18+
- npm or yarn

### Installation
```bash
cd frontend
npm install
```

### Development Server
```bash
npm run dev
```
Access at: `http://localhost:5177`

### Production Build
```bash
npm run build
npm run preview
```

## 🎯 Core Components

### Pages

#### Login (`/login`)
- Simple username-based authentication
- Creates new user if doesn't exist
- Stores user in localStorage

#### Home (`/`)
- Dashboard with user statistics
- Active contests display
- Quick contest join functionality

#### Contest (`/contest/:id`)
- Three-tab interface: Problems, Leaderboard, Submissions
- Join contest button for new participants
- Real-time status updates

#### Problem (`/problem/:id`)
- Plain text code editor with monospace font
- Run Code (test) vs Submit (final)
- Live result display with test case details

### State Management

Using Zustand for global state:
```typescript
{
  user: User | null
  selectedLanguage: ProgrammingLanguage
  joinedContests: Set<number>
}
```

### API Integration

Key API services:
- `contestApi`: Contest CRUD and leaderboard
- `submissionApi`: Code submission and status
- `userApi`: User registration and details

## 📊 Data Flow

1. **User Login** → Store in Zustand + localStorage
2. **Contest Join** → Backend validation → Update local state
3. **Code Submission**:
   - Submit to backend
   - Receive submission ID
   - Poll status every 2 seconds
   - Display results
4. **Leaderboard**:
   - Load on tab switch
   - Auto-refresh every 15 seconds when viewing

## 🎨 Styling

### Tailwind CSS Classes
- **Cards**: `bg-white rounded-2xl shadow-lg`
- **Buttons**: `bg-gradient-to-r from-X to-Y`
- **Hover Effects**: `hover:scale-105 transition-transform`

### Color Palette
- Primary: Violet/Purple gradients
- Success: Green shades
- Error: Red shades
- Info: Blue shades

## 🔄 Polling Mechanisms

### Submission Status
```typescript
// Polls every 2 seconds until final status
useEffect(() => {
  const interval = setInterval(() => {
    fetchSubmissionStatus(submissionId)
  }, 2000)
  return () => clearInterval(interval)
}, [submissionId])
```

### Leaderboard Updates
```typescript
// Refreshes every 15 seconds when tab is active
useEffect(() => {
  if (activeTab === 'leaderboard') {
    const interval = setInterval(() => {
      loadLeaderboard(contestId)
    }, 15000)
    return () => clearInterval(interval)
  }
}, [activeTab])
```

## 📱 Responsive Design

- **Mobile**: Single column layout
- **Tablet**: Two column grid
- **Desktop**: Full multi-column layout

## 🧪 Testing

### Manual Testing
1. Login with username
2. Join a contest
3. Submit code solution
4. Monitor status updates
5. Check leaderboard

### Component Testing
```bash
npm run test  # If tests are configured
```

## 📈 Performance Optimizations

- **Route-based Code Splitting**: If implemented
- **Proper Cleanup**: Intervals and timeouts cleaned on unmount
- **Efficient Polling**: Only active when needed (leaderboard on tab view)

## 🔒 Security

- **Input Sanitization**: User inputs validated
- **XSS Prevention**: React's built-in protection
- **CORS**: Configured for backend API only
- **localStorage**: Only non-sensitive data

## 🚀 Deployment

### Build for Production
```bash
npm run build
```

### Serve Static Files
The `dist` folder can be served by any static file server:
- Nginx
- Apache
- Vercel
- Netlify

## 🔄 Future Enhancements

1. **WebSocket Integration**: Replace polling with real-time updates
2. **PWA Support**: Offline capabilities
3. **Theme Customization**: User-selectable themes
4. **Code Sharing**: Share solutions with others
5. **Analytics**: Track user progress

---

Built for the Shodh AI Full Stack Engineer Assessment