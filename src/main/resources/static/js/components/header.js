function renderHeader() {
    const el = document.getElementById('header');
    if (!el) return;

    const role = getRole();
    const user = getCurrentUser();

    el.innerHTML = `
        <div class="logo-title">ClinicHub</div>
        <nav>
            ${user ? `
                <span>${user.name}</span>
                <span class="role-badge">${role}</span>
                <button id="logoutBtn" class="dashboard-btn" type="button" style="width:auto; margin-top:0;">Sair</button>
            ` : ''}
        </nav>
    `;

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
}