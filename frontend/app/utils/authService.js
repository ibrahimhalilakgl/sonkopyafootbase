// Basit Singleton AuthService
class AuthService {
  static instance;

  constructor(api) {
    if (AuthService.instance) {
      return AuthService.instance;
    }
    this.api = api;
    AuthService.instance = this;
  }

  setToken(token) {
    if (!token) return;
    localStorage.setItem('authToken', token);
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  setUser(user) {
    if (!user) return;
    localStorage.setItem('user', JSON.stringify(user));
  }

  getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  getUserRole() {
    const user = this.getUser();
    return user?.rol || user?.role || null;
  }

  isAdmin() {
    return this.getUserRole() === 'ADMIN';
  }

  isEditor() {
    return this.getUserRole() === 'EDITOR';
  }

  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  async login(email, password) {
    const response = await this.api.login(email, password);
    this.setToken(response?.token);
    if (response?.kullanici) {
      this.setUser(response.kullanici);
    }
    return response;
  }

  async register(payload) {
    const response = await this.api.register(payload);
    this.setToken(response?.token);
    if (response?.kullanici) {
      this.setUser(response.kullanici);
    }
    return response;
  }
}

export default AuthService;

