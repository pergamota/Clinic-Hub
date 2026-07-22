const SESSION_KEY = 'clinichub_session';

function saveSession(session) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

function getSession() {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
}

function clearSession() {
    localStorage.removeItem(SESSION_KEY);
}

function getToken() {
    const session = getSession();
    return session ? session.token : null;
}

function getRole() {
    const session = getSession();
    return session ? session.role : null;
}

function getCurrentUser() {
    const session = getSession();
    return session ? session.user : null;
}

function getCurrentProfile() {
    const session = getSession();
    return session ? session.profile : null;
}

function isLoggedIn() {
    return !!getToken();
}

function decodeJwtPayload(token) {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const json = decodeURIComponent(
        atob(base64)
            .split('')
            .map(c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
            .join('')
    );
    return JSON.parse(json);
}

function requireAuth(allowedRoles) {
    if (!isLoggedIn()) {
        window.location.href = '/index.html';
        return;
    }
    if (allowedRoles && !allowedRoles.includes(getRole())) {
        window.location.href = '/index.html';
    }
}

function logout() {
    clearSession();
    window.location.href = '/index.html';
}

function formatDate(value) {
    if (!value) return '-';
    const date = new Date(value);
    return date.toLocaleDateString('pt-BR');
}

function formatDateTime(value) {
    if (!value) return '-';
    const date = new Date(value);
    return date.toLocaleString('pt-BR');
}

function extractErrorMessage(body) {
    if (!body) return 'Unexpected error';
    if (body.message) return body.message;
    const values = Object.values(body);
    if (values.length > 0 && values.every(v => typeof v === 'string')) {
        return values.join(', ');
    }
    return 'Unexpected error';
}

async function apiFetch(path, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers
    });

    if (response.status === 204) {
        return null;
    }

    let body = null;
    try {
        body = await response.json();
    } catch (e) {
        body = null;
    }

    if (!response.ok) {
        if (response.status === 401) {
            clearSession();
            window.location.href = '/index.html';
        }
        throw new Error(extractErrorMessage(body));
    }

    return body;
}