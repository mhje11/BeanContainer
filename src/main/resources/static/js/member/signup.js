document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('signupForm');
    const userId = document.getElementById('userId');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const nickname = document.getElementById('nickname');
    const name = document.getElementById('name');
    const checkUserIdButton = document.getElementById('checkUserIdButton');
    const signupButton = document.getElementById('signupButton');

    const userIdError = document.getElementById('userIdError');
    const passwordError = document.getElementById('passwordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');
    const nicknameError = document.getElementById('nicknameError');

    let isUserIdValid = false;

    // 아이디 중복 확인
    checkUserIdButton.addEventListener('click', function() {
        const userIdValue = userId.value.trim();

        if (userIdValue.length < 4) {
            userIdError.textContent = '아이디는 4글자 이상이어야 합니다.';
            userIdError.style.color = 'red';
            isUserIdValid = false;
            return;
        }

        // 서버에 아이디 중복 확인 요청
        fetch(`/api/auth/check-userid?userId=${encodeURIComponent(userIdValue)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(isDuplicate => {
                if (isDuplicate) {  // 서버에서 true를 반환하면 중복된 아이디
                    userIdError.textContent = '이미 존재하는 사용자 ID 입니다.';
                    userIdError.style.color = 'red';
                    isUserIdValid = false;
                } else {
                    userIdError.textContent = '사용 가능한 아이디입니다.';
                    userIdError.style.color = 'green';
                    isUserIdValid = true;
                }
                updateSignupButtonState();
            })
            .catch(error => {
                console.error('Error:', error);
                userIdError.textContent = '아이디 중복 확인 중 오류가 발생했습니다. 다시 시도해주세요.';
                userIdError.style.color = 'red';
                isUserIdValid = false;
            });
    });

    function validatePassword() {
        const passwordValue = password.value;
        let isValid = true;
        let errorMsg = '';

        if (passwordValue.length < 8 || passwordValue.length > 20) {
            errorMsg += '비밀번호는 8자 이상 20자 이하여야 합니다. ';
            isValid = false;
        }

        if (!/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/.test(passwordValue)) {
            errorMsg += '비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다. ';
            isValid = false;
        }

        passwordError.textContent = errorMsg;
        return isValid;
    }

    function validateConfirmPassword() {
        if (password.value !== confirmPassword.value) {
            confirmPasswordError.textContent = '비밀번호가 일치하지 않습니다.';
            return false;
        } else {
            confirmPasswordError.textContent = '';
            return true;
        }
    }

    function validateNickname() {
        const nicknameValue = nickname.value.trim();
        if (nicknameValue.length < 2 || nicknameValue.length > 10) {
            nicknameError.textContent = '닉네임은 2자 이상 10자 이하여야 합니다.';
            return false;
        } else {
            nicknameError.textContent = '';
            return true;
        }
    }


    function updateSignupButtonState() {
        signupButton.disabled = !isUserIdValid;
    }

    userId.addEventListener('input', function() {
        isUserIdValid = false;
        updateSignupButtonState();
    });

    password.addEventListener('input', validatePassword);
    confirmPassword.addEventListener('input', validateConfirmPassword);
    nickname.addEventListener('input', validateNickname);
    name.addEventListener('input', validateName);

    form.addEventListener('submit', function(e) {
        e.preventDefault(); // 항상 기본 제출을 막습니다.

        // 모든 필드의 유효성을 검사합니다.
        const isPasswordValid = validatePassword();
        const isConfirmPasswordValid = validateConfirmPassword();
        const isNicknameValid = validateNickname();
        const isNameValid = validateName();

        if (isUserIdValid && isPasswordValid && isConfirmPasswordValid && isNicknameValid && isNameValid) {
            // 모든 유효성 검사를 통과했을 때만 폼을 제출합니다.
            console.log('폼 제출 성공');
            // 여기에 실제 폼 제출 로직을 추가하세요 (예: fetch를 사용한 서버 요청)
        } else {
            console.log('폼 제출 실패: 유효성 검사 오류');
        }
    });
});