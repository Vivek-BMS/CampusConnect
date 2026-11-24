/**
 * CampusConnect Authentication Management
 */

const Auth = {
  init() {
    this.bindEvents();
    this.checkProtectedPage();
    this.updateUserUI();
  },

  checkProtectedPage() {
    const path = window.location.pathname.toLowerCase();
    const isDashboard = path.includes('dashboard.html') || path.includes('report-lost.html') || path.includes('report-found.html');
    const isAdmin = path.includes('admin.html');

    const token = API.getToken();
    const user = API.getCurrentUser();

    if ((isDashboard || isAdmin) && !token) {
      window.location.href = 'login.html';
      return;
    }

    if (isAdmin && user && user.role !== 'ROLE_ADMIN' && user.role !== 'ROLE_SECURITY_STAFF') {
      alert('Access restricted to Campus Administrators and Security Staff.');
      window.location.href = 'dashboard.html';
    }
  },

  updateUserUI() {
    const user = API.getCurrentUser();
    if (!user) return;

    const nameElements = document.querySelectorAll('.current-user-name');
    nameElements.forEach(el => el.textContent = user.fullName);

    const roleElements = document.querySelectorAll('.current-user-role');
    roleElements.forEach(el => {
      let roleLabel = 'Student';
      if (user.role === 'ROLE_ADMIN') roleLabel = 'Administrator';
      if (user.role === 'ROLE_SECURITY_STAFF') roleLabel = 'Security Staff';
      el.textContent = `${roleLabel} • ${user.studentId || ''}`;
    });

    const avatarElements = document.querySelectorAll('.user-avatar');
    avatarElements.forEach(el => {
      el.textContent = user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    });
  },

  async handleLogin(email, password) {
    App.showLoading('Authenticating credentials...');
    try {
      const resp = await API.post('/auth/login', { email, password });
      App.hideLoading();

      if (resp && resp.success && resp.data) {
        API.setToken(resp.data.token);
        API.setCurrentUser(resp.data);
        App.showToast('Login successful! Redirecting...', 'success');

        setTimeout(() => {
          if (resp.data.role === 'ROLE_ADMIN' || resp.data.role === 'ROLE_SECURITY_STAFF') {
            window.location.href = 'admin.html';
          } else {
            window.location.href = 'dashboard.html';
          }
        }, 800);
      } else {
        App.showToast(resp ? resp.message : 'Invalid email or password', 'error');
      }
    } catch (e) {
      App.hideLoading();
      App.showToast('Network error during login', 'error');
    }
  },

  async handleRegister(formData) {
    if (formData.password !== formData.confirmPassword) {
      App.showToast('Passwords do not match', 'error');
      return;
    }

    App.showLoading('Registering new student account...');
    try {
      const resp = await API.post('/auth/register', formData);
      App.hideLoading();

      if (resp && resp.success && resp.data) {
        API.setToken(resp.data.token);
        API.setCurrentUser(resp.data);
        App.showToast('Account created successfully! Welcome to CampusConnect.', 'success');
        setTimeout(() => {
          window.location.href = 'dashboard.html';
        }, 900);
      } else {
        App.showToast(resp ? resp.message : 'Registration failed', 'error');
      }
    } catch (e) {
      App.hideLoading();
      App.showToast('Network error during registration', 'error');
    }
  },

  bindEvents() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
      loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;
        this.handleLogin(email, password);
      });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
      registerForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const formData = {
          fullName: document.getElementById('regFullName').value.trim(),
          studentId: document.getElementById('regStudentId').value.trim(),
          email: document.getElementById('regEmail').value.trim(),
          department: document.getElementById('regDepartment').value,
          password: document.getElementById('regPassword').value,
          confirmPassword: document.getElementById('regConfirmPassword').value,
          phone: document.getElementById('regPhone') ? document.getElementById('regPhone').value.trim() : null
        };
        this.handleRegister(formData);
      });
    }

    // Quick fill credentials for seamless portfolio demonstration!
    const quickFillBtns = document.querySelectorAll('[data-demo-login]');
    quickFillBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const role = btn.dataset.demoLogin;
        const emailInput = document.getElementById('loginEmail');
        const passInput = document.getElementById('loginPassword');

        if (role === 'student') {
          emailInput.value = 'rahul.cs@campus.edu';
          passInput.value = 'Student@123';
        } else if (role === 'admin') {
          emailInput.value = 'admin@campus.edu';
          passInput.value = 'Admin@123';
        } else if (role === 'security') {
          emailInput.value = 'security@campus.edu';
          passInput.value = 'Security@123';
        }
        App.showToast(`Populated ${role} demo credentials. Click Sign In!`, 'info');
      });
    });

    // Logout buttons
    const logoutBtns = document.querySelectorAll('.action-logout');
    logoutBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        if (confirm('Are you sure you want to log out of CampusConnect?')) {
          API.logout();
        }
      });
    });
  }
};

document.addEventListener('DOMContentLoaded', () => Auth.init());
