let currentTab = 'appointments';
let myAppointments = [];
let myMedicalRecords = [];
let myExams = [];
let patients = [];
let profile = null;

const TAB_TITLES = {
    appointments: 'Minhas Consultas',
    medicalRecords: 'Prontuarios',
    exams: 'Exames'
};

function patientLabel(patientId) {
    const patient = patients.find(p => p.id === patientId);
    return patient ? `Paciente #${patient.id} (${patient.cpf})` : `Paciente #${patientId}`;
}

function appointmentLabel(appointmentId) {
    const appointment = myAppointments.find(a => a.id === appointmentId);
    if (!appointment) return `Consulta #${appointmentId}`;
    return `Consulta #${appointment.id} - ${patientLabel(appointment.patientId)} (${formatDateTime(appointment.appointmentDate)})`;
}

function cardShell(title, lines, actionsHtml) {
    return `
        <div class="entity-card">
            <div class="entity-info">
                <h3>${title}</h3>
                ${lines.map(l => `<p>${l}</p>`).join('')}
            </div>
            <div class="card-actions">${actionsHtml}</div>
        </div>
    `;
}

function emptyState(message) {
    return `<div class="empty-state">${message}</div>`;
}

async function loadData() {
    const [allAppointments, allPatients, allRecords, allExams] = await Promise.all([
        getAppointments(), getPatients(), getMedicalRecords(), getExams()
    ]);
    patients = allPatients;
    myAppointments = allAppointments.filter(a => a.doctorId === profile.id);
    const myAppointmentIds = myAppointments.map(a => a.id);
    myMedicalRecords = allRecords.filter(r => myAppointmentIds.includes(r.appointmentID));
    myExams = allExams.filter(e => myAppointmentIds.includes(e.appointmentId));
}

async function refreshAndRender() {
    await loadData();
    switchTab(currentTab);
}

function switchTab(tab) {
    currentTab = tab;
    document.getElementById('tabTitle').textContent = TAB_TITLES[tab];
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('secondary', btn.dataset.tab !== tab);
    });

    if (tab === 'appointments') renderAppointments();
    else if (tab === 'medicalRecords') renderMedicalRecords();
    else if (tab === 'exams') renderExams();
}

/* ===== Appointments ===== */

function renderAppointments() {
    const content = document.getElementById('content');
    if (myAppointments.length === 0) {
        content.innerHTML = emptyState('Nenhuma consulta atribuida a voce.');
        return;
    }
    content.innerHTML = myAppointments.map(a => cardShell(
        patientLabel(a.patientId),
        [
            `Data: ${formatDateTime(a.appointmentDate)}`,
            `<span class="status-badge status-${a.status}">${a.status}</span>`
        ],
        a.status === 'SCHEDULED'
            ? `<button data-action="complete" data-id="${a.id}">Marcar Concluida</button>`
            : ''
    )).join('');

    content.querySelectorAll('[data-action="complete"]').forEach(btn =>
        btn.addEventListener('click', () => completeAppointment(Number(btn.dataset.id))));
}

async function completeAppointment(id) {
    if (!confirm('Marcar esta consulta como concluida?')) return;
    try {
        await updateAppointmentStatus(id, 'COMPLETED');
        showToast('Consulta marcada como concluida', 'success');
        refreshAndRender();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

/* ===== Medical Records ===== */

function renderMedicalRecords() {
    const content = document.getElementById('content');
    if (myMedicalRecords.length === 0) {
        content.innerHTML = emptyState('Nenhum prontuario registrado.');
        return;
    }
    content.innerHTML = myMedicalRecords.map(r => cardShell(
        appointmentLabel(r.appointmentID),
        [`Descricao: ${r.description}`, `Criado em: ${formatDateTime(r.createdAt)}`],
        `<button data-action="edit" data-id="${r.id}">Editar</button>`
    )).join('');

    content.querySelectorAll('[data-action="edit"]').forEach(btn =>
        btn.addEventListener('click', () => openMedicalRecordForm(Number(btn.dataset.id))));
}

function completedAppointmentsWithoutRecord() {
    const recordedAppointmentIds = myMedicalRecords.map(r => r.appointmentID);
    return myAppointments.filter(a => a.status === 'COMPLETED' && !recordedAppointmentIds.includes(a.id));
}

function openMedicalRecordForm(id) {
    const record = id ? myMedicalRecords.find(r => r.id === id) : null;
    const availableAppointments = record ? [] : completedAppointmentsWithoutRecord();

    if (!record && availableAppointments.length === 0) {
        showToast('Nao ha consultas concluidas sem prontuario', 'info');
        return;
    }

    openModal(`
        <h2>${record ? 'Editar' : 'Novo'} Prontuario</h2>
        <form id="entityForm">
            ${!record ? `
            <div class="form-group">
                <label for="appointmentId">Consulta</label>
                <select class="input-field" id="appointmentId" required>
                    <option value="">Selecione...</option>
                    ${availableAppointments.map(a => `<option value="${a.id}">${patientLabel(a.patientId)} - ${formatDateTime(a.appointmentDate)}</option>`).join('')}
                </select>
            </div>
            ` : ''}
            <div class="form-group">
                <label for="description">Descricao</label>
                <textarea class="input-field" id="description" rows="4" required>${record ? record.description : ''}</textarea>
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Salvar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const description = document.getElementById('description').value;
        try {
            if (record) {
                await updateMedicalRecord(record.id, description);
            } else {
                const appointmentId = Number(document.getElementById('appointmentId').value);
                await createMedicalRecord({ appointmentId, description });
            }
            closeModal();
            showToast('Prontuario salvo com sucesso', 'success');
            refreshAndRender();
        } catch (err) {
            document.getElementById('formError').textContent = err.message;
        }
    });
}

/* ===== Exams ===== */

function renderExams() {
    const content = document.getElementById('content');
    if (myExams.length === 0) {
        content.innerHTML = emptyState('Nenhum exame solicitado.');
        return;
    }
    content.innerHTML = myExams.map(e => cardShell(
        e.examType || 'Exame',
        [
            appointmentLabel(e.appointmentId),
            `Resultado: ${e.resultUrl || 'Pendente'}`,
            `Solicitado em: ${formatDateTime(e.requestedAt)}`
        ],
        `<button data-action="edit" data-id="${e.id}">Registrar Resultado</button>`
    )).join('');

    content.querySelectorAll('[data-action="edit"]').forEach(btn =>
        btn.addEventListener('click', () => openExamForm(Number(btn.dataset.id))));
}

function openExamForm(id) {
    const exam = id ? myExams.find(e => e.id === id) : null;

    if (!exam) {
        openModal(`
            <h2>Novo Exame</h2>
            <form id="entityForm">
                <div class="form-group">
                    <label for="appointmentId">Consulta</label>
                    <select class="input-field" id="appointmentId" required>
                        <option value="">Selecione...</option>
                        ${myAppointments.map(a => `<option value="${a.id}">${patientLabel(a.patientId)} - ${formatDateTime(a.appointmentDate)}</option>`).join('')}
                    </select>
                </div>
                <div class="form-group">
                    <label for="examType">Tipo de exame</label>
                    <input class="input-field" id="examType" required>
                </div>
                <div id="formError" class="field-error"></div>
                <button class="dashboard-btn" type="submit">Solicitar</button>
            </form>
        `);

        document.getElementById('entityForm').addEventListener('submit', async (event) => {
            event.preventDefault();
            const dto = {
                appointmentId: Number(document.getElementById('appointmentId').value),
                examType: document.getElementById('examType').value
            };
            try {
                await createExam(dto);
                closeModal();
                showToast('Exame solicitado com sucesso', 'success');
                refreshAndRender();
            } catch (err) {
                document.getElementById('formError').textContent = err.message;
            }
        });
        return;
    }

    openModal(`
        <h2>Registrar Resultado</h2>
        <form id="entityForm">
            <div class="form-group">
                <label for="resultUrl">Resultado (URL ou texto)</label>
                <input class="input-field" id="resultUrl" required value="${exam.resultUrl || ''}">
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Salvar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        try {
            await updateExamResult(exam.id, document.getElementById('resultUrl').value);
            closeModal();
            showToast('Resultado registrado com sucesso', 'success');
            refreshAndRender();
        } catch (err) {
            document.getElementById('formError').textContent = err.message;
        }
    });
}

/* ===== Create button dispatch ===== */

function openCreateModal() {
    if (currentTab === 'medicalRecords') openMedicalRecordForm(null);
    else if (currentTab === 'exams') openExamForm(null);
    else showToast('Nao ha criacao disponivel nesta aba', 'info');
}

document.addEventListener('DOMContentLoaded', async () => {
    requireAuth(['DOCTOR']);
    profile = getCurrentProfile();
    renderHeader();
    renderFooter();

    if (!profile) {
        document.getElementById('content').innerHTML =
            '<p class="empty-state">Nenhum perfil de medico vinculado a este usuario.</p>';
        return;
    }

    document.querySelectorAll('.tab-btn').forEach(btn =>
        btn.addEventListener('click', () => switchTab(btn.dataset.tab)));
    document.getElementById('createBtn').addEventListener('click', openCreateModal);

    try {
        await loadData();
        switchTab(currentTab);
    } catch (err) {
        document.getElementById('content').innerHTML = `<p class="empty-state">${err.message}</p>`;
    }
});