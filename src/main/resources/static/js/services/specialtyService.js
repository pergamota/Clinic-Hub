const getSpecialties = () => apiFetch('/specialties');
const getSpecialtyById = (id) => apiFetch(`/specialties/${id}`);
const createSpecialty = (dto) => apiFetch('/specialties', { method: 'POST', body: JSON.stringify(dto) });
const updateSpecialty = (id, dto) => apiFetch(`/specialties/${id}`, { method: 'PUT', body: JSON.stringify(dto) });
const deleteSpecialty = (id) => apiFetch(`/specialties/${id}`, { method: 'DELETE' });