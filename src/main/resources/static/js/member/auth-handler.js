document.addEventListener('DOMContentLoaded', function() {
    // API 요청을 보내는 함수
    function makeApiRequest(url, method = 'GET', data = null) {
        return fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include', // 쿠키를 포함하여 요청을 보냄
            body: data ? JSON.stringify(data) : null
        }).then(response => {
            if (response.status === 401) {
                // 액세스 토큰이 만료된 경우
                return refreshAccessToken().then(success => {
                    if (success) {
                        // 토큰 갱신 성공, 원래 요청 재시도
                        return makeApiRequest(url, method, data);
                    } else {
                        // 토큰 갱신 실패, 로그인 페이지로 리다이렉트
                        handleLoginExpired();
                        throw new Error('Login expired');
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

    // 로그아웃 함수
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