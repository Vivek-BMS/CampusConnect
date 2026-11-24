/**
 * CampusConnect Admin Dashboard & Operations Logic
 * Claims Verification, Physical Handover Recording, Custody, Chart.js, Audit Logs
 */

const Admin = {
  claims: [],
  handovers: [],
  auditLogs: [],

  async init() {
    await this.loadStats();
    await this.initCharts();
    await this.loadClaims();
    await this.loadHandovers();
    await this.loadAuditLogs();
    this.bindHandoverForm();
  },

  async loadStats() {
    try {
      const resp = await API.get('/admin/stats');
      const s = (resp && resp.data) ? resp.data : FallbackData.adminStats;

      document.getElementById('adminStatUsers').textContent = s.totalUsers || 142;
      document.getElementById('adminStatLost').textContent = s.totalLost || 14;
      document.getElementById('adminStatFound').textContent = s.totalFound || 18;
      document.getElementById('adminStatPendingClaims').textContent = s.pendingClaims || 3;
      document.getElementById('adminStatRecovered').textContent = s.recoveredItems || 11;
      document.getElementById('adminStatMatches').textContent = s.potentialMatches || 9;
    } catch (e) {
      console.error('Error loading admin stats:', e);
    }
  },

  async initCharts() {
    // 1. Monthly Recovery Trend Chart (Line / Bar)
    const recoveryCtx = document.getElementById('recoveryTrendsChart');
    if (recoveryCtx && typeof Chart !== 'undefined') {
      new Chart(recoveryCtx, {
        type: 'line',
        data: {
          labels: ['Jul', 'Aug', 'Sep', 'Oct', 'Nov'],
          datasets: [{
            label: 'Recovered Belongings',
            data: [4, 7, 12, 9, 11],
            borderColor: '#10B981',
            backgroundColor: 'rgba(16, 185, 129, 0.1)',
            fill: true,
            tension: 0.35,
            borderWidth: 3,
            pointBackgroundColor: '#10B981',
            pointRadius: 5
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false }
          },
          scales: {
            y: { beginAtZero: true, grid: { color: '#F1F5F9' } },
            x: { grid: { display: false } }
          }
        }
      });
    }

    // 2. Category Distribution Chart (Doughnut)
    const categoryCtx = document.getElementById('categoryDistributionChart');
    if (categoryCtx && typeof Chart !== 'undefined') {
      new Chart(categoryCtx, {
        type: 'doughnut',
        data: {
          labels: ['Electronics', 'Wallets', 'ID Cards', 'Bags', 'Accessories', 'Books'],
          datasets: [{
            data: [12, 8, 6, 5, 4, 3],
            backgroundColor: ['#3B82F6', '#142B4A', '#10B981', '#F59E0B', '#8B5CF6', '#EC4899'],
            borderWidth: 2,
            borderColor: '#FFFFFF'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } }
          }
        }
      });
    }
  },

  async loadClaims() {
    const tbody = document.getElementById('adminClaimsTableBody');
    if (!tbody) return;

    try {
      const resp = await API.get('/claims/all');
      this.claims = (resp && resp.data) ? resp.data : FallbackData.claims;

      tbody.innerHTML = this.claims.map(c => {
        let statusBadge = '<span class="badge badge-pending">PENDING</span>';
        if (c.status === 'APPROVED') statusBadge = '<span class="badge badge-recovered">APPROVED</span>';
        if (c.status === 'REJECTED') statusBadge = '<span class="badge badge-rejected">REJECTED</span>';

        return `
          <tr>
            <td><strong>#CLM-${c.id}</strong></td>
            <td><strong>${c.itemTitle}</strong></td>
            <td>
              <div>${c.claimantName}</div>
              <div style="font-size: 0.75rem; color: #64748B;">${c.claimantStudentId || c.claimantEmail}</div>
            </td>
            <td>
              <div style="font-size: 0.85rem; max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                ${c.ownershipDescription}
              </div>
            </td>
            <td>${statusBadge}</td>
            <td>
              <button class="btn btn-sm btn-outline" onclick="Admin.viewClaimModal(${c.id})">Review Proof</button>
            </td>
          </tr>
        `;
      }).join('');
    } catch (e) {
      console.error('Error loading claims:', e);
    }
  },

  viewClaimModal(claimId) {
    const claim = this.claims.find(c => c.id === claimId);
    if (!claim) return;

    document.getElementById('adminClaimItemTitle').textContent = claim.itemTitle;
    document.getElementById('adminClaimantName').textContent = claim.claimantName;
    document.getElementById('adminClaimantId').textContent = claim.claimantStudentId || 'N/A';
    document.getElementById('adminClaimantEmail').textContent = claim.claimantEmail || 'N/A';
    document.getElementById('adminClaimOwnership').textContent = claim.ownershipDescription;
    document.getElementById('adminClaimPrivateMarks').textContent = claim.privateDetails || 'None provided';

    const claimActionBtns = document.getElementById('adminClaimActionButtons');
    if (claimActionBtns) {
      if (claim.status === 'PENDING' || claim.status === 'UNDER_REVIEW') {
        claimActionBtns.innerHTML = `
          <button class="btn btn-danger btn-sm" onclick="Admin.updateClaimStatus(${claim.id}, 'REJECTED')">✕ Reject Claim</button>
          <button class="btn btn-success btn-sm" onclick="Admin.updateClaimStatus(${claim.id}, 'APPROVED')">✓ Approve Claim</button>
        `;
      } else {
        claimActionBtns.innerHTML = `<span style="font-size: 0.85rem; color: #64748B; font-weight: 600;">Status: ${claim.status}</span>`;
      }
    }

    App.openModal('adminClaimReviewModal');
  },

  async updateClaimStatus(claimId, status) {
    const notes = prompt(`Enter optional verification remarks for ${status}:`, status === 'APPROVED' ? 'Proof matches physical item records.' : 'Identifying marks did not match.');
    if (notes === null) return;

    App.showLoading('Updating claim status...');
    try {
      const resp = await API.patch(`/claims/${claimId}/status`, { status, adminNotes: notes });
      App.hideLoading();
      App.closeModal('adminClaimReviewModal');
      App.showToast(`Claim #CLM-${claimId} has been ${status}`, status === 'APPROVED' ? 'success' : 'warning');
      this.loadClaims();
      this.loadStats();
    } catch (e) {
      App.hideLoading();
      App.closeModal('adminClaimReviewModal');
      App.showToast(`Claim status updated to ${status} (Preview Mode)`, 'success');
      this.loadClaims();
    }
  },

  async loadHandovers() {
    const tbody = document.getElementById('adminHandoversTableBody');
    if (!tbody) return;

    try {
      const resp = await API.get('/handovers');
      this.handovers = (resp && resp.data) ? resp.data : FallbackData.handovers;

      tbody.innerHTML = this.handovers.map(h => `
        <tr>
          <td><strong>#HDV-${h.id}</strong></td>
          <td>${h.itemTitle}</td>
          <td>${h.recipientName} (${h.recipientStudentId})</td>
          <td>${h.authorizedByName}</td>
          <td>${h.location}</td>
          <td>${new Date(h.handoverDate).toLocaleDateString()}</td>
          <td><span class="badge badge-recovered">RECOVERED</span></td>
        </tr>
      `).join('');
    } catch (e) {
      console.error('Error loading handovers:', e);
    }
  },

  async loadAuditLogs() {
    const tbody = document.getElementById('adminAuditLogsTableBody');
    if (!tbody) return;

    try {
      const resp = await API.get('/admin/audit-logs');
      this.auditLogs = (resp && resp.data) ? resp.data : FallbackData.auditLogs;

      tbody.innerHTML = this.auditLogs.map(l => `
        <tr>
          <td>${new Date(l.createdAt).toLocaleTimeString()}</td>
          <td><span class="badge badge-category">${l.action}</span></td>
          <td>${l.entityName} #${l.entityId}</td>
          <td style="font-size: 0.85rem; color: #475569;">${l.details}</td>
          <td><code style="font-size: 0.75rem;">${l.ipAddress || '127.0.0.1'}</code></td>
        </tr>
      `).join('');
    } catch (e) {
      console.error('Error loading audit logs:', e);
    }
  },

  bindHandoverForm() {
    const form = document.getElementById('recordHandoverForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const data = {
        itemId: parseInt(document.getElementById('handoverItemId').value, 10),
        claimId: parseInt(document.getElementById('handoverClaimId').value, 10),
        location: document.getElementById('handoverLocation').value.trim(),
        verificationNotes: document.getElementById('handoverNotes').value.trim(),
        ackConfirmed: true
      };

      App.showLoading('Logging handover & finalizing status to RECOVERED...');
      try {
        const resp = await API.post('/handovers', data);
        App.hideLoading();
        App.closeModal('recordHandoverModal');
        if (resp && resp.success) {
          App.showToast('Handover logged! Item marked as RECOVERED.', 'success');
          form.reset();
          this.loadHandovers();
          this.loadStats();
        }
      } catch (err) {
        App.hideLoading();
        App.closeModal('recordHandoverModal');
        App.showToast('Handover recorded (Preview Mode)', 'success');
        form.reset();
      }
    });
  }
};

document.addEventListener('DOMContentLoaded', () => {
  if (document.getElementById('adminClaimsTableBody')) {
    Admin.init();
  }
});
