/**
 * CampusConnect Global Application Utilities
 * Modals, Toasts, Drawer navigation, and Shared UI Helpers
 */

const App = {
  init() {
    this.createToastContainer();
    this.bindMobileNav();
    this.bindNotificationBell();
  },

  createToastContainer() {
    if (!document.getElementById('toastContainer')) {
      const container = document.createElement('div');
      container.id = 'toastContainer';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }
  },

  showToast(message, type = 'info') {
    this.createToastContainer();
    const container = document.getElementById('toastContainer');

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
      <div style="font-weight: 700;">${type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ'}</div>
      <div style="font-size: 0.9rem;">${message}</div>
    `;

    container.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  },

  showLoading(text = 'Loading...') {
    let loader = document.getElementById('globalLoader');
    if (!loader) {
      loader = document.createElement('div');
      loader.id = 'globalLoader';
      loader.style.cssText = `
        position: fixed; inset: 0; background: rgba(20,43,74,0.4);
        backdrop-filter: blur(3px); z-index: 2000; display: flex;
        align-items: center; justify-content: center; color: white;
        font-family: inherit; font-weight: 600; font-size: 1.1rem;
      `;
      document.body.appendChild(loader);
    }
    loader.innerHTML = `
      <div style="background: white; color: #142B4A; padding: 20px 32px; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.15); display: flex; align-items: center; gap: 14px;">
        <div class="spinner" style="width: 22px; height: 22px; border: 3px solid #E2E8F0; border-top-color: #3B82F6; border-radius: 50%; animation: spin 0.8s linear infinite;"></div>
        <div>${text}</div>
      </div>
      <style>@keyframes spin { to { transform: rotate(360deg); } }</style>
    `;
    loader.style.display = 'flex';
  },

  hideLoading() {
    const loader = document.getElementById('globalLoader');
    if (loader) loader.style.display = 'none';
  },

  openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('active');
  },

  closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
  },

  bindMobileNav() {
    const mobileBtn = document.querySelector('.mobile-menu-btn');
    const sidebar = document.querySelector('.sidebar');
    if (mobileBtn && sidebar) {
      mobileBtn.addEventListener('click', () => {
        sidebar.classList.toggle('open');
      });
    }
  },

  async bindNotificationBell() {
    const bellBtn = document.getElementById('notificationBellBtn');
    if (!bellBtn) return;

    // Fetch unread count
    try {
      const resp = await API.get('/notifications/unread-count');
      if (resp && resp.data) {
        const count = resp.data.count || 0;
        const badge = document.getElementById('notifCountBadge');
        if (badge) {
          badge.textContent = count;
          badge.style.display = count > 0 ? 'inline-flex' : 'none';
        }
      }
    } catch (e) {
      // Ignored
    }

    bellBtn.addEventListener('click', async () => {
      this.openModal('notificationsModal');
      this.loadNotificationsList();
    });
  },

  async loadNotificationsList() {
    const container = document.getElementById('notificationsModalList');
    if (!container) return;

    container.innerHTML = '<div style="text-align: center; padding: 20px;">Loading notifications...</div>';

    const resp = await API.get('/notifications');
    if (resp && resp.data && resp.data.length > 0) {
      container.innerHTML = resp.data.map(n => `
        <div style="padding: 14px; border-bottom: 1px solid #E2E8F0; background: ${n.isRead ? 'transparent' : '#EFF6FF'}; border-radius: 8px; margin-bottom: 6px;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
            <div style="font-weight: 700; color: #142B4A; font-size: 0.95rem;">${n.title}</div>
            <div style="font-size: 0.75rem; color: #64748B;">${new Date(n.createdAt).toLocaleDateString()}</div>
          </div>
          <div style="font-size: 0.85rem; color: #475569; line-height: 1.4;">${n.message}</div>
        </div>
      `).join('');
    } else {
      container.innerHTML = '<div style="text-align: center; color: #94A3B8; padding: 30px;">No notifications yet.</div>';
    }
  }
};

document.addEventListener('DOMContentLoaded', () => App.init());
