// ==========================================
// PROFESSIONAL SAILING REGATTA GAME 🌊⛵
// ==========================================

console.log('🎮 Professional Sailing Game Loading...');

// Global game state
window.gameStarted = true;
window.playerPosition = {x: 0, y: 0};
window.targetPosition = {x: 14, y: 7};
window.moves = 0;

// Professional sailing game initialization
window.initializeGrid = function() {
    console.log('🌊 Building professional sailing grid...');
    
    const grid = document.getElementById('raceGrid');
    if (!grid) {
        console.error('❌ Grid element not found!');
        return;
    }
    
    grid.innerHTML = '';
    
    // Get game data from window or use fallback
    const gameData = window.sailingGameData || {
        grid: 'P              \n XXXXXXXXXXXXX \n XX           X\n XX           X\n XXXX         X\n              X\n XX           X\n             M ',
        width: 15,
        height: 8
    };
    
    // Parse map grid from backend
    const mapGrid = gameData.grid;
    const rows = mapGrid.split('\n');
    const height = rows.length;
    const width = Math.max(...rows.map(row => row.length));
    
    console.log(`📐 Grid dimensions: ${width} x ${height}`);
    
    // Professional grid styling
    grid.style.gridTemplateColumns = `repeat(${width}, 1fr)`;
    grid.style.gridTemplateRows = `repeat(${height}, 1fr)`;
    grid.style.width = `${width * 40}px`;
    grid.style.height = `${height * 40}px`;
    
    // Build the sailing course
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
            
            // Professional sailing course elements
            switch(cellChar) {
                case 'P':
                    cell.innerHTML = '🚢';
                    cell.style.background = '#dbeafe';
                    cell.style.boxShadow = '0 0 10px #3b82f6';
                    window.playerPosition.x = x;
                    window.playerPosition.y = y;
                    console.log(`⚓ Start position: (${x}, ${y})`);
                    break;
                case 'M':
                    cell.innerHTML = '🏁';
                    cell.style.background = '#dcfce7';
                    cell.style.boxShadow = '0 0 10px #10b981';
                    window.targetPosition.x = x;
                    window.targetPosition.y = y;
                    console.log(`🎯 Finish line: (${x}, ${y})`);
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
    
    updateStatus('🏁 ¡Regata iniciada! Navega hacia la meta 🎯');
    console.log('✅ Professional sailing course ready!');
};

// Professional boat movement
window.moveBoat = function(direction) {
    if (!window.gameStarted) {
        alert('🚫 ¡La regata no ha comenzado!');
        return;
    }
    
    console.log(`⛵ Navegating ${direction} from (${window.playerPosition.x}, ${window.playerPosition.y})`);
    
    let newX = window.playerPosition.x;
    let newY = window.playerPosition.y;
    
    // Professional navigation controls
    switch(direction) {
        case 'up': newY = Math.max(0, newY - 1); break;
        case 'down': newY = Math.min(7, newY + 1); break; // 8 rows (0-7)
        case 'left': newX = Math.max(0, newX - 1); break;
        case 'right': newX = Math.min(14, newX + 1); break; // 15 columns (0-14)
    }
    
    // Collision detection
    const newCell = document.getElementById(`cell_${newX}_${newY}`);
    if (newCell && newCell.innerHTML === '🪨') {
        alert('⚠️ ¡No puedes navegar sobre las rocas!');
        return;
    }
    
    // Move the boat
    const oldCell = document.getElementById(`cell_${window.playerPosition.x}_${window.playerPosition.y}`);
    if (oldCell && oldCell.innerHTML !== '🏁') {
        oldCell.innerHTML = '🌊';
        oldCell.style.background = '#bfdbfe';
        oldCell.style.boxShadow = 'none';
    }
    
    window.playerPosition.x = newX;
    window.playerPosition.y = newY;
    window.moves++;
    
    const currentCell = document.getElementById(`cell_${newX}_${newY}`);
    if (currentCell) {
        if (currentCell.innerHTML === '🏁') {
            // VICTORY!
            currentCell.innerHTML = '🏆';
            currentCell.style.background = '#fef3c7';
            currentCell.style.boxShadow = '0 0 20px #f59e0b';
            updateStatus(`🏆 ¡VICTORIA! Regata completada en ${window.moves} movimientos!`);
            window.gameStarted = false;
            setTimeout(() => {
                alert(`🎉 ¡Felicitaciones! Ganaste la regata profesional en ${window.moves} movimientos! 🏆`);
            }, 500);
        } else {
            currentCell.innerHTML = '🚢';
            currentCell.style.background = '#dbeafe';
            currentCell.style.boxShadow = '0 0 10px #3b82f6';
            updateStatus(`⛵ Navegando... Movimientos: ${window.moves}`);
        }
    }
};

// Professional status updates
window.updateStatus = function(message) {
    const statusEl = document.getElementById('statusText');
    if (statusEl) {
        statusEl.textContent = message;
        console.log('📊 Status:', message);
    }
};

// ==========================================
// EXISTING FUNCTIONALITY
// ==========================================

// Activa enlace de navegación
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 DOM Content Loaded - Starting app...');
    
    const path = location.pathname;
    document.querySelectorAll('.nav a').forEach(a => {
        if (path.startsWith(a.getAttribute('href'))) a.classList.add('active');
    });

    // Initialize sailing game if on board page
    console.log('🔍 Current path:', path);
    if (path.includes('/game/board') || document.getElementById('raceGrid')) {
        console.log('🌊 Detected game board page - initializing sailing game...');
        setTimeout(() => {
            window.initializeGrid();
        }, 100); // Small delay to ensure DOM is ready
    }

    // Confirmación de borrado (formularios con clase .js-delete)
    document.querySelectorAll('form.js-delete').forEach(f => {
        f.addEventListener('submit', (e) => {
            if (!confirm('¿Eliminar este registro?')) e.preventDefault();
        });
    });

    // Preview de color en modelos
    const colorInput = document.querySelector('input[name="colorHex"]');
    const swatch = document.getElementById('color-preview');
    if (colorInput && swatch){
        const upd = () => { swatch.style.background = colorInput.value || '#000'; };
        colorInput.addEventListener('input', upd); upd();
    }

    // Oculta alertas luego de 3s
    setTimeout(()=> document.querySelectorAll('.alert').forEach(a=> a.style.display='none'), 3000);
});

console.log('✅ Professional Sailing Game & App Loaded!');