function loginModalTemplate(role) {
    const roleLabel = { ADMIN: 'Administrador', DOCTOR: 'Medico', PATIENT: 'Paciente' }[role] || role;
    return `
        <h2>Login - ${roleLabel}</h2>
        <form id="loginForm">
            <div class="form-group">
                <label for="email">Email</label>
                <input class="input-field" type="email" id="email" required>
            </div>
            <div class="form-group">
                <label for="password">Senha</label>
                <input class="input-field" type="password" id="password" required>
            </div>
            <div id="loginError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Entrar</button>
        </form>
    `;
}

function selectRole(role) {
    openModal(loginModalTemplate(role));

    document.getElementById('loginForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorEl = document.getElementById('loginError');
        errorEl.textContent = '';

        try {
            const session = await login(email, password);
            closeModal();
            showToast('Login realizado com sucesso', 'success');

            if (session.role !== role) {
                showToast(`Esta conta pertence ao perfil ${session.role}`, 'info');
            }

            window.location.href = dashboardPathForRole(session.role);
        } catch (err) {
            errorEl.textContent = err.message;
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    if (isLoggedIn()) {
        window.location.href = dashboardPathForRole(getRole());
        return;
    }

    renderHeader();
    renderFooter();

    document.querySelectorAll('.role-btn').forEach((btn) => {
        btn.addEventListener('click', () => selectRole(btn.dataset.role));
    });
});