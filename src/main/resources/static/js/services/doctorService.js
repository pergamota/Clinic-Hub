const getDoctors = () => apiFetch('/doctors');
const getDoctorById = (id) => apiFetch(`/doctors/${id}`);
const createDoctor = (dto) => apiFetch('/doctors', { method: 'POST', body: JSON.stringify(dto) });
const updateDoctor = (id, dto) => apiFetch(`/doctors/${id}`, { method: 'PUT', body: JSON.stringify(dto) });
const deleteDoctor = (id) => apiFetch(`/doctors/${id}`, { method: 'DELETE' });