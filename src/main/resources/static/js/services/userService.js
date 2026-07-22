const getUsers = () => apiFetch('/users');
const getUserById = (id) => apiFetch(`/users/${id}`);
const createUser = (dto) => apiFetch('/users', { method: 'POST', body: JSON.stringify(dto) });
const updateUser = (id, dto) => apiFetch(`/users/${id}`, { method: 'PUT', body: JSON.stringify(dto) });
const deleteUser = (id) => apiFetch(`/users/${id}`, { method: 'DELETE' });