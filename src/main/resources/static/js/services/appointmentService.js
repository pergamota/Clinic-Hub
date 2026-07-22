const getAppointments = () => apiFetch('/appointments');
const getAppointmentById = (id) => apiFetch(`/appointments/${id}`);
const createAppointment = (dto) => apiFetch('/appointments', { method: 'POST', body: JSON.stringify(dto) });
const updateAppointmentStatus = (id, status) =>
    apiFetch(`/appointments/${id}`, { method: 'PUT', body: JSON.stringify({ status }) });
const deleteAppointment = (id) => apiFetch(`/appointments/${id}`, { method: 'DELETE' });