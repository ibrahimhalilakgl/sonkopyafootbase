import request from './request';
import { API_BASE_URL, API_ENDPOINTS } from './constants';

// Auth API
export const authAPI = {
  login: (email, password) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.AUTH.LOGIN}`, {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  register: (data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.AUTH.REGISTER}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  resetPassword: (email, newPassword) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.AUTH.RESET_PASSWORD}`, {
      method: 'POST',
      body: JSON.stringify({ email, newPassword }),
    }),
};

// Matches API
export const matchesAPI = {
  list: (params = {}) => {
    const queryString = new URLSearchParams(params).toString();
    const url = `${API_BASE_URL}${API_ENDPOINTS.MATCHES.LIST}${queryString ? `?${queryString}` : ''}`;
    return request(url);
  },

  get: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.GET(id)}`),

  predict: (matchId, homeScore, awayScore) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.PREDICT(matchId)}`, {
      method: 'POST',
      body: JSON.stringify({ homeScore, awayScore }),
    }),

  addComment: (matchId, message) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.COMMENT(matchId)}`, {
      method: 'POST',
      body: JSON.stringify({ message }),
    }),

  updateComment: (commentId, message) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.DELETE_COMMENT(commentId)}`, {
      method: 'PUT',
      body: JSON.stringify({ message }),
    }),

  deleteComment: (commentId) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.DELETE_COMMENT(commentId)}`, {
      method: 'DELETE',
    }),

  likeComment: (commentId) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.LIKE_COMMENT(commentId)}`, {
      method: 'POST',
    }),

  getTeams: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.TEAMS(id)}`),

  getEvents: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.EVENTS(id)}`),

  getMedia: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.MEDIA(id)}`),

  getStatusHistory: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.STATUS_HISTORY(id)}`),

  addEvent: (matchId, data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.EVENTS(matchId)}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  addMedia: (matchId, data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.MATCHES.MEDIA(matchId)}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),
};

// Players API
export const playersAPI = {
  list: () => request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.LIST}`),

  get: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.GET(id)}`),

  getRatings: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.RATINGS(id)}`),

  rate: (playerId, score, comment) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.RATE(playerId)}`, {
      method: 'POST',
      body: JSON.stringify({ score, comment }),
    }),

  getScore: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.SCORE(id)}`),

  getMedia: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.MEDIA(id)}`),

  getStatistics: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.STATISTICS(id)}`),

  getComments: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.COMMENTS(id)}`),

  addComment: (playerId, comment) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.COMMENTS(playerId)}`, {
      method: 'POST',
      body: JSON.stringify({ comment }),
    }),

  addMedia: (playerId, data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.PLAYERS.MEDIA(playerId)}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),
};

// Users API
export const usersAPI = {
  getProfile: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.USERS.PROFILE(id)}`),
  me: () => request(`${API_BASE_URL}${API_ENDPOINTS.USERS.ME}`),

  follow: (id) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.USERS.FOLLOW(id)}`, {
      method: 'POST',
    }),

  unfollow: (id) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.USERS.UNFOLLOW(id)}`, {
      method: 'DELETE',
    }),
};

// Teams API
export const teamsAPI = {
  list: () => request(`${API_BASE_URL}${API_ENDPOINTS.TEAMS.LIST}`),
  get: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.TEAMS.GET(id)}`),
  getPlayers: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.TEAMS.PLAYERS(id)}`),
  getMatches: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.TEAMS.MATCHES(id)}`),
  getStatistics: (id) => request(`${API_BASE_URL}${API_ENDPOINTS.TEAMS.STATISTICS(id)}`),
};

// Feed API
export const feedAPI = {
  get: () => request(`${API_BASE_URL}${API_ENDPOINTS.FEED.GET}`),
};

// Editor API
export const editorAPI = {
  createMatch: (matchData) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.EDITOR.CREATE_MATCH}`, {
      method: 'POST',
      body: JSON.stringify(matchData),
    }),
  
  getMyMatches: () =>
    request(`${API_BASE_URL}${API_ENDPOINTS.EDITOR.MY_MATCHES}`),
  
  updateScore: (matchId, scoreData) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.EDITOR.UPDATE_SCORE(matchId)}`, {
      method: 'PUT',
      body: JSON.stringify(scoreData),
    }),
  
  addMatchEvent: (matchId, eventData) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.EDITOR.ADD_EVENT(matchId)}`, {
      method: 'POST',
      body: JSON.stringify(eventData),
    }),
  
  startMatch: (matchId) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.EDITOR.START_MATCH(matchId)}`, {
      method: 'POST',
    }),
  
  finishMatch: (matchId) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.EDITOR.FINISH_MATCH(matchId)}`, {
      method: 'POST',
    }),

  // Command Pattern - Skor yönetimi (geri alınabilir)
  updateMatchScoreCommand: (scoreData) =>
    request(`${API_BASE_URL}/editor/matches/score-command`, {
      method: 'POST',
      body: JSON.stringify(scoreData),
    }),

  finishMatchCommand: (finishData) =>
    request(`${API_BASE_URL}/editor/matches/finish-command`, {
      method: 'POST',
      body: JSON.stringify(finishData),
    }),

  undoLastCommand: () =>
    request(`${API_BASE_URL}/editor/matches/undo`, {
      method: 'POST',
    }),

  getCommandHistory: () =>
    request(`${API_BASE_URL}/editor/matches/history`),
};

// Admin API
export const adminAPI = {
  // Maç onay işlemleri
  getPendingMatches: () =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.PENDING_MATCHES}`),

  approveMatch: (matchId) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.APPROVE_MATCH(matchId)}`, {
      method: 'POST',
    }),

  rejectMatch: (matchId) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.REJECT_MATCH(matchId)}`, {
      method: 'POST',
    }),

  // Maç işlemleri
  createMatch: (data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.MATCHES}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  updateMatch: (id, data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.MATCH(id)}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // Command Pattern - Skor yönetimi (geri alınabilir)
  updateMatchScore: (scoreData) =>
    request(`${API_BASE_URL}/admin/matches/score`, {
      method: 'POST',
      body: JSON.stringify(scoreData),
    }),

  finishMatch: (finishData) =>
    request(`${API_BASE_URL}/admin/matches/finish`, {
      method: 'POST',
      body: JSON.stringify(finishData),
    }),

  undoLastCommand: () =>
    request(`${API_BASE_URL}/admin/matches/undo`, {
      method: 'POST',
    }),

  getCommandHistory: () =>
    request(`${API_BASE_URL}/admin/matches/history`),

  // Takım işlemleri
  createTeam: (data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.TEAMS}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  updateTeam: (id, data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.TEAM(id)}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // Oyuncu işlemleri
  createPlayer: (data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.PLAYERS}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  updatePlayer: (id, data) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.ADMIN.PLAYER(id)}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
};

// Home API
export const homeAPI = {
  get: () => request(`${API_BASE_URL}/home`),
};

// Notifications API
export const notificationsAPI = {
  getAll: () => request(`${API_BASE_URL}${API_ENDPOINTS.NOTIFICATIONS.LIST}`),
  
  getUnread: () => request(`${API_BASE_URL}${API_ENDPOINTS.NOTIFICATIONS.UNREAD}`),
  
  getUnreadCount: () => request(`${API_BASE_URL}${API_ENDPOINTS.NOTIFICATIONS.UNREAD_COUNT}`),
  
  getRecent: (limit = 10) => 
    request(`${API_BASE_URL}${API_ENDPOINTS.NOTIFICATIONS.RECENT}?limit=${limit}`),
  
  markAsRead: (id) => 
    request(`${API_BASE_URL}${API_ENDPOINTS.NOTIFICATIONS.MARK_READ(id)}`, {
      method: 'PUT',
    }),
  
  markAllAsRead: () =>
    request(`${API_BASE_URL}${API_ENDPOINTS.NOTIFICATIONS.MARK_ALL_READ}`, {
      method: 'PUT',
    }),
  
  deleteNotification: (id) =>
    request(`${API_BASE_URL}${API_ENDPOINTS.NOTIFICATIONS.DELETE(id)}`, {
      method: 'DELETE',
    }),
};
