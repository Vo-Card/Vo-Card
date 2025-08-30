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
 * Refresh the access token using the HttpOnly refresh token cookie.
 * Stores the new access token in sessionStorage.
 * @returns {Promise<string|null>} The new access token, or null if refresh failed.
 */
export async function refreshToken() {
    try {
        const response = await fetch("/api/auth/refresh", {
            method: "POST",
            credentials: "include"
        });

        if (!response.ok) {
            TokenManager.clearAccessToken();
            return null;
        }

        const data = await response.json();
        const newAccessToken = data.access_token;

        if (newAccessToken) {
            TokenManager.setAccessToken(newAccessToken);
            return newAccessToken;
        } else {
            TokenManager.clearAccessToken();
            return null;
        }

    } catch (error) {
        console.error("Error refreshing token:", error);
        TokenManager.clearAccessToken();
        return null;
    }
}

/**
 * A wrapper for the native fetch() API that automatically handles JWT
 * authentication and token refreshing.
 * @param {string} url - The URL to fetch.
 * @param {object} options - The fetch options (e.g., method, body, headers).
 * @returns {Promise<Response>} The response from the fetch call.
 */
export async function fetchWithAuth(url, options = {}) {
    let accessToken = TokenManager.getAccessToken();

    if (!accessToken) {
        accessToken = await refreshToken();
        if (!accessToken) {
            window.location.replace("/login");
            return null;
        }
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