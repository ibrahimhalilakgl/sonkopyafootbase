const isAuthed = () => {
  if (typeof window === 'undefined') return false;
  return Boolean(localStorage.getItem('authToken'));
};

const getUserRole = () => {
  if (typeof window === 'undefined') return null;
  const userStr = localStorage.getItem('user');
  if (!userStr) return null;
  try {
    const user = JSON.parse(userStr);
    const role = user?.rol || user?.role || null;
    // Rol'ü büyük harfe çevir (backend'den "EDITOR", "ADMIN", "USER" gelebilir, ama case-sensitive olabilir)
    return role ? role.toUpperCase() : null;
  } catch (e) {
    return null;
  }
};

const baseMenu = [
  {
    key: 'home',
    name: 'Ana Sayfa',
    link: '/app',
    icon: 'ion-ios-home'
  },
  {
    key: 'matches',
    name: 'Maçlar',
    link: '/app/matches',
    icon: 'ion-ios-football'
  },
  {
    key: 'players',
    name: 'Oyuncular',
    link: '/app/players',
    icon: 'ion-ios-people'
  },
  {
    key: 'teams',
    name: 'Takımlar',
    link: '/app/teams',
    icon: 'ion-ios-people'
  }
];

const authMenu = [
  {
    key: 'auth',
    name: 'Giriş / Kayıt',
    link: '/login',
    icon: 'ion-log-in'
  }
];

const profileMenu = [
  {
    key: 'profile',
    name: 'Profilim',
    link: '/app/profile',
    icon: 'ion-ios-person'
  }
];

const editorMenu = [
  {
    key: 'editor-match-add',
    name: 'Maç Ekle',
    link: '/app/editor/match/add',
    icon: 'ion-ios-add-circle'
  }
];

const adminMenu = [
  {
    key: 'admin-match-approval',
    name: 'Maç Onayları',
    link: '/app/admin/matches/approval',
    icon: 'ion-ios-checkmark-circle'
  }
];

const getMenuItems = () => {
  if (!isAuthed()) {
    return [...authMenu, ...baseMenu];
  }

  const role = getUserRole();
  let menuItems = [...baseMenu, ...profileMenu];

  // Rol kontrolü - case-insensitive
  if (role && (role === 'EDITOR' || role === 'editor')) {
    menuItems = [...menuItems, ...editorMenu];
  }

  if (role && (role === 'ADMIN' || role === 'admin')) {
    menuItems = [...menuItems, ...adminMenu];
  }

  return menuItems;
};

const getMenu = () => [
  {
    key: 'footbase',
    name: 'FootBase',
    icon: 'ion-ios-football',
    child: getMenuItems(),
  }
];

// Her çağrıldığında fresh menu döndürmek için function olarak export ediyoruz
// Backward compatibility için default export da mevcut
const menu = getMenu();

module.exports = menu;
module.exports.getMenu = getMenu;
module.exports.getMenuItems = getMenuItems;
module.exports.default = getMenu;
