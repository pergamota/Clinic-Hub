const getPatients = () => apiFetch('/patients');
const getPatientById = (id) => apiFetch(`/patients/${id}`);
const createPatient = (dto) => apiFetch('/patients', { method: 'POST', body: JSON.stringify(dto) });
const updatePatient = (id, dto) => apiFetch(`/patients/${id}`, { method: 'PUT', body: JSON.stringify(dto) });
const deletePatient = (id) => apiFetch(`/patients/${id}`, { method: 'DELETE' });