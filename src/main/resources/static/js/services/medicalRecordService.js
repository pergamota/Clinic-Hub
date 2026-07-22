const getMedicalRecords = () => apiFetch('/medical-records');
const getMedicalRecordById = (id) => apiFetch(`/medical-records/${id}`);
const createMedicalRecord = (dto) => apiFetch('/medical-records', { method: 'POST', body: JSON.stringify(dto) });
const updateMedicalRecord = (id, description) =>
    apiFetch(`/medical-records/${id}`, { method: 'PUT', body: JSON.stringify({ description }) });
const deleteMedicalRecord = (id) => apiFetch(`/medical-records/${id}`, { method: 'DELETE' });