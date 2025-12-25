import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import { NavLink } from 'react-router-dom';
import Button from '@mui/material/Button';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Avatar from '@mui/material/Avatar';
import Badge from '@mui/material/Badge';
import IconButton from '@mui/material/IconButton';
import NotificationsIcon from '@mui/icons-material/Notifications';
import Divider from '@mui/material/Divider';
import brand from 'dan-api/dummy/brand';
import dummy from 'dan-api/dummy/dummyContents';
const logo = '/logo.png';
import MainMenu from './MainMenu';
import { notificationsAPI } from 'utils/api';
import useStyles from './sidebar-jss';

function SidebarContent(props) {
  const { classes, cx } = useStyles();
  const [transform, setTransform] = useState(0);
  const [okunmamisSayi, setOkunmamisSayi] = useState(0);

  const handleScroll = (event) => {
    const scroll = event.target.scrollTop;
    setTransform(scroll);
  };

  // Bildirim sayısını yükle
  const loadOkunmamisSayi = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;
      
      const data = await notificationsAPI.getUnreadCount();
      setOkunmamisSayi(data.count || 0);
    } catch (error) {
      console.error('Bildirim sayısı yüklenemedi:', error);
    }
  };

  useEffect(() => {
    const mainContent = document.getElementById('sidebar');
    mainContent.addEventListener('scroll', handleScroll);
    
    // Bildirim sayısını yükle
    const token = localStorage.getItem('token');
    if (token) {
      loadOkunmamisSayi();
      
      // Her 30 saniyede bir yenile
      const interval = setInterval(loadOkunmamisSayi, 30000);
      return () => {
        window.removeEventListener('scroll', handleScroll);
        clearInterval(interval);
      };
    }
    
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  const {
    turnDarker,
    drawerPaper,
    toggleDrawerOpen,
    loadTransition,
    leftSidebar,
    dataMenu,
    status,
    anchorEl,
    openMenuStatus,
    closeMenuStatus,
    changeStatus,
    isLogin
  } = props;

  const setStatus = st => {
    switch (st) {
      case 'online':
        return classes.online;
      case 'idle':
        return classes.idle;
      case 'bussy':
        return classes.bussy;
      default:
        return classes.offline;
    }
  };

  return (
    <div className={cx(classes.drawerInner, !drawerPaper ? classes.drawerPaperClose : '')}>
      <div className={classes.drawerHeader}>
        <NavLink to="/app" className={cx(classes.brand, classes.brandBar, turnDarker && classes.darker)}>
          <img src={logo} alt={brand.name} style={{ height: 48, width: 'auto' }} />
          {brand.name}
        </NavLink>
        {isLogin ? (
          <div
            className={cx(classes.profile, classes.user)}
            style={{ opacity: 1 - (transform / 100), marginTop: transform * -0.3 }}
          >
            <Avatar
              alt={dummy.user.name}
              src={dummy.user.avatar}
              className={cx(classes.avatar, classes.bigAvatar)}
            />
            <div>
              <h4>{dummy.user.name || 'Kullanıcı'}</h4>
              <Button size="small" onClick={openMenuStatus}>
                <i className={cx(classes.dotStatus, setStatus(status))} />
                {status === 'online' ? 'Çevrimiçi' : status === 'idle' ? 'Boşta' : status === 'bussy' ? 'Meşgul' : 'Çevrimdışı'}
              </Button>
              <Menu
                id="status-menu"
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={closeMenuStatus}
                className={classes.statusMenu}
              >
                <MenuItem onClick={() => changeStatus('online')}>
                  <i className={cx(classes.dotStatus, classes.online)} />
                  Çevrimiçi
                </MenuItem>
                <MenuItem onClick={() => changeStatus('idle')}>
                  <i className={cx(classes.dotStatus, classes.idle)} />
                  Boşta
                </MenuItem>
                <MenuItem onClick={() => changeStatus('bussy')}>
                  <i className={cx(classes.dotStatus, classes.bussy)} />
                  Meşgul
                </MenuItem>
                <MenuItem onClick={() => changeStatus('offline')}>
                  <i className={cx(classes.dotStatus, classes.offline)} />
                  Çevrimdışı
                </MenuItem>
              </Menu>
            </div>
          </div>
        ) : null}
        
        {/* Bildirim Kutusu - Her zaman göster, token varsa badge ile */}
        <div style={{ 
          padding: '12px 16px',
          backgroundColor: 'rgba(255,255,255,0.05)',
          borderTop: '1px solid rgba(255,255,255,0.1)',
          borderBottom: '1px solid rgba(255,255,255,0.1)',
        }}>
          <Button
            fullWidth
            variant="contained"
            color="primary"
            startIcon={
              <Badge badgeContent={okunmamisSayi} color="error">
                <NotificationsIcon />
              </Badge>
            }
            component={NavLink}
            to="/app/notifications"
            sx={{
              justifyContent: 'flex-start',
              padding: '10px 16px',
              textTransform: 'none',
              fontWeight: 'bold',
              boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
              '&:hover': {
                boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
              }
            }}
          >
            Bildirimler
            {okunmamisSayi > 0 && (
              <span style={{ marginLeft: 'auto', fontSize: '0.9em', opacity: 0.9 }}>
                ({okunmamisSayi} yeni)
              </span>
            )}
          </Button>
        </div>
      </div>
      <div
        id="sidebar"
        className={
          cx(
            classes.menuContainer,
            leftSidebar && classes.rounded,
            isLogin && classes.withProfile
          )
        }
      >
        <MainMenu loadTransition={loadTransition} dataMenu={dataMenu} toggleDrawerOpen={toggleDrawerOpen} />
      </div>
    </div>
  );
}

SidebarContent.propTypes = {
  drawerPaper: PropTypes.bool.isRequired,
  turnDarker: PropTypes.bool,
  toggleDrawerOpen: PropTypes.func,
  loadTransition: PropTypes.func,
  leftSidebar: PropTypes.bool.isRequired,
  dataMenu: PropTypes.array.isRequired,
  status: PropTypes.string.isRequired,
  anchorEl: PropTypes.object,
  openMenuStatus: PropTypes.func.isRequired,
  closeMenuStatus: PropTypes.func.isRequired,
  changeStatus: PropTypes.func.isRequired,
  isLogin: PropTypes.bool
};

SidebarContent.defaultProps = {
  turnDarker: false,
  toggleDrawerOpen: () => {},
  loadTransition: () => {},
  anchorEl: null,
  isLogin: false,
};

export default SidebarContent;
