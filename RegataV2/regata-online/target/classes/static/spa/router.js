// SPA Router and Navigation
const Router = {
    currentPage: 'players',

    navigate(page) {
        this.currentPage = page;
        
        // Update active button
        document.querySelectorAll('nav button').forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.page === page) {
                btn.classList.add('active');
            }
        });

        // Render page
        this.render();
    },

    async render() {
        const app = document.getElementById('app');
        app.innerHTML = '<div class="loading">Cargando...</div>';

        try {
            switch(this.currentPage) {
                case 'players':
                    await Components.renderPlayers(app);
                    break;
                case 'boats':
                    await Components.renderBoats(app);
                    break;
                case 'models':
                    await Components.renderModels(app);
                    break;
                case 'maps':
                    await Components.renderMaps(app);
                    break;
                case 'game':
                    await Components.renderGameLobby(app);
                    break;
                case 'game-board':
                    await GameEngine.renderGameBoard(app);
                    break;
                default:
                    app.innerHTML = '<h2>Página no encontrada</h2>';
            }
        } catch (error) {
            console.error('Render error:', error);
            app.innerHTML = `<div style="color: red;">Error: ${error.message}</div>`;
        }
    }
};

function navigate(page) {
    Router.navigate(page);
}

// Initialize on load
window.addEventListener('DOMContentLoaded', async () => {
    console.log('🚀 SPA Initializing...');
    const authenticated = await Auth.checkAuth();
    if (authenticated) {
        Router.navigate('players');
    }
});

console.log('✅ Router Module Loaded');
