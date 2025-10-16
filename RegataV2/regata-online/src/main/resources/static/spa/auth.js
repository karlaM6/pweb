// Authentication Module
const Auth = {
    currentUser: null,

    async login(username) {
        try {
            const response = await API.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ username })
            });

            this.currentUser = response;
            localStorage.setItem('userId', response.id);
            this.updateUI();
            return response;
        } catch (error) {
            throw new Error('Error de autenticación');
        }
    },

    async checkAuth() {
        const userId = localStorage.getItem('userId');
        if (!userId) {
            this.showLoginForm();
            return false;
        }

        try {
            const response = await API.request(`/auth/current?userId=${userId}`);
            if (response.authenticated) {
                this.currentUser = response;
                this.updateUI();
                return true;
            } else {
                this.showLoginForm();
                return false;
            }
        } catch (error) {
            this.showLoginForm();
            return false;
        }
    },

    logout() {
        this.currentUser = null;
        localStorage.removeItem('userId');
        this.showLoginForm();
    },

    showLoginForm() {
        document.getElementById('app').innerHTML = `
            <div style="max-width: 400px; margin: 100px auto; background: white; padding: 40px; border-radius: 16px; box-shadow: 0 8px 32px rgba(0,0,0,0.1);">
                <h2 style="text-align: center; color: #2563eb; margin-bottom: 30px;">🔐 Iniciar Sesión</h2>
                <div class="form-group">
                    <label>Nombre de Usuario:</label>
                    <input type="text" id="loginUsername" placeholder="Ingresa tu nombre de usuario">
                </div>
                <button class="btn btn-primary" onclick="Auth.handleLogin()" style="width: 100%; padding: 12px;">
                    Entrar
                </button>
                <p style="margin-top: 20px; text-align: center; color: #6b7280; font-size: 14px;">
                    Usa cualquier nombre de usuario existente en el sistema
                </p>
            </div>
        `;
    },

    async handleLogin() {
        const username = document.getElementById('loginUsername').value;
        if (!username) {
            alert('Por favor ingresa un nombre de usuario');
            return;
        }

        try {
            await this.login(username);
            // Redirect based on role
            if (this.isAdmin()) {
                Router.navigate('players');
            } else {
                Router.navigate('game');
            }
        } catch (error) {
            alert('Error: ' + error.message);
        }
    },

    updateUI() {
        const header = document.querySelector('header');
        if (!this.currentUser) {
            return;
        }

        const roleColor = this.currentUser.role === 'ADMINISTRATOR' ? '#ef4444' : '#10b981';
        const roleText = this.currentUser.role === 'ADMINISTRATOR' ? '👑 Admin' : '⛵ Jugador';

        // Navigation buttons based on role
        const navButtons = this.isAdmin() ? `
            <button onclick="navigate('players')" data-page="players">👥 Jugadores</button>
            <button onclick="navigate('boats')" data-page="boats">🚢 Barcos</button>
            <button onclick="navigate('models')" data-page="models">📋 Modelos</button>
            <button onclick="navigate('maps')" data-page="maps">🗺️ Mapas</button>
            <button onclick="navigate('game')" data-page="game">🎮 Jugar</button>
        ` : `
            <button onclick="navigate('game')" data-page="game">🎮 Jugar</button>
        `;

        header.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h1 style="margin: 0;">⛵ Regata Online - SPA</h1>
                <div style="display: flex; align-items: center; gap: 15px;">
                    <span style="font-weight: 600; color: ${roleColor};">${roleText}: ${this.currentUser.displayName}</span>
                    <button class="btn btn-danger" onclick="Auth.logout()">🚪 Salir</button>
                </div>
            </div>
            <nav>
                ${navButtons}
            </nav>
        `;
    },

    isAdmin() {
        return this.currentUser && this.currentUser.role === 'ADMINISTRATOR';
    },

    isPlayer() {
        return this.currentUser && this.currentUser.role === 'PLAYER';
    }
};

console.log('✅ Auth Module Loaded');
