import React, { Fragment } from 'react';
import { PropTypes } from 'prop-types';

import Fade from '@mui/material/Fade';
import Typography from '@mui/material/Typography';
import { HeaderMenu, BreadCrumb } from 'dan-components';
import dataMenuModule from 'dan-api/ui/menu';
import Decoration from '../Decoration';
import useStyles from '../appStyles-jss';
import AppFooter from 'components/Footer/AppFooter';

// Dinamik menu için
const getDataMenu = () => {
  if (typeof dataMenuModule.getMenu === 'function') {
    return dataMenuModule.getMenu();
  }
  return dataMenuModule;
};

function DropMenuLayout(props) {
  // Her render'da fresh menu al (localStorage'dan okuyor)
  const dataMenu = getDataMenu();
  const { classes, cx } = useStyles();
  const {
    children,
    pageLoaded,
    mode,
    gradient,
    deco,
    bgPosition,
    changeMode,
    place,
    history,
    titleException,
    toggleDrawer,
    sidebarOpen,
    loadTransition
  } = props;
  return (
    <Fragment>
      <HeaderMenu
        type="top-navigation"
        dataMenu={dataMenu}
        changeMode={changeMode}
        mode={mode}
        history={history}
        toggleDrawerOpen={toggleDrawer}
        openMobileNav={sidebarOpen}
        loadTransition={loadTransition}
        logoLink="/app"
      />
      <main
        className={
          cx(
            classes.content,
            classes.highMargin
          )
        }
        id="mainContent"
        style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}
      >
        <Decoration
          mode={mode}
          gradient={gradient}
          decoration={deco}
          bgPosition={bgPosition}
          horizontalMenu
        />
        <section
          className={cx(classes.mainWrap, classes.topbarLayout)}
          style={{ flex: 1, display: 'flex', flexDirection: 'column' }}
        >
          {titleException.indexOf(history.location.pathname) < 0 && (
            <div className={classes.pageTitle}>
              <Typography component="h4" className={bgPosition === 'header' ? classes.darkTitle : classes.lightTitle} variant="h4">{place}</Typography>
              <BreadCrumb separator=" / " theme={bgPosition === 'header' ? 'dark' : 'light'} location={history.location} />
            </div>
          )}
          {!pageLoaded && (<img src="/images/spinner.gif" alt="spinner" className={classes.circularProgress} />)}
          <Fade
            in={pageLoaded}
            {...(pageLoaded ? { timeout: 700 } : {})}
          >
            <div className={!pageLoaded ? classes.hideApp : ''} style={{ flex: 1 }}>
              {/* Application content will load here */}
              { children }
            </div>
          </Fade>
          <AppFooter />
        </section>
      </main>
    </Fragment>
  );
}

DropMenuLayout.propTypes = {

  children: PropTypes.node.isRequired,
  history: PropTypes.object.isRequired,
  changeMode: PropTypes.func.isRequired,
  toggleDrawer: PropTypes.func.isRequired,
  loadTransition: PropTypes.func.isRequired,
  sidebarOpen: PropTypes.bool.isRequired,
  pageLoaded: PropTypes.bool.isRequired,
  mode: PropTypes.string.isRequired,
  gradient: PropTypes.bool.isRequired,
  deco: PropTypes.bool.isRequired,
  bgPosition: PropTypes.string.isRequired,
  place: PropTypes.string.isRequired,
  titleException: PropTypes.array.isRequired,
};

export default DropMenuLayout;
