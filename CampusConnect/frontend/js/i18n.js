/**
 * CampusConnect Bilingual Support (English & Kannada)
 * Allows seamless language toggling across all interface elements.
 */

const I18n = {
  currentLang: localStorage.getItem('cc_lang') || 'en',

  translations: {
    en: {
      appName: "CampusConnect",
      tagline: "Lost Something? Let's Find It Together.",
      heroSubtitle: "A centralized digital platform for college campuses to manage lost and found belongings. Powered by smart matching, verified ownership claims, and digital handover custody tracking.",
      reportLostBtn: "Report Lost Item",
      reportFoundBtn: "Report Found Item",
      browseItemsBtn: "Browse Items",
      loginBtn: "Sign In",
      registerBtn: "Register Student",
      dashboard: "Dashboard",
      myReports: "My Reports",
      myClaims: "My Claims",
      notifications: "Notifications",
      profile: "Profile",
      logout: "Logout",
      totalLost: "Total Lost Reports",
      totalFound: "Total Found Reports",
      potentialMatches: "Potential Matches",
      recoveredItems: "Recovered Items",
      pendingClaims: "Pending Claims",
      howItWorksTitle: "How CampusConnect Works",
      step1Title: "1. Report Belongings",
      step1Desc: "Submit details of lost or found articles with photos, location, and identifying marks.",
      step2Title: "2. Smart Matching",
      step2Desc: "Our Java algorithm cross-checks title, category, color, location, and date to spot matches.",
      step3Title: "3. Verify Ownership",
      step3Desc: "Submit private ownership proof only visible to campus safety staff for verification.",
      step4Title: "4. Physical Handover",
      step4Desc: "Collect item securely at campus security desk with digital handover acknowledgement.",
      recentReports: "Recent Reports",
      quickActions: "Quick Actions",
      searchPlaceholder: "Search items by title, brand, color, location...",
      category: "Category",
      status: "Status",
      location: "Location",
      date: "Date",
      viewDetails: "View Details",
      claimItem: "Claim Item",
      submitClaimBtn: "Submit Ownership Claim"
    },
    kn: {
      appName: "ಕ್ಯಾಂಪಸ್ ಕನೆಕ್ಟ್",
      tagline: "ಏನಾದರೂ ಕಳೆದುಹೋಗಿದೆಯೇ? ಜೊತೆಯಾಗಿ ಹುಡುಕೋಣ.",
      heroSubtitle: "ಕಾಲೇಜು ಕ್ಯಾಂಪಸ್‌ನಲ್ಲಿ ಕಳೆದುಹೋದ ಮತ್ತು ಸಿಕ್ಕ ವಸ್ತುಗಳನ್ನು ಸುಲಭವಾಗಿ ನಿರ್ವಹಿಸಲು ಡಿಜಿಟಲ್ ವೇದಿಕೆ. ಸ್ಮಾರ್ಟ್ ಹೋಲಿಕೆ ಮತ್ತು ಸುರಕ್ಷಿತ ಮಾಲೀಕತ್ವ ಪರಿಶೀಲನೆ.",
      reportLostBtn: "ಕಳೆದುಹೋದ ವಸ್ತು ವರದಿ",
      reportFoundBtn: "ಸಿಕ್ಕ ವಸ್ತು ವರದಿ",
      browseItemsBtn: "ವಸ್ತುಗಳನ್ನು ಬ್ರೌಸ್ ಮಾಡಿ",
      loginBtn: "ಪ್ರವೇಶಿಸಿ (ಲಾಗಿನ್)",
      registerBtn: "ನೋಂದಣಿ ಮಾಡಿ",
      dashboard: "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್",
      myReports: "ನನ್ನ ವರದಿಗಳು",
      myClaims: "ನನ್ನ ಕ್ಲೈಮ್‌ಗಳು",
      notifications: "ಸೂಚನೆಗಳು",
      profile: "ಪ್ರೊಫೈಲ್",
      logout: "ನಿರ್ಗಮಿಸಿ",
      totalLost: "ಕಳೆದುಹೋದ ವರದಿಗಳು",
      totalFound: "ಸಿಕ್ಕಿದ ವರದಿಗಳು",
      potentialMatches: "ಸಂಭಾವ್ಯ ಹೊಂದಾಣಿಕೆಗಳು",
      recoveredItems: "ಮರಳಿ ಪಡೆದ ವಸ್ತುಗಳು",
      pendingClaims: "ಬಾಕಿ ಇರುವ ಕ್ಲೈಮ್‌ಗಳು",
      howItWorksTitle: "ಕ್ಯಾಂಪಸ್ ಕನೆಕ್ಟ್ ಹೇಗೆ ಕೆಲಸ ಮಾಡುತ್ತದೆ",
      step1Title: "೧. ವರದಿ ಸಲ್ಲಿಸಿ",
      step1Desc: "ಕಳೆದುಹೋದ ಅಥವಾ ಸಿಕ್ಕಿದ ವಸ್ತುಗಳ ವಿವರ, ಫೋಟೋ ಮತ್ತು ಸ್ಥಳವನ್ನು ದಾಖಲಿಸಿ.",
      step2Title: "೨. ಸ್ಮಾರ್ಟ್ ಮ್ಯಾಚಿಂಗ್",
      step2Desc: "ನಮ್ಮ ಜಾವಾ ಅಲ್ಗಾರಿದಮ್ ವರ್ಗ, ಬಣ್ಣ, ಸ್ಥಳ ಮತ್ತು ದಿನಾಂಕವನ್ನು ಹೋಲಿಸುತ್ತದೆ.",
      step3Title: "೩. ಮಾಲೀಕತ್ವ ಪರಿಶೀಲನೆ",
      step3Desc: "ವಸ್ತುವಿನ ರಹಸ್ಯ ಗುರುತುಗಳನ್ನು ನೀಡಿ ನಿಮ್ಮ ಮಾಲೀಕತ್ವವನ್ನು ಸಾಬೀತುಪಡಿಸಿ.",
      step4Title: "೪. ವಸ್ತು ಮರಳಿ ಪಡೆಯಿರಿ",
      step4Desc: "ಕ್ಯಾಂಪಸ್ ಸೆಕ್ಯುರಿಟಿ ಡೆಸ್ಕ್‌ನಲ್ಲಿ ಪರಿಶೀಲನೆಯ ನಂತರ ವಸ್ತುವನ್ನು ಸ್ವೀಕರಿಸಿ.",
      recentReports: "ಇತ್ತೀಚಿನ ವರದಿಗಳು",
      quickActions: "ತ್ವರಿತ ಕ್ರಿಯೆಗಳು",
      searchPlaceholder: "ವಸ್ತು, ಬಣ್ಣ, ಸ್ಥಳ ಹುಡುಕಿ...",
      category: "ವರ್ಗ",
      status: "ಸ್ಥಿತಿ",
      location: "ಸ್ಥಳ",
      date: "ದಿನಾಂಕ",
      viewDetails: "ವಿವರ ನೋಡಿ",
      claimItem: "ಕ್ಲೈಮ್ ಮಾಡಿ",
      submitClaimBtn: "ಕ್ಲೈಮ್ ಸಲ್ಲಿಸಿ"
    }
  },

  init() {
    this.applyLanguage(this.currentLang);
    this.bindToggle();
  },

  bindToggle() {
    const toggleBtns = document.querySelectorAll('.lang-toggle-btn');
    toggleBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        this.currentLang = this.currentLang === 'en' ? 'kn' : 'en';
        localStorage.setItem('cc_lang', this.currentLang);
        this.applyLanguage(this.currentLang);
      });
    });
  },

  applyLanguage(lang) {
    const dict = this.translations[lang] || this.translations.en;
    document.querySelectorAll('[data-i18n]').forEach(el => {
      const key = el.getAttribute('data-i18n');
      if (dict[key]) {
        if (el.tagName === 'INPUT' && el.getAttribute('placeholder')) {
          el.placeholder = dict[key];
        } else {
          el.textContent = dict[key];
        }
      }
    });

    const label = lang === 'en' ? 'ಕನ್ನಡ' : 'English';
    document.querySelectorAll('.lang-label').forEach(el => el.textContent = label);
  }
};

document.addEventListener('DOMContentLoaded', () => I18n.init());
