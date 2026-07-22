const getExams = () => apiFetch('/exams');
const getExamById = (id) => apiFetch(`/exams/${id}`);
const createExam = (dto) => apiFetch('/exams', { method: 'POST', body: JSON.stringify(dto) });
const updateExamResult = (id, resultUrl) =>
    apiFetch(`/exams/${id}`, { method: 'PUT', body: JSON.stringify({ resultUrl }) });
const deleteExam = (id) => apiFetch(`/exams/${id}`, { method: 'DELETE' });