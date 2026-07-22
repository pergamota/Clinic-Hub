const refData = { users: [], specialties: [], doctors: [], patients: [] };
let currentTab = 'specialties';
let appointments = [];

const TAB_TITLES = {
    specialties: 'Especialidades',
    doctors: 'Medicos',
    patients: 'Pacientes',
    users: 'Usuarios',
    appointments: 'Consultas'
};

function userLabel(userId) {
    const user = refData.users.find(u => u.id === userId);
    return user ? `${user.name} (${user.email})` : `Usuario #${userId}`;
}

function specialtyLabel(specialtyId) {
    const specialty = refData.specialties.find(s => s.id === specialtyId);
    return specialty ? specialty.name : `#${specialtyId}`;
}

function doctorLabel(doctorId) {
    const doctor = refData.doctors.find(d => d.id === doctorId);
    return doctor ? userLabel(doctor.userId) : `Medico #${doctorId}`;
}

function patientLabel(patientId) {
    const patient = refData.patients.find(p => p.id === patientId);
    return patient ? userLabel(patient.userId) : `Paciente #${patientId}`;
}

async function loadReferenceData() {
    const [users, specialties, doctors, patients] = await Promise.all([
        getUsers(), getSpecialties(), getDoctors(), getPatients()
    ]);
    refData.users = users;
    refData.specialties = specialties;
    refData.doctors = doctors;
    refData.patients = patients;
}

async function switchTab(tab) {
    currentTab = tab;
    document.getElementById('tabTitle').textContent = TAB_TITLES[tab];
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('secondary', btn.dataset.tab !== tab);
    });

    const content = document.getElementById('content');
    content.innerHTML = '<p>Carregando...</p>';

    try {
        if (tab === 'specialties') renderSpecialties();
        else if (tab === 'doctors') renderDoctors();
        else if (tab === 'patients') renderPatients();
        else if (tab === 'users') renderUsers();
        else if (tab === 'appointments') {
            appointments = await getAppointments();
            renderAppointments();
        }
    } catch (err) {
        content.innerHTML = `<p class="empty-state">${err.message}</p>`;
    }
}

async function refreshAndRender() {
    await loadReferenceData();
    if (currentTab === 'appointments') {
        appointments = await getAppointments();
    }
    switchTab(currentTab);
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

/* ===== Specialties ===== */

function renderSpecialties() {
    const content = document.getElementById('content');
    if (refData.specialties.length === 0) {
        content.innerHTML = emptyState('Nenhuma especialidade cadastrada.');
        return;
    }
    content.innerHTML = refData.specialties.map(s => cardShell(
        s.name,
        [`ID: ${s.id}`],
        `<button data-action="edit" data-id="${s.id}">Editar</button><button data-action="delete" data-id="${s.id}">Excluir</button>`
    )).join('');

    content.querySelectorAll('[data-action="edit"]').forEach(btn =>
        btn.addEventListener('click', () => openSpecialtyForm(Number(btn.dataset.id))));
    content.querySelectorAll('[data-action="delete"]').forEach(btn =>
        btn.addEventListener('click', () => handleDelete('Especialidade', () => deleteSpecialty(Number(btn.dataset.id)))));
}

function openSpecialtyForm(id) {
    const specialty = id ? refData.specialties.find(s => s.id === id) : null;
    openModal(`
        <h2>${specialty ? 'Editar' : 'Nova'} Especialidade</h2>
        <form id="entityForm">
            <div class="form-group">
                <label for="name">Nome</label>
                <input class="input-field" id="name" required value="${specialty ? specialty.name : ''}">
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Salvar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const dto = { name: document.getElementById('name').value };
        try {
            if (specialty) await updateSpecialty(specialty.id, dto);
            else await createSpecialty(dto);
            closeModal();
            showToast('Especialidade salva com sucesso', 'success');
            refreshAndRender();
        } catch (err) {
            document.getElementById('formError').textContent = err.message;
        }
    });
}

/* ===== Doctors ===== */

function renderDoctors() {
    const content = document.getElementById('content');
    if (refData.doctors.length === 0) {
        content.innerHTML = emptyState('Nenhum medico cadastrado.');
        return;
    }
    content.innerHTML = refData.doctors.map(d => cardShell(
        userLabel(d.userId),
        [`CRM: ${d.crm}`, `Especialidade: ${specialtyLabel(d.specialtyId)}`],
        `<button data-action="edit" data-id="${d.id}">Editar</button><button data-action="delete" data-id="${d.id}">Excluir</button>`
    )).join('');

    content.querySelectorAll('[data-action="edit"]').forEach(btn =>
        btn.addEventListener('click', () => openDoctorForm(Number(btn.dataset.id))));
    content.querySelectorAll('[data-action="delete"]').forEach(btn =>
        btn.addEventListener('click', () => handleDelete('Medico', () => deleteDoctor(Number(btn.dataset.id)))));
}

function openDoctorForm(id) {
    const doctor = id ? refData.doctors.find(d => d.id === id) : null;
    const doctorUsers = refData.users.filter(u => u.role === 'DOCTOR');

    openModal(`
        <h2>${doctor ? 'Editar' : 'Novo'} Medico</h2>
        <form id="entityForm">
            <div class="form-group">
                <label for="userId">Usuario</label>
                <select class="input-field" id="userId" required>
                    <option value="">Selecione...</option>
                    ${doctorUsers.map(u => `<option value="${u.id}" ${doctor && doctor.userId === u.id ? 'selected' : ''}>${u.name} (${u.email})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label for="crm">CRM</label>
                <input class="input-field" id="crm" required value="${doctor ? doctor.crm : ''}">
            </div>
            <div class="form-group">
                <label for="specialtyId">Especialidade</label>
                <select class="input-field" id="specialtyId" required>
                    <option value="">Selecione...</option>
                    ${refData.specialties.map(s => `<option value="${s.id}" ${doctor && doctor.specialtyId === s.id ? 'selected' : ''}>${s.name}</option>`).join('')}
                </select>
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Salvar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const dto = {
            userId: Number(document.getElementById('userId').value),
            crm: document.getElementById('crm').value,
            specialtyId: Number(document.getElementById('specialtyId').value)
        };
        try {
            if (doctor) await updateDoctor(doctor.id, dto);
            else await createDoctor(dto);
            closeModal();
            showToast('Medico salvo com sucesso', 'success');
            refreshAndRender();
        } catch (err) {
            document.getElementById('formError').textContent = err.message;
        }
    });
}

/* ===== Patients ===== */

function renderPatients() {
    const content = document.getElementById('content');
    if (refData.patients.length === 0) {
        content.innerHTML = emptyState('Nenhum paciente cadastrado.');
        return;
    }
    content.innerHTML = refData.patients.map(p => cardShell(
        userLabel(p.userId),
        [`CPF: ${p.cpf}`, `Nascimento: ${formatDate(p.birthDate)}`, `Telefone: ${p.phone || '-'}`],
        `<button data-action="edit" data-id="${p.id}">Editar</button><button data-action="delete" data-id="${p.id}">Excluir</button>`
    )).join('');

    content.querySelectorAll('[data-action="edit"]').forEach(btn =>
        btn.addEventListener('click', () => openPatientForm(Number(btn.dataset.id))));
    content.querySelectorAll('[data-action="delete"]').forEach(btn =>
        btn.addEventListener('click', () => handleDelete('Paciente', () => deletePatient(Number(btn.dataset.id)))));
}

function openPatientForm(id) {
    const patient = id ? refData.patients.find(p => p.id === id) : null;
    const patientUsers = refData.users.filter(u => u.role === 'PATIENT');

    openModal(`
        <h2>${patient ? 'Editar' : 'Novo'} Paciente</h2>
        <form id="entityForm">
            <div class="form-group">
                <label for="userId">Usuario</label>
                <select class="input-field" id="userId" required>
                    <option value="">Selecione...</option>
                    ${patientUsers.map(u => `<option value="${u.id}" ${patient && patient.userId === u.id ? 'selected' : ''}>${u.name} (${u.email})</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label for="cpf">CPF</label>
                <input class="input-field" id="cpf" required value="${patient ? patient.cpf : ''}">
            </div>
            <div class="form-group">
                <label for="birthDate">Data de nascimento</label>
                <input class="input-field" type="date" id="birthDate" value="${patient && patient.birthDate ? patient.birthDate : ''}">
            </div>
            <div class="form-group">
                <label for="phone">Telefone</label>
                <input class="input-field" id="phone" value="${patient && patient.phone ? patient.phone : ''}">
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Salvar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const dto = {
            userId: Number(document.getElementById('userId').value),
            cpf: document.getElementById('cpf').value,
            birthDate: document.getElementById('birthDate').value || null,
            phone: document.getElementById('phone').value
        };
        try {
            if (patient) await updatePatient(patient.id, dto);
            else await createPatient(dto);
            closeModal();
            showToast('Paciente salvo com sucesso', 'success');
            refreshAndRender();
        } catch (err) {
            document.getElementById('formError').textContent = err.message;
        }
    });
}

/* ===== Users ===== */

function renderUsers() {
    const content = document.getElementById('content');
    if (refData.users.length === 0) {
        content.innerHTML = emptyState('Nenhum usuario cadastrado.');
        return;
    }
    content.innerHTML = refData.users.map(u => cardShell(
        u.name,
        [`Email: ${u.email}`, `Papel: ${u.role}`],
        `<button data-action="edit" data-id="${u.id}">Editar</button><button data-action="delete" data-id="${u.id}">Excluir</button>`
    )).join('');

    content.querySelectorAll('[data-action="edit"]').forEach(btn =>
        btn.addEventListener('click', () => openUserForm(Number(btn.dataset.id))));
    content.querySelectorAll('[data-action="delete"]').forEach(btn =>
        btn.addEventListener('click', () => handleDelete('Usuario', () => deleteUser(Number(btn.dataset.id)))));
}

function openUserForm(id) {
    const user = id ? refData.users.find(u => u.id === id) : null;

    openModal(`
        <h2>${user ? 'Editar' : 'Novo'} Usuario</h2>
        <form id="entityForm">
            <div class="form-group">
                <label for="name">Nome</label>
                <input class="input-field" id="name" required value="${user ? user.name : ''}">
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input class="input-field" type="email" id="email" required value="${user ? user.email : ''}">
            </div>
            ${!user ? `
            <div class="form-group">
                <label for="password">Senha</label>
                <input class="input-field" type="password" id="password" required>
            </div>
            ` : ''}
            <div class="form-group">
                <label for="role">Papel</label>
                <select class="input-field" id="role" ${user ? 'disabled' : 'required'}>
                    <option value="ADMIN" ${user && user.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    <option value="DOCTOR" ${user && user.role === 'DOCTOR' ? 'selected' : ''}>DOCTOR</option>
                    <option value="PATIENT" ${user && user.role === 'PATIENT' ? 'selected' : ''}>PATIENT</option>
                </select>
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Salvar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const dto = {
            name: document.getElementById('name').value,
            email: document.getElementById('email').value,
            password: user ? undefined : document.getElementById('password').value,
            role: user ? user.role : document.getElementById('role').value
        };
        try {
            if (user) await updateUser(user.id, dto);
            else await createUser(dto);
            closeModal();
            showToast('Usuario salvo com sucesso', 'success');
            refreshAndRender();
        } catch (err) {
            document.getElementById('formError').textContent = err.message;
        }
    });
}

/* ===== Appointments ===== */

function renderAppointments() {
    const content = document.getElementById('content');
    if (appointments.length === 0) {
        content.innerHTML = emptyState('Nenhuma consulta cadastrada.');
        return;
    }
    content.innerHTML = appointments.map(a => cardShell(
        `Consulta #${a.id}`,
        [
            `Paciente: ${patientLabel(a.patientId)}`,
            `Medico: ${doctorLabel(a.doctorId)}`,
            `Data: ${formatDateTime(a.appointmentDate)}`,
            `<span class="status-badge status-${a.status}">${a.status}</span>`
        ],
        `<button data-action="edit" data-id="${a.id}">Status</button><button data-action="delete" data-id="${a.id}">Excluir</button>`
    )).join('');

    content.querySelectorAll('[data-action="edit"]').forEach(btn =>
        btn.addEventListener('click', () => openAppointmentForm(Number(btn.dataset.id))));
    content.querySelectorAll('[data-action="delete"]').forEach(btn =>
        btn.addEventListener('click', () => handleDelete('Consulta', () => deleteAppointment(Number(btn.dataset.id)))));
}

function openAppointmentForm(id) {
    const appointment = id ? appointments.find(a => a.id === id) : null;

    if (!appointment) {
        openModal(`
            <h2>Nova Consulta</h2>
            <form id="entityForm">
                <div class="form-group">
                    <label for="patientId">Paciente</label>
                    <select class="input-field" id="patientId" required>
                        <option value="">Selecione...</option>
                        ${refData.patients.map(p => `<option value="${p.id}">${userLabel(p.userId)}</option>`).join('')}
                    </select>
                </div>
                <div class="form-group">
                    <label for="doctorId">Medico</label>
                    <select class="input-field" id="doctorId" required>
                        <option value="">Selecione...</option>
                        ${refData.doctors.map(d => `<option value="${d.id}">${userLabel(d.userId)}</option>`).join('')}
                    </select>
                </div>
                <div class="form-group">
                    <label for="appointmentDate">Data e hora</label>
                    <input class="input-field" type="datetime-local" id="appointmentDate" required>
                </div>
                <div id="formError" class="field-error"></div>
                <button class="dashboard-btn" type="submit">Salvar</button>
            </form>
        `);

        document.getElementById('entityForm').addEventListener('submit', async (event) => {
            event.preventDefault();
            const dto = {
                patientId: Number(document.getElementById('patientId').value),
                doctorId: Number(document.getElementById('doctorId').value),
                appointmentDate: document.getElementById('appointmentDate').value
            };
            try {
                await createAppointment(dto);
                closeModal();
                showToast('Consulta criada com sucesso', 'success');
                refreshAndRender();
            } catch (err) {
                document.getElementById('formError').textContent = err.message;
            }
        });
        return;
    }

    openModal(`
        <h2>Atualizar Status</h2>
        <form id="entityForm">
            <div class="form-group">
                <label for="status">Status</label>
                <select class="input-field" id="status" required>
                    <option value="SCHEDULED" ${appointment.status === 'SCHEDULED' ? 'selected' : ''}>SCHEDULED</option>
                    <option value="COMPLETED" ${appointment.status === 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                    <option value="CANCELLED" ${appointment.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                </select>
            </div>
            <div id="formError" class="field-error"></div>
            <button class="dashboard-btn" type="submit">Salvar</button>
        </form>
    `);

    document.getElementById('entityForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        try {
            await updateAppointmentStatus(appointment.id, document.getElementById('status').value);
            closeModal();
            showToast('Status atualizado com sucesso', 'success');
            refreshAndRender();
        } catch (err) {
            document.getElementById('formError').textContent = err.message;
        }
    });
}

/* ===== Shared ===== */

async function handleDelete(label, deleteFn) {
    if (!confirm(`Tem certeza que deseja excluir este(a) ${label}?`)) return;
    try {
        await deleteFn();
        showToast(`${label} excluido(a) com sucesso`, 'success');
        refreshAndRender();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function openCreateModal() {
    if (currentTab === 'specialties') openSpecialtyForm(null);
    else if (currentTab === 'doctors') openDoctorForm(null);
    else if (currentTab === 'patients') openPatientForm(null);
    else if (currentTab === 'users') openUserForm(null);
    else if (currentTab === 'appointments') openAppointmentForm(null);
}

document.addEventListener('DOMContentLoaded', async () => {
    requireAuth(['ADMIN']);
    renderHeader();
    renderFooter();

    document.querySelectorAll('.tab-btn').forEach(btn =>
        btn.addEventListener('click', () => switchTab(btn.dataset.tab)));
    document.getElementById('createBtn').addEventListener('click', openCreateModal);

    try {
        await loadReferenceData();
        switchTab(currentTab);
    } catch (err) {
        document.getElementById('content').innerHTML = `<p class="empty-state">${err.message}</p>`;
    }
});