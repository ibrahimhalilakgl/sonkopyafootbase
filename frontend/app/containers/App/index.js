import React from 'react';
import { PropTypes } from 'prop-types';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Application from './Application';
import ThemeWrapper from './ThemeWrapper';
import {
  NotFoundDedicated,
} from '../pageListAsync';

window.__MUI_USE_NEXT_TYPOGRAPHY_VARIANTS__ = true;

function App(props) {
  const { history } = props;
  return (
    <ThemeWrapper>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/app/matches" replace />} />
          <Route path="login" element={<Navigate to="/app/login" replace />} />
          <Route path="register" element={<Navigate to="/app/register" replace />} />
          <Route path="app/*" element={<Application history={history} />} />
          <Route path="*" element={<NotFoundDedicated />} />
        </Routes>
      </BrowserRouter>
    </ThemeWrapper>
  );
}

App.propTypes = {
  history: PropTypes.object.isRequired,
};

export default App;
