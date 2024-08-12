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

// 전역 스코프에 logout 함수 노출
window.logout = logout;

console.log('Logout function defined:', typeof window.logout);