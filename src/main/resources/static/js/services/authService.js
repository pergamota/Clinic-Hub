async function loginRequest(email, password) {
    return apiFetch('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password })
    });
}

async function resolveSessionFromToken(token) {
    saveSession({ token, role: null, user: null, profile: null });

    const payload = decodeJwtPayload(token);
    const email = payload.sub;

    const users = await apiFetch('/users');
    const user = users.find(u => u.email === email);
    if (!user) {
        clearSession();
        throw new Error('User not found for the authenticated token');
    }

    let profile = null;
    if (user.role === 'DOCTOR') {
        const doctors = await apiFetch('/doctors');
        profile = doctors.find(d => d.userId === user.id) || null;
    } else if (user.role === 'PATIENT') {
        const patients = await apiFetch('/patients');
        profile = patients.find(p => p.userId === user.id) || null;
    }

    const session = { token, role: user.role, user, profile };
    saveSession(session);
    return session;
}

async function login(email, password) {
    const { token } = await loginRequest(email, password);
    return resolveSessionFromToken(token);
}

function dashboardPathForRole(role) {
    switch (role) {
        case 'ADMIN': return '/pages/adminDashboard.html';
        case 'DOCTOR': return '/pages/doctorDashboard.html';
        case 'PATIENT': return '/pages/patientDashboard.html';
        default: return '/index.html';
    }
}