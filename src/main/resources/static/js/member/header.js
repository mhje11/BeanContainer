console.log('header.js loaded');

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM content loaded');

    // 로그아웃 버튼에 이벤트 리스너 추가
    const logoutButton = document.querySelector('a[th\\:onclick="logout()"]');
    if (logoutButton) {
        console.log('Logout button found');
        logoutButton.removeAttribute('th:onclick');
        logoutButton.addEventListener('click', function(event) {
            console.log('Logout button clicked');
            event.preventDefault();
            logout();
        });
    } else {
        console.log('Logout button not found');
    }
});

function logout() {
    console.log('Logout function called');
    fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'same-origin'
    })
        .then(response => {
            console.log('Logout response received', response);
            if (response.ok) {
                console.log('Logout successful, redirecting to home page');
                window.location.href = '/';
            } else {
                console.error('로그아웃 실패');
            }
        })
        .catch(error => {
            console.error('로그아웃 중 오류 발생:', error);
        });
}

// 토큰 재발급 함수
function refreshAccessToken() {
    console.log('Refreshing access token');
    return fetch('/api/auth/refreshToken', {
        method: 'POST',
        credentials: 'include'
    }).then(response => {
        if (response.ok) {
            console.log('Token refresh successful');
            return true;
        } else {
            console.log('Token refresh failed');
            return false;
        }
    }).catch(error => {
        console.error('Error refreshing token:', error);
        return false;
    });
}

// API 요청 함수
function makeApiRequest(url, method = 'GET', data = null) {
    console.log(`Making API request to ${url}`);
    return fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: data ? JSON.stringify(data) : null
    }).then(response => {
        if (response.status === 401 || response.status === 403) {
            return response.text().then(errorMessage => {
                if (errorMessage === 'JWT 토큰이 만료되었습니다.' || errorMessage === '유효하지 않은 리프레시 토큰입니다.') {
                    console.log('Token expired, attempting refresh');
                    return refreshAccessToken().then(success => {
                        if (success) {
                            console.log('Token refreshed, retrying original request');
                            return makeApiRequest(url, method, data);
                        } else {
                            console.log('Token refresh failed, redirecting to login');
                            window.location.href = '/login';
                            throw new Error('로그인 만료');
                        }
                    });
                } else {
                    console.log('Authentication failed, redirecting to login');
                    window.location.href = '/login';
                    throw new Error('Authentication failed');
                }
            });
        }
        return response.json();
    });
}

// 전역 스코프에 함수들 노출
window.logout = logout;
window.refreshAccessToken = refreshAccessToken;
window.makeApiRequest = makeApiRequest;

console.log('Global functions defined:',
    'logout:', typeof window.logout,
    'refreshAccessToken:', typeof window.refreshAccessToken,
    'makeApiRequest:', typeof window.makeApiRequest);