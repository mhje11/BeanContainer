import { initEditor } from './editor.js';

let editor;

document.addEventListener('DOMContentLoaded', function() {
    initEditor('#editor')
        .then(newEditor => {
            editor = newEditor;
        })
        .catch(error => {
            console.log('CKEditor 초기화 오류: ', error);
        });
});

document.getElementById('postForm').addEventListener('submit', function (event) {
    event.preventDefault();

    if (!editor) {
        alert('Editor not initialized yet.');
        return;
    }

    const formData = new FormData();
    const editorContent = editor.getData();

    formData.append('postRequestDto', new Blob([JSON.stringify({
        title: document.getElementById('title').value,
        content: editorContent
    })], {type: 'application/json'}));

    const images = document.getElementById('images').files;

    for (let i = 0; i < images.length; i++) {
        formData.append('images', images[i]);
    }

    fetch('/api/posts', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(error => {
                    throw new Error(error.message);
                });
            }
            return response.json()
        })
        .then(data => {
            console.log(data);
            window.location.href = '/posts/' + data.postId;
        })
        .catch(error => {
            alert(error.message);
            console.error('Error:', error)
        });
});

document.getElementById('images').addEventListener('change', function () {
    const fileInput = document.getElementById('images');
    const fileNameDisplay = document.getElementById('fileName');

    if (fileInput.files.length > 0) {
        fileNameDisplay.textContent = Array.from(fileInput.files).map(file => file.name).join(', ');
    } else {
        fileNameDisplay.textContent = '선택된 파일 없음';
    }
});