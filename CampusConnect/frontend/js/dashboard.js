/**
 * CampusConnect Student Dashboard Logic
 */

const Dashboard = {
  async init() {
    await this.loadStats();
    await this.loadPotentialMatches();
    await this.loadRecentReports();
  },

  async loadStats() {
    try {
      const resp = await API.get('/admin/stats');
      const stats = (resp && resp.data) ? resp.data : {
        totalLost: 14, totalFound: 18, potentialMatches: 9, recoveredItems: 11
      };

      const lostEl = document.getElementById('statTotalLost');
      if (lostEl) lostEl.textContent = stats.totalLost;

      const foundEl = document.getElementById('statTotalFound');
      if (foundEl) foundEl.textContent = stats.totalFound;

      const matchesEl = document.getElementById('statPotentialMatches');
      if (matchesEl) matchesEl.textContent = stats.potentialMatches;

      const recoveredEl = document.getElementById('statRecovered');
      if (recoveredEl) recoveredEl.textContent = stats.recoveredItems;
    } catch (e) {
      console.error('Error loading dashboard stats:', e);
    }
  },

  async loadPotentialMatches() {
    const container = document.getElementById('dashboardMatchesList');
    if (!container) return;

    try {
      const resp = await API.get('/matches/my-matches');
      const matches = (resp && resp.data && resp.data.length > 0) ? resp.data : [];

      if (matches.length === 0) {
        container.innerHTML = `
          <div style="text-align: center; padding: 30px; color: #94A3B8;">
            <div style="font-size: 2rem; margin-bottom: 8px;">🔍</div>
            <div style="font-weight: 600;">No active matches detected yet</div>
            <div style="font-size: 0.85rem;">Our Java smart matching algorithm continuously compares new lost and found reports.</div>
          </div>
        `;
        return;
      }

      container.innerHTML = matches.map(m => {
        let reasons = [];
        try {
          reasons = JSON.parse(m.matchReasons);
        } catch (e) {
          reasons = [m.matchReasons];
        }

        const scoreColor = m.similarityScore >= 80 ? '#10B981' : m.similarityScore >= 60 ? '#3B82F6' : '#F59E0B';

        return `
          <div style="background: white; border: 1px solid #E2E8F0; border-radius: 12px; padding: 20px; margin-bottom: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.03); display: flex; flex-direction: column; gap: 12px;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 10px;">
              <div>
                <span class="badge badge-found">Potential Match Found</span>
                <h4 style="color: #142B4A; margin-top: 6px; font-size: 1.1rem;">${m.foundItem.title}</h4>
                <div style="font-size: 0.85rem; color: #64748B; margin-top: 2px;">
                  Matches your lost item: <strong>${m.lostItem.title}</strong>
                </div>
              </div>
              <div style="text-align: right;">
                <div style="font-size: 1.4rem; font-weight: 800; color: ${scoreColor};">${m.similarityScore}%</div>
                <div style="font-size: 0.75rem; color: #64748B; font-weight: 600;">Similarity Score</div>
              </div>
            </div>

            <!-- Matching Reason Pills -->
            <div style="display: flex; flex-wrap: wrap; gap: 6px;">
              ${reasons.map(r => `<span class="reason-tag">${r}</span>`).join('')}
            </div>

            <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #F1F5F9; padding-top: 12px; margin-top: 4px;">
              <div style="font-size: 0.825rem; color: #64748B;">
                📍 ${m.foundItem.location} • 📅 ${m.foundItem.incidentDate}
              </div>
              <button class="btn btn-sm btn-accent" onclick="Dashboard.initiateClaim(${m.foundItem.id}, '${m.foundItem.title}')">
                Claim This Item
              </button>
            </div>
          </div>
        `;
      }).join('');
    } catch (e) {
      console.error('Error loading matches:', e);
    }
  },

  async loadRecentReports() {
    const container = document.getElementById('dashboardRecentReports');
    if (!container) return;

    try {
      const resp = await API.get('/items/browse');
      const items = (resp && resp.data) ? resp.data.slice(0, 6) : [];

      if (items.length === 0) {
        container.innerHTML = '<div style="text-align: center; padding: 24px; color: #94A3B8;">No recent items reported.</div>';
        return;
      }

      container.innerHTML = items.map(item => {
        const isLost = item.itemType === 'LOST';
        const typeBadge = isLost ? '<span class="badge badge-lost">LOST</span>' : '<span class="badge badge-found">FOUND</span>';
        let statusBadge = '<span class="badge badge-pending">OPEN</span>';
        if (item.status === 'RECOVERED') statusBadge = '<span class="badge badge-recovered">RECOVERED</span>';
        if (item.status === 'CLAIM_PENDING') statusBadge = '<span class="badge badge-pending">CLAIM PENDING</span>';

        return `
          <div class="item-card">
            <img src="${item.imageUrl || 'assets/placeholder.png'}" class="item-card-image" alt="${item.title}" onerror="this.src='https://placehold.co/400x240/142b4a/ffffff?text=${encodeURIComponent(item.title)}'">
            <div class="item-card-body">
              <div class="item-card-header">
                ${typeBadge}
                ${statusBadge}
              </div>
              <h4 class="item-card-title">${item.title}</h4>
              <div class="item-card-meta">
                <div class="meta-row">🏷️ <span>${item.category}</span></div>
                <div class="meta-row">📍 <span>${item.location}</span></div>
                <div class="meta-row">📅 <span>${item.incidentDate}</span></div>
              </div>
              <div class="item-card-footer">
                <button class="btn btn-sm btn-outline" onclick="Items.openItemModal(${item.id})">View Details</button>
                ${!isLost && item.status !== 'RECOVERED' ? `<button class="btn btn-sm btn-accent" onclick="Dashboard.initiateClaim(${item.id}, '${item.title}')">Claim</button>` : ''}
              </div>
            </div>
          </div>
        `;
      }).join('');
    } catch (e) {
      console.error('Error loading recent reports:', e);
    }
  },

  initiateClaim(itemId, itemTitle) {
    const claimItemIdInput = document.getElementById('claimItemId');
    const claimItemTitleEl = document.getElementById('claimItemTitleDisplay');
    if (claimItemIdInput) claimItemIdInput.value = itemId;
    if (claimItemTitleEl) claimItemTitleEl.textContent = itemTitle;
    App.openModal('claimModal');
  }
};

document.addEventListener('DOMContentLoaded', () => {
  if (document.getElementById('dashboardMatchesList')) {
    Dashboard.init();
  }
});
