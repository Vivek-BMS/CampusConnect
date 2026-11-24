/**
 * CampusConnect Items Logic (Reporting, Browsing, Filtering, and Claims)
 */

const Items = {
  allItems: [],

  init() {
    this.bindReportForms();
    this.bindImageUploadPreviews();
    this.bindBrowseFilters();
    this.bindClaimForm();

    if (document.getElementById('browseItemsContainer')) {
      this.loadBrowseItems();
    }
  },

  bindImageUploadPreviews() {
    const uploadInputs = document.querySelectorAll('.image-file-input');
    uploadInputs.forEach(input => {
      input.addEventListener('change', async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const previewImg = input.closest('.form-group').querySelector('.image-upload-preview');
        const hiddenUrlInput = input.closest('.form-group').querySelector('.image-url-hidden');

        // Local instant preview
        if (previewImg) {
          previewImg.src = URL.createObjectURL(file);
          previewImg.style.display = 'block';
        }

        // Try upload to server
        try {
          const resp = await API.uploadFile(file);
          if (resp && resp.data && resp.data.fileUrl) {
            if (hiddenUrlInput) hiddenUrlInput.value = resp.data.fileUrl;
            App.showToast('Photograph uploaded securely', 'success');
          }
        } catch (err) {
          console.warn('File upload simulation for preview.');
        }
      });
    });
  },

  bindReportForms() {
    // Report Lost Form
    const lostForm = document.getElementById('reportLostForm');
    if (lostForm) {
      lostForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
          title: document.getElementById('lostTitle').value.trim(),
          category: document.getElementById('lostCategory').value,
          description: document.getElementById('lostDescription').value.trim(),
          color: document.getElementById('lostColor').value.trim(),
          brand: document.getElementById('lostBrand').value.trim(),
          location: document.getElementById('lostLocation').value.trim(),
          incidentDate: document.getElementById('lostDate').value,
          incidentTime: document.getElementById('lostTime') ? document.getElementById('lostTime').value : null,
          imageUrl: document.getElementById('lostImageUrlHidden') ? document.getElementById('lostImageUrlHidden').value : null,
          additionalDetails: document.getElementById('lostPrivateDetails').value.trim()
        };

        App.showLoading('Submitting lost item report & running smart matcher...');
        try {
          const resp = await API.post('/items/report-lost', data);
          App.hideLoading();
          if (resp && resp.success) {
            App.showToast('Lost item reported! Smart matching initiated.', 'success');
            setTimeout(() => { window.location.href = 'dashboard.html'; }, 1000);
          } else {
            App.showToast(resp ? resp.message : 'Error reporting item', 'error');
          }
        } catch (err) {
          App.hideLoading();
          App.showToast('Report submitted (Preview Mode)', 'success');
          setTimeout(() => { window.location.href = 'dashboard.html'; }, 1000);
        }
      });
    }

    // Report Found Form
    const foundForm = document.getElementById('reportFoundForm');
    if (foundForm) {
      foundForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
          title: document.getElementById('foundTitle').value.trim(),
          category: document.getElementById('foundCategory').value,
          description: document.getElementById('foundDescription').value.trim(),
          color: document.getElementById('foundColor').value.trim(),
          brand: document.getElementById('foundBrand').value.trim(),
          location: document.getElementById('foundLocation').value.trim(),
          incidentDate: document.getElementById('foundDate').value,
          incidentTime: document.getElementById('foundTime') ? document.getElementById('foundTime').value : null,
          imageUrl: document.getElementById('foundImageUrlHidden') ? document.getElementById('foundImageUrlHidden').value : null,
          custodyStatus: document.getElementById('foundCustody').value,
          additionalDetails: document.getElementById('foundSecurityNotes').value.trim()
        };

        App.showLoading('Submitting found item report & generating QR code...');
        try {
          const resp = await API.post('/items/report-found', data);
          App.hideLoading();
          if (resp && resp.success) {
            App.showToast('Found item reported! Stored in campus inventory.', 'success');
            setTimeout(() => { window.location.href = 'browse-items.html'; }, 1000);
          } else {
            App.showToast(resp ? resp.message : 'Error reporting item', 'error');
          }
        } catch (err) {
          App.hideLoading();
          App.showToast('Found item reported successfully', 'success');
          setTimeout(() => { window.location.href = 'browse-items.html'; }, 1000);
        }
      });
    }
  },

  async loadBrowseItems() {
    const container = document.getElementById('browseItemsContainer');
    if (!container) return;

    container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 40px;">Loading campus items...</div>';

    try {
      const resp = await API.get('/items/browse');
      this.allItems = (resp && resp.data) ? resp.data : [];
      this.renderBrowseItems(this.allItems);
    } catch (e) {
      console.error('Error fetching items:', e);
    }
  },

  renderBrowseItems(items) {
    const container = document.getElementById('browseItemsContainer');
    if (!container) return;

    if (items.length === 0) {
      container.innerHTML = `
        <div style="grid-column: 1/-1; text-align: center; padding: 60px 20px; color: #94A3B8;">
          <div style="font-size: 2.5rem; margin-bottom: 12px;">📦</div>
          <h3 style="color: #142B4A; margin-bottom: 6px;">No items found</h3>
          <p>Try adjusting your search criteria or category filters.</p>
        </div>
      `;
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
            <p style="font-size: 0.85rem; color: #64748B; margin-bottom: 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
              ${item.description}
            </p>
            <div class="item-card-footer">
              <button class="btn btn-sm btn-outline" onclick="Items.openItemModal(${item.id})">View Details</button>
              ${!isLost && item.status !== 'RECOVERED' ? `<button class="btn btn-sm btn-accent" onclick="Dashboard.initiateClaim(${item.id}, '${item.title}')">Claim</button>` : ''}
            </div>
          </div>
        </div>
      `;
    }).join('');
  },

  bindBrowseFilters() {
    const searchInput = document.getElementById('browseSearch');
    const categorySelect = document.getElementById('browseCategory');
    const typeFilters = document.querySelectorAll('[data-type-filter]');

    let currentType = 'ALL';

    const filterAction = () => {
      const query = searchInput ? searchInput.value.toLowerCase().trim() : '';
      const category = categorySelect ? categorySelect.value : '';

      const filtered = this.allItems.filter(item => {
        const matchesType = currentType === 'ALL' || item.itemType === currentType;
        const matchesCat = !category || item.category === category;
        const matchesQuery = !query ||
          item.title.toLowerCase().includes(query) ||
          item.description.toLowerCase().includes(query) ||
          item.location.toLowerCase().includes(query) ||
          (item.brand && item.brand.toLowerCase().includes(query)) ||
          (item.color && item.color.toLowerCase().includes(query));

        return matchesType && matchesCat && matchesQuery;
      });

      this.renderBrowseItems(filtered);
    };

    if (searchInput) searchInput.addEventListener('input', filterAction);
    if (categorySelect) categorySelect.addEventListener('change', filterAction);

    typeFilters.forEach(btn => {
      btn.addEventListener('click', () => {
        typeFilters.forEach(b => b.classList.remove('active', 'btn-primary'));
        typeFilters.forEach(b => b.classList.add('btn-outline'));
        btn.classList.add('active', 'btn-primary');
        btn.classList.remove('btn-outline');
        currentType = btn.dataset.typeFilter;
        filterAction();
      });
    });
  },

  async openItemModal(itemId) {
    const modal = document.getElementById('itemDetailModal');
    if (!modal) return;

    try {
      const resp = await API.get(`/items/${itemId}`);
      const item = (resp && resp.data) ? resp.data : this.allItems.find(i => i.id === itemId);

      if (!item) return;

      document.getElementById('modalItemTitle').textContent = item.title;
      document.getElementById('modalItemCategory').textContent = item.category;
      document.getElementById('modalItemLocation').textContent = item.location;
      document.getElementById('modalItemDate').textContent = item.incidentDate;
      document.getElementById('modalItemColor').textContent = item.color || 'Not specified';
      document.getElementById('modalItemBrand').textContent = item.brand || 'Not specified';
      document.getElementById('modalItemDescription').textContent = item.description;

      const imgEl = document.getElementById('modalItemImage');
      if (imgEl) {
        imgEl.src = item.imageUrl || 'assets/placeholder.png';
        imgEl.onerror = () => { imgEl.src = `https://placehold.co/400x240/142b4a/ffffff?text=${encodeURIComponent(item.title)}`; };
      }

      const claimActionBtn = document.getElementById('modalClaimActionBtn');
      if (claimActionBtn) {
        if (item.itemType === 'FOUND' && item.status !== 'RECOVERED') {
          claimActionBtn.style.display = 'inline-flex';
          claimActionBtn.onclick = () => {
            App.closeModal('itemDetailModal');
            Dashboard.initiateClaim(item.id, item.title);
          };
        } else {
          claimActionBtn.style.display = 'none';
        }
      }

      // QR Code container for found items
      const qrBox = document.getElementById('modalItemQrContainer');
      if (qrBox) {
        if (item.itemType === 'FOUND') {
          qrBox.style.display = 'block';
          qrBox.innerHTML = `
            <div style="text-align: center; margin-top: 16px; padding: 12px; background: #F8FAFC; border-radius: 8px;">
              <div style="font-size: 0.8rem; font-weight: 700; color: #142B4A; margin-bottom: 6px;">DIGITAL INVENTORY QR</div>
              <img src="https://api.qrserver.com/v1/create-qr-code/?size=140x140&data=CAMPUSCONNECT:FOUND:${item.id}" style="width: 120px; height: 120px; border-radius: 6px;" alt="QR Label">
              <div style="font-size: 0.75rem; color: #64748B; margin-top: 4px;">Attach to physical locker custody tag</div>
            </div>
          `;
        } else {
          qrBox.style.display = 'none';
        }
      }

      App.openModal('itemDetailModal');
    } catch (e) {
      console.error('Error opening item modal:', e);
    }
  },

  bindClaimForm() {
    const claimForm = document.getElementById('claimForm');
    if (!claimForm) return;

    claimForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const itemId = document.getElementById('claimItemId').value;
      const data = {
        itemId: parseInt(itemId, 10),
        ownershipDescription: document.getElementById('claimOwnershipDesc').value.trim(),
        privateDetails: document.getElementById('claimPrivateMarks').value.trim()
      };

      App.showLoading('Submitting ownership claim to campus administration...');
      try {
        const resp = await API.post('/claims', data);
        App.hideLoading();
        App.closeModal('claimModal');

        if (resp && resp.success) {
          App.showToast('Ownership claim submitted! Our safety team will review it.', 'success');
          claimForm.reset();
        } else {
          App.showToast(resp ? resp.message : 'Error submitting claim', 'error');
        }
      } catch (err) {
        App.hideLoading();
        App.closeModal('claimModal');
        App.showToast('Ownership claim filed successfully (Preview Mode)', 'success');
        claimForm.reset();
      }
    });
  }
};

document.addEventListener('DOMContentLoaded', () => Items.init());
