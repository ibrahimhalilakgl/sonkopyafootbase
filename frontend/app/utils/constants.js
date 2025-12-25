export const RESTART_ON_REMOUNT = '@@saga-injector/restart-on-remount';
export const DAEMON = '@@saga-injector/daemon';
export const ONCE_TILL_UNMOUNT = '@@saga-injector/once-till-unmount';

// API Configuration
export const API_BASE_URL = process.env.API_URL || 'http://localhost:8080/api';
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    RESET_PASSWORD: '/auth/reset-password',
  },
  MATCHES: {
    LIST: '/matches',
    GET: (id) => `/matches/${id}`,
    PREDICT: (id) => `/matches/${id}/predictions`,
    COMMENT: (id) => `/matches/${id}/comments`,
    DELETE_COMMENT: (id) => `/matches/comments/${id}`,
    LIKE_COMMENT: (id) => `/matches/comments/${id}/like`,
    SEARCH: '/matches',
    TEAMS: (id) => `/matches/${id}/teams`,
    EVENTS: (id) => `/matches/${id}/events`,
    MEDIA: (id) => `/matches/${id}/media`,
    STATUS_HISTORY: (id) => `/matches/${id}/status-history`,
  },
  PLAYERS: {
    LIST: '/players',
    GET: (id) => `/players/${id}`,
    RATINGS: (id) => `/players/${id}/ratings`,
    RATE: (id) => `/players/${id}/ratings`,
    SCORE: (id) => `/players/${id}/score`,
    MEDIA: (id) => `/players/${id}/media`,
    STATISTICS: (id) => `/players/${id}/statistics`,
    COMMENTS: (id) => `/players/${id}/comments`,
  },
  TEAMS: {
    LIST: '/teams',
    GET: (id) => `/teams/${id}`,
    PLAYERS: (id) => `/teams/${id}/players`,
    MATCHES: (id) => `/teams/${id}/matches`,
    STATISTICS: (id) => `/teams/${id}/statistics`,
  },
  USERS: {
    PROFILE: (id) => `/users/${id}`,
    ME: '/users/me',
    FOLLOW: (id) => `/users/${id}/follow`,
    UNFOLLOW: (id) => `/users/${id}/follow`,
  },
  FEED: {
    GET: '/feed',
  },
  ADMIN: {
    MATCHES: '/admin/matches',
    MATCH: (id) => `/admin/matches/${id}`,
    TEAMS: '/admin/teams',
    TEAM: (id) => `/admin/teams/${id}`,
    PLAYERS: '/admin/players',
    PLAYER: (id) => `/admin/players/${id}`,
    PENDING_MATCHES: '/admin/matches/pending',
    APPROVE_MATCH: (id) => `/admin/matches/${id}/approve`,
    REJECT_MATCH: (id) => `/admin/matches/${id}/reject`,
  },
  EDITOR: {
    CREATE_MATCH: '/editor/matches',
    MY_MATCHES: '/editor/matches/my-matches',
    UPDATE_SCORE: (id) => `/editor/matches/${id}/score`,
    ADD_EVENT: (id) => `/editor/matches/${id}/events`,
    START_MATCH: (id) => `/editor/matches/${id}/start`,
    FINISH_MATCH: (id) => `/editor/matches/${id}/finish`,
  },
  NOTIFICATIONS: {
    LIST: '/notifications',
    UNREAD: '/notifications/unread',
    UNREAD_COUNT: '/notifications/unread/count',
    RECENT: '/notifications/recent',
    MARK_READ: (id) => `/notifications/${id}/read`,
    MARK_ALL_READ: '/notifications/read-all',
    DELETE: (id) => `/notifications/${id}`,
  },
};
