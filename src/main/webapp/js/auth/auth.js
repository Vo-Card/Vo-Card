// /js/auth/auth.js

// The TokenManager handles getting and setting tokens from sessionStorage
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

    console.log("Using access token:", accessToken);

    if (!accessToken) {
        console.error("No access token found. Redirecting to login.");
        return null;
    }

    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${accessToken}`
    };

    let response = await fetch(url, { ...options, headers });

    console.log("Fetch response status:", response.status);

    if (response.status === 401) {
        console.log("Access token expired. Attempting to refresh.");
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
                console.error("Session refresh failed. Redirecting to login.");
                TokenManager.clearAccessToken();
            }
        } catch (error) {
            console.error("Network error during refresh:", error);
            TokenManager.clearAccessToken();
        }
    }
    
    return response;
}