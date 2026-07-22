let currentTab = 'appointments';
let myAppointments = [];
let myExams = [];
let doctors = [];
let specialties = [];
let profile = null;

const TAB_TITLES = {
    appointments: 'Minhas Consultas',
    exams: 'Meus Exames'
};

function specialtyLabel(specialtyId) {
    const specialty = specialties.find(s => s.id === specialtyId);
    return specialty ? specialty.name : `#${specialtyId}`;
}

function doctorLabel(doctorId) {
    const doctor = doctors.find(d => d.id === doctorId);
    return doctor ? `Dr(a). CRM ${doctor.crm} - ${specialtyLabel(doctor.specialtyId)}` : `Medico #${doctorId}`;
}

function cardShell(title, lines, actionsHtml) {
    return `
        <div class="entity-card">
            <div class="entity-info">
                <h3>${title}</h3>
                ${lines.map(l => `<p>${l}</p>`).join('')}
            </div>
            ${actionsHtml ? `<div class="card-actions">${actionsHtml}</div>` : ''}
        </div>
    `;
}

function emptyState(message) {
    return `<div class="empty-state">${message}</div>`;
}

async function loadData() {
    const [allAppointments, allDoctors, allSpecialties, allExams] = await Promise.all([
        getAppointments(), getDoctors(), getSpecialties(), getExams()
    ]);
    doctors = allDoctors;
    specialties = allSpecialties;
    myAppointments = allAppointments.filter(a => a.patientId === profile.id);
    const myAppointmentIds = myAppointments.map(a => a.id);
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
    document.getElementById('createBtn').style.display = tab === 'appointments' ? 'block' : 'none';

    if (tab === 'appointments') renderAppointments();
    else if (tab === 'exams') renderExams();
}

/* ===== Appointments ===== */

function renderAppointments() {
    const content = document.getElementById('content');
    if (myAppointments.length === 0) {
        content.innerHTML = emptyState('Voce ainda nao tem consultas agendadas.');
        return;
    }
    content.innerHTML = myAppointments.map(a => cardShell(
        doctorLabel(a.doctorId),
        [
            `Data: ${formatDateTime(a.appointmentDate)}`,
            `<span class="status-badge status-${a.status}">${a.status}</span>`
        ],
        ''
    )).join('');
}

function openScheduleForm() {
    openModal(`
        <h2>Agendar Consulta</h2>
        <form id="entityForm">
            <div class="form-group">
                <label for="doctorId">Medico</label>
                <select class="input-field" id="doctorId" required>
                    <option value="">Selecione...</option>
                    ${doctors.map(d => `<option value="${d.id}">${doctorLabel(d.id)}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label for="appointmentDate">Data e hora</label>
                <input class="input-field" type="datetime-local" id="appointmentDate" required>
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Agendar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const dto = {
            patientId: profile.id,
            doctorId: Number(document.getElementById('doctorId').value),
            appointmentDate: document.getElementById('appointmentDate').value
        };
        try {
            await createAppointment(dto);
            closeModal();
            showToast('Consulta agendada com sucesso', 'success');
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
        content.innerHTML = emptyState('Nenhum exame encontrado.');
        return;
    }
    content.innerHTML = myExams.map(e => cardShell(
        e.examType || 'Exame',
        [
            `Solicitado em: ${formatDateTime(e.requestedAt)}`,
            `Resultado: ${e.resultUrl || 'Ainda nao disponivel'}`
        ],
        ''
    )).join('');
}

document.addEventListener('DOMContentLoaded', async () => {
    requireAuth(['PATIENT']);
    profile = getCurrentProfile();
    renderHeader();
    renderFooter();

    if (!profile) {
        document.getElementById('content').innerHTML =
            '<p class="empty-state">Nenhum perfil de paciente vinculado a este usuario.</p>';
        return;
    }

    document.querySelectorAll('.tab-btn').forEach(btn =>
        btn.addEventListener('click', () => switchTab(btn.dataset.tab)));
    document.getElementById('createBtn').addEventListener('click', openScheduleForm);

    try {
        await loadData();
        switchTab(currentTab);
    } catch (err) {
        document.getElementById('content').innerHTML = `<p class="empty-state">${err.message}</p>`;
    }
});