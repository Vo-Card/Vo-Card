export const TokenManager = {
    getAccessToken: () => {
        return sessionStorage.getItem('access_token');
    },
    setAccessToken: (token) => {
        sessionStorage.setItem('access_token', token);
    },
    clearAccessToken: () => {
        sessionStorage.removeItem('access_token');
    }
};

/**
 * A wrapper for the native fetch() API that automatically handles JWT
 * authentication and token refreshing.
 * @param {string} url - The URL to fetch.
 * @param {object} options - The fetch options (e.g., method, body, headers).
 * @returns {Promise<Response>} The response from the fetch call.
 */
export async function fetchWithAuth(url, options = {}) {
    const accessToken = TokenManager.getAccessToken();

    if (!accessToken) {
        return null;
    }

    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${accessToken}`
    };

    let response = await fetch(url, { ...options, headers });

    if (response.status === 401) {
        try {
            const refreshResponse = await fetch("/api/auth/refresh", { 
                method: "POST",
                credentials: "include"
             });
            
            if (refreshResponse.ok) {
                const data = await refreshResponse.json();
                TokenManager.setAccessToken(data.access_token);
                
                const newAccessToken = TokenManager.getAccessToken();
                const newHeaders = {
                    ...options.headers,
                    'Authorization': `Bearer ${newAccessToken}`
                };
                response = await fetch(url, { ...options, headers: newHeaders });
                
            } else {
                TokenManager.clearAccessToken();
                window.location.replace("/login");
            }
        } catch (error) {
            TokenManager.clearAccessToken();
            window.location.replace("/login");
        }
    }
    
    return response;
}