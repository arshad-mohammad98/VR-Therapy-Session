/**
 * Client-side UI utilities for Virtual Reality Therapy platform.
 */
document.addEventListener('DOMContentLoaded', () => {
    // Quick role filler for demo convenience on login page
    window.fillLogin = function(username, password) {
        const u = document.getElementById('username');
        const p = document.getElementById('password');
        if (u && p) {
            u.value = username;
            p.value = password;
        }
    };

    // Auto-sync range sliders with their display labels
    document.querySelectorAll('input[type="range"]').forEach(slider => {
        const label = document.getElementById(slider.id + '-val');
        if (label) {
            slider.addEventListener('input', (e) => {
                label.textContent = e.target.value;
            });
        }
    });
});
