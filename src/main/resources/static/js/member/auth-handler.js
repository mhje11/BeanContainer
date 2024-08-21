document.addEventListener('DOMContentLoaded', function() {
    function makeApiRequest(url, method = 'GET', data = null) {
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
                    if (errorMessage === 'TOKEN_EXPIRED') {
                        return refreshAccessToken().then(success => {
                            if (success) {
                                return makeApiRequest(url, method, data);
                            } else {
                                handleLoginExpired();
                                throw new Error('로그인 만료');
                            }
                        });
                    } else {
                        handleLoginExpired();
                        throw new Error('Authentication failed');
                    }
                });
            }
            return response.json();
        });
    }

    // 액세스 토큰 갱신 함수
    function refreshAccessToken() {
        return fetch('/api/auth/refresh', {
            method: 'POST',
            credentials: 'include'
        }).then(response => {
            if (response.ok) {
                return true; // 토큰 갱신 성공
            } else {
                return false; // 토큰 갱신 실패
            }
        }).catch(() => false);
    }

    // 로그인 만료 처리 함수
    function handleLoginExpired() {
        alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
        window.location.href = '/login';
    }

    // 로그아웃
    function logout() {
        return fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'include'
        }).then(response => {
            if (response.ok) {
                window.location.href = '/login';
            } else {
                throw new Error('Logout failed');
            }
        });
    }

    // 전역 객체로 노출
    window.makeApiRequest = makeApiRequest;
    window.logout = logout;
});