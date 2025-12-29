import React, { useContext } from 'react';
import { PropTypes } from 'prop-types';
import { Routes, Route } from 'react-router-dom';
import { ThemeContext } from './ThemeWrapper';
import Dashboard from '../Templates/Dashboard';
import { NotFound } from '../pageListAsync';
import MatchesPage from '../Pages/MatchesPage';
import PlayersPage from '../Pages/PlayersPage';
import TeamsPage from '../Pages/TeamsPage';
import HomePage from '../Pages/HomePage';
import MatchDetailPage from '../Pages/MatchDetailPage';
import PlayerDetailPage from '../Pages/PlayerDetailPage';
import TeamDetailPage from '../Pages/TeamDetailPage';
import LoginPage from '../Pages/LoginPage';
import RegisterPage from '../Pages/RegisterPage';
import ProfilePage from '../Pages/ProfilePage';
import EditorMatchAddPage from '../Pages/EditorMatchAddPage';
import AdminMatchApprovalPage from '../Pages/AdminMatchApprovalPage';
import EditorMatchEventPage from '../Pages/EditorMatchEventPage';
import NotificationsPage from '../Pages/NotificationsPage';
import AdminMatchScoreManagementPage from '../Pages/AdminMatchScoreManagementPage';
import EditorMatchScoreManagementPage from '../Pages/EditorMatchScoreManagementPage';

function Application(props) {
  const { history } = props;
  const changeMode = useContext(ThemeContext);
  return (
    <Dashboard history={history} changeMode={changeMode}>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="matches" element={<MatchesPage />} />
        <Route path="matches/:id" element={<MatchDetailPage />} />
        <Route path="players" element={<PlayersPage />} />
        <Route path="players/:id" element={<PlayerDetailPage />} />
        <Route path="teams" element={<TeamsPage />} />
        <Route path="teams/:id" element={<TeamDetailPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route path="editor/match/add" element={<EditorMatchAddPage />} />
        <Route path="editor/match/:id/events" element={<EditorMatchEventPage />} />
        <Route path="editor/matches/score" element={<EditorMatchScoreManagementPage />} />
        <Route path="admin/matches/approval" element={<AdminMatchApprovalPage />} />
        <Route path="admin/matches/score" element={<AdminMatchScoreManagementPage />} />
        <Route path="notifications" element={<NotificationsPage />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Dashboard>
  );
}

Application.propTypes = {
  history: PropTypes.object.isRequired,
};

export default Application;
