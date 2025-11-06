// UI Components Module
const Components = {
    async renderPlayers(container) {
        // Check if user is admin
        if (!Auth.isAdmin()) {
            container.innerHTML = `
                <div style="text-align: center; padding: 60px 20px;">
                    <h2 style="color: #ef4444;">🚫 Acceso Restringido</h2>
                    <p style="color: #6b7280; font-size: 18px;">Solo los Administradores pueden gestionar jugadores.</p>
                    <p style="color: #6b7280;">Los Jugadores solo pueden participar en las regatas.</p>
                    <button class="btn btn-primary" onclick="navigate('game')" style="margin-top: 20px;">
                        🎮 Ir a Jugar
                    </button>
                </div>
            `;
            return;
        }

        const players = await API.players.getAll();
        
        container.innerHTML = `
            <h2>👥 Gestión de Jugadores</h2>
            <button class="btn btn-primary" onclick="Components.showCreatePlayerForm()">➕ Nuevo Jugador</button>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre de Usuario</th>
                        <th>Nombre para Mostrar</th>
                        <th>Rol</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    ${players.map(p => `
                        <tr>
                            <td>${p.id}</td>
                            <td>${p.username}</td>
                            <td>${p.displayName}</td>
                            <td><span style="color: ${p.role === 'ADMINISTRATOR' ? '#ef4444' : '#10b981'};">${p.role === 'ADMINISTRATOR' ? '👑 Admin' : '⛵ Jugador'}</span></td>
                            <td>
                                <button class="btn btn-primary" onclick="Components.editPlayer(${p.id})">✏️ Editar</button>
                                <button class="btn btn-danger" onclick="Components.deletePlayer(${p.id})">🗑️ Borrar</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    },

    async renderBoats(container) {
        // Check if user is admin
        if (!Auth.isAdmin()) {
            container.innerHTML = `
                <div style="text-align: center; padding: 60px 20px;">
                    <h2 style="color: #ef4444;">🚫 Acceso Restringido</h2>
                    <p style="color: #6b7280; font-size: 18px;">Solo los Administradores pueden gestionar barcos.</p>
                    <p style="color: #6b7280;">Los Jugadores solo pueden participar en las regatas.</p>
                    <button class="btn btn-primary" onclick="navigate('game')" style="margin-top: 20px;">
                        🎮 Ir a Jugar
                    </button>
                </div>
            `;
            return;
        }

        const boats = await API.boats.getAll();
        
        container.innerHTML = `
            <h2>🚢 Gestión de Barcos</h2>
            <button class="btn btn-primary" onclick="Components.showCreateBoatForm()">➕ Nuevo Barco</button>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Modelo</th>
                        <th>Propietario</th>
                        <th>Posición</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    ${boats.map(b => `
                        <tr>
                            <td>${b.id}</td>
                            <td>${b.model ? b.model.name : 'N/A'}</td>
                            <td>${b.owner ? b.owner.displayName : 'N/A'}</td>
                            <td>(${b.x}, ${b.y})</td>
                            <td>
                                <button class="btn btn-primary" onclick="Components.editBoat(${b.id})">✏️ Editar</button>
                                <button class="btn btn-danger" onclick="Components.deleteBoat(${b.id})">🗑️ Borrar</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    },

    async renderModels(container) {
        // Check if user is admin
        if (!Auth.isAdmin()) {
            container.innerHTML = `
                <div style="text-align: center; padding: 60px 20px;">
                    <h2 style="color: #ef4444;">🚫 Acceso Restringido</h2>
                    <p style="color: #6b7280; font-size: 18px;">Solo los Administradores pueden gestionar modelos.</p>
                    <p style="color: #6b7280;">Los Jugadores solo pueden participar en las regatas.</p>
                    <button class="btn btn-primary" onclick="navigate('game')" style="margin-top: 20px;">
                        🎮 Ir a Jugar
                    </button>
                </div>
            `;
            return;
        }

        const models = await API.models.getAll();
        
        container.innerHTML = `
            <h2>📋 Gestión de Modelos</h2>
            <button class="btn btn-primary" onclick="Components.showCreateModelForm()">➕ Nuevo Modelo</button>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Color</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    ${models.map(m => `
                        <tr>
                            <td>${m.id}</td>
                            <td>${m.name}</td>
                            <td><span style="background: ${m.colorHex}; padding: 5px 15px; border-radius: 4px; color: white;">${m.colorHex}</span></td>
                            <td>
                                <button class="btn btn-primary" onclick="Components.editModel(${m.id})">✏️ Editar</button>
                                <button class="btn btn-danger" onclick="Components.deleteModel(${m.id})">🗑️ Borrar</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    },

    async renderMaps(container) {
        // Check if user is admin
        if (!Auth.isAdmin()) {
            container.innerHTML = `
                <div style="text-align: center; padding: 60px 20px;">
                    <h2 style="color: #ef4444;">🚫 Acceso Restringido</h2>
                    <p style="color: #6b7280; font-size: 18px;">Solo los Administradores pueden gestionar mapas.</p>
                    <p style="color: #6b7280;">Los Jugadores solo pueden participar en las regatas.</p>
                    <button class="btn btn-primary" onclick="navigate('game')" style="margin-top: 20px;">
                        🎮 Ir a Jugar
                    </button>
                </div>
            `;
            return;
        }

        const maps = await API.maps.getAll();
        
        container.innerHTML = `
            <h2>🗺️ Gestión de Mapas</h2>
            <button class="btn btn-primary" onclick="Components.showCreateMapForm()">➕ Nuevo Mapa</button>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Dimensiones</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    ${maps.map(m => `
                        <tr>
                            <td>${m.id}</td>
                            <td>${m.name}</td>
                            <td>${m.width} x ${m.height}</td>
                            <td>
                                <button class="btn btn-primary" onclick="Components.editMap(${m.id})">✏️ Editar</button>
                                <button class="btn btn-danger" onclick="Components.deleteMap(${m.id})">🗑️ Borrar</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    },

    async renderGameLobby(container) {
        const [players, boats, maps] = await Promise.all([
            API.players.getAll(),
            API.boats.getAll(),
            API.maps.getAll()
        ]);

        container.innerHTML = `
            <h2>🎮 ¡Juega la Regata!</h2>
            <div class="form-group">
                <label>Selecciona Navegante:</label>
                <select id="playerId">
                    <option value="">-- Elige navegante --</option>
                    ${players.map(p => `<option value="${p.id}">${p.displayName}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Selecciona Barco:</label>
                <select id="boatId">
                    <option value="">-- Elige barco --</option>
                    ${boats.map(b => `<option value="${b.id}">${b.model ? b.model.name : 'Barco ' + b.id}</option>`).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Selecciona Mapa:</label>
                <select id="mapId">
                    <option value="">-- Elige mapa --</option>
                    ${maps.map(m => `<option value="${m.id}">${m.name}</option>`).join('')}
                </select>
            </div>
            <button class="btn btn-primary" onclick="GameEngine.startGame()" style="width: 100%; padding: 15px; font-size: 18px;">
                🚢 ¡Comenzar Regata!
            </button>
        `;
    },

    // Placeholder CRUD functions
    showCreatePlayerForm() { alert('Crear jugador - Implementar formulario'); },
    editPlayer(id) { alert(`Editar jugador ${id}`); },
    async deletePlayer(id) {
        if (confirm('¿Eliminar jugador?')) {
            await API.players.delete(id);
            Router.render();
        }
    },

    showCreateBoatForm() { alert('Crear barco - Implementar formulario'); },
    editBoat(id) { alert(`Editar barco ${id}`); },
    async deleteBoat(id) {
        if (confirm('¿Eliminar barco?')) {
            await API.boats.delete(id);
            Router.render();
        }
    },

    showCreateModelForm() { alert('Crear modelo - Implementar formulario'); },
    editModel(id) { alert(`Editar modelo ${id}`); },
    async deleteModel(id) {
        if (confirm('¿Eliminar modelo?')) {
            await API.models.delete(id);
            Router.render();
        }
    },

    showCreateMapForm() { alert('Crear mapa - Implementar formulario'); },
    editMap(id) { alert(`Editar mapa ${id}`); },
    async deleteMap(id) {
        if (confirm('¿Eliminar mapa?')) {
            await API.maps.delete(id);
            Router.render();
        }
    }
};

console.log('✅ Components Module Loaded');
