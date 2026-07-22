function renderFooter() {
    const el = document.getElementById('footer');
    if (!el) return;

    el.innerHTML = `
        <div class="footer-container">
            <div class="footer-column">
                <h4>ClinicHub</h4>
                <p>Sistema de gestao de clinica medica.</p>
            </div>
            <div class="footer-column">
                <h4>Sobre</h4>
                <p>Projeto de portfolio full stack.</p>
            </div>
            <div class="footer-column">
                <h4>Contato</h4>
                <p>suporte@clinichub.com</p>
            </div>
        </div>
    `;
}