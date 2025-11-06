// Game Engine Module
const GameEngine = {
    gameState: null,
    playerPosition: { x: 0, y: 0 },
    targetPosition: { x: 14, y: 7 },
    moves: 0,
    gameStarted: false,

    async startGame() {
        const playerId = document.getElementById('playerId').value;
        const boatId = document.getElementById('boatId').value;
        const mapId = document.getElementById('mapId').value;

        if (!playerId || !boatId || !mapId) {
            alert('Por favor selecciona navegante, barco y mapa');
            return;
        }

        try {
            this.gameState = await API.game.start({
                playerId: parseInt(playerId),
                boatId: parseInt(boatId),
                mapId: parseInt(mapId)
            });

            this.gameStarted = true;
            this.moves = 0;
            
            Router.currentPage = 'game-board';
            await this.renderGameBoard(document.getElementById('app'));
        } catch (error) {
            alert('Error iniciando el juego: ' + error.message);
        }
    },

    async renderGameBoard(container) {
        if (!this.gameState) {
            container.innerHTML = '<p>No hay juego activo</p>';
            return;
        }

        const { player, boat, gameMap } = this.gameState;

        container.innerHTML = `
            <h2>🏁 ¡Navegando la Regata!</h2>
            
            <div style="background: white; padding: 15px; margin: 15px 0; border-radius: 12px; box-shadow: 0 6px 24px rgba(0,0,0,0.1);">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span style="font-weight: 600; color: #10b981;">⛵ Navegante: ${player.displayName}</span>
                    <span style="font-weight: 600; color: #8b5cf6;">🚢 Barco: ${boat.model.name}</span>
                    <span style="font-weight: 600; color: #f59e0b;">🗺️ Curso: ${gameMap.name}</span>
                </div>
            </div>

            <div class="game-board">
                <h3 style="color: #2563eb;">🌊 Campo de Regata</h3>
                
                <div id="raceGrid" style="display: grid; gap: 2px; background: #0ea5e9; padding: 10px; border-radius: 12px;"></div>
                
                <div>
                    <h4 style="color: #8b5cf6; margin-bottom: 15px;">🎮 Controles de Navegación</h4>
                    <div class="controls">
                        <button class="btn btn-primary" onclick="GameEngine.moveBoat('up')">⬆️ Norte</button>
                        <button class="btn btn-primary" onclick="GameEngine.moveBoat('down')">⬇️ Sur</button>
                        <button class="btn btn-primary" onclick="GameEngine.moveBoat('left')">⬅️ Oeste</button>
                        <button class="btn btn-primary" onclick="GameEngine.moveBoat('right')">➡️ Este</button>
                    </div>
                </div>

                <div id="gameStatus" style="text-align: center; padding: 15px; background: #f0f9ff; border-radius: 8px; margin-top: 20px;">
                    <p style="margin: 0; color: #0c4a6e; font-weight: 600;">Estado: <span id="statusText">¡Navegando hacia la victoria!</span></p>
                </div>

                <button class="btn" style="background: #6b7280; color: white; margin-top: 20px;" onclick="navigate('game')">
                    🏠 Volver al Lobby
                </button>
            </div>
        `;

        this.initializeGrid(gameMap);
    },

    initializeGrid(gameMap) {
        const grid = document.getElementById('raceGrid');
        if (!grid) return;

        const rows = gameMap.grid.split('\n');
        const height = rows.length;
        const width = Math.max(...rows.map(row => row.length));

        grid.style.gridTemplateColumns = `repeat(${width}, 1fr)`;
        grid.style.gridTemplateRows = `repeat(${height}, 1fr)`;
        grid.style.width = `${width * 40}px`;
        grid.style.height = `${height * 40}px`;

        for (let y = 0; y < height; y++) {
            for (let x = 0; x < width; x++) {
                const cell = document.createElement('div');
                cell.style.width = '38px';
                cell.style.height = '38px';
                cell.style.border = '1px solid #60a5fa';
                cell.style.display = 'flex';
                cell.style.alignItems = 'center';
                cell.style.justifyContent = 'center';
                cell.style.fontSize = '18px';
                cell.style.fontWeight = 'bold';
                cell.id = `cell_${x}_${y}`;

                const cellChar = (rows[y] && x < rows[y].length) ? rows[y][x] : ' ';

                switch(cellChar) {
                    case 'P':
                        cell.innerHTML = '🚢';
                        cell.style.background = '#dbeafe';
                        cell.style.boxShadow = '0 0 10px #3b82f6';
                        this.playerPosition.x = x;
                        this.playerPosition.y = y;
                        break;
                    case 'M':
                        cell.innerHTML = '🏁';
                        cell.style.background = '#dcfce7';
                        cell.style.boxShadow = '0 0 10px #10b981';
                        this.targetPosition.x = x;
                        this.targetPosition.y = y;
                        break;
                    case 'X':
                        cell.innerHTML = '🪨';
                        cell.style.background = '#fecaca';
                        cell.style.boxShadow = '0 0 5px #ef4444';
                        break;
                    default:
                        cell.innerHTML = '🌊';
                        cell.style.background = '#bfdbfe';
                        break;
                }

                grid.appendChild(cell);
            }
        }

        this.updateStatus('🏁 ¡Regata iniciada! Navega hacia la meta 🎯');
    },

    moveBoat(direction) {
        if (!this.gameStarted) {
            alert('🚫 ¡La regata no ha comenzado!');
            return;
        }

        let newX = this.playerPosition.x;
        let newY = this.playerPosition.y;

        switch(direction) {
            case 'up': newY = Math.max(0, newY - 1); break;
            case 'down': newY = Math.min(7, newY + 1); break;
            case 'left': newX = Math.max(0, newX - 1); break;
            case 'right': newX = Math.min(14, newX + 1); break;
        }

        const newCell = document.getElementById(`cell_${newX}_${newY}`);
        if (newCell && newCell.innerHTML === '🪨') {
            alert('⚠️ ¡No puedes navegar sobre las rocas!');
            return;
        }

        const oldCell = document.getElementById(`cell_${this.playerPosition.x}_${this.playerPosition.y}`);
        if (oldCell) {
            oldCell.innerHTML = '🌊';
            oldCell.style.background = '#bfdbfe';
            oldCell.style.boxShadow = 'none';
        }

        this.playerPosition.x = newX;
        this.playerPosition.y = newY;
        this.moves++;

        if (newCell) {
            newCell.innerHTML = '🚢';
            newCell.style.background = '#dbeafe';
            newCell.style.boxShadow = '0 0 10px #3b82f6';
        }

        if (newX === this.targetPosition.x && newY === this.targetPosition.y) {
            this.gameStarted = false;
            this.updateStatus(`🏆 ¡VICTORIA! Regata completada en ${this.moves} movimientos!`);
            setTimeout(() => {
                alert(`🎉 ¡Felicitaciones! Completaste la regata en ${this.moves} movimientos!`);
            }, 500);
        } else {
            this.updateStatus(`⛵ Navegando... Movimientos: ${this.moves}`);
        }
    },

    updateStatus(message) {
        const statusText = document.getElementById('statusText');
        if (statusText) {
            statusText.textContent = message;
        }
    }
};

console.log('✅ Game Engine Module Loaded');
