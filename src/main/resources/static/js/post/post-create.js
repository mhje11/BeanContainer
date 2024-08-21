document.getElementById('images').addEventListener('change', function() {
    const fileInput = document.getElementById('images');
    const fileNameDisplay = document.getElementById('fileName');

    if (fileInput.files.length > 0) {
        fileNameDisplay.textContent = Array.from(fileInput.files).map(file => file.name).join(', ');
    } else {
        fileNameDisplay.textContent = '선택된 파일 없음';
    }
});

document.getElementById('postForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const formData = new FormData();    // this

    const postRequestDto = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value
    };

    formData.append('postRequestDto', new Blob([JSON.stringify({
        title: document.getElementById('title').value,
        content: document.getElementById('content').value
    })], { type: 'application/json' }));

    const images = document.getElementById('images').files;

    for (let i = 0; i < images.length && i < 5; i++) {  // 이미지 최대 5장
        formData.append('images', images[i]);
    }

    fetch('/api/post/create', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            window.location.href = '/postList/' + data.postId;
        })
        .catch(error => console.error('Error:', error));
});