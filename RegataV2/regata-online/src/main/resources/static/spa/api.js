// API Communication Module
const API = {
    baseURL: '/api',

    async request(endpoint, options = {}) {
        try {
            const response = await fetch(`${this.baseURL}${endpoint}`, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    // Players
    players: {
        getAll: () => API.request('/players'),
        getById: (id) => API.request(`/players/${id}`),
        create: (data) => API.request('/players', { method: 'POST', body: JSON.stringify(data) }),
        update: (id, data) => API.request(`/players/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
        delete: (id) => API.request(`/players/${id}`, { method: 'DELETE' })
    },

    // Boats
    boats: {
        getAll: () => API.request('/boats'),
        getById: (id) => API.request(`/boats/${id}`),
        create: (data) => API.request('/boats', { method: 'POST', body: JSON.stringify(data) }),
        update: (id, data) => API.request(`/boats/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
        delete: (id) => API.request(`/boats/${id}`, { method: 'DELETE' })
    },

    // Models
    models: {
        getAll: () => API.request('/models'),
        getById: (id) => API.request(`/models/${id}`),
        create: (data) => API.request('/models', { method: 'POST', body: JSON.stringify(data) }),
        update: (id, data) => API.request(`/models/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
        delete: (id) => API.request(`/models/${id}`, { method: 'DELETE' })
    },

    // Maps
    maps: {
        getAll: () => API.request('/maps'),
        getById: (id) => API.request(`/maps/${id}`),
        create: (data) => API.request('/maps', { method: 'POST', body: JSON.stringify(data) }),
        update: (id, data) => API.request(`/maps/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
        delete: (id) => API.request(`/maps/${id}`, { method: 'DELETE' })
    },

    // Game
    game: {
        start: (data) => API.request('/game/start', { method: 'POST', body: JSON.stringify(data) }),
        move: (data) => API.request('/game/move', { method: 'POST', body: JSON.stringify(data) }),
        getState: (playerId, boatId, mapId) => API.request(`/game/state?playerId=${playerId}&boatId=${boatId}&mapId=${mapId}`)
    }
};

console.log('✅ API Module Loaded');
