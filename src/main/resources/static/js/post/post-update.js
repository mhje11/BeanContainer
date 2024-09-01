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

async function fetchPost() {
    try {
        const postId = window.location.pathname.split('/')[2];   // URL에서 postId 추출
        const response = await fetch(`/api/posts/${postId}`);
        if (!response.ok) {
            throw new Error('게시글을 가져오는 데 실패하였습니다.');
        }

        const post = await response.json();
        console.log('Fetched Post: ', post)
        document.getElementById('title').value = post.title;
        // document.getElementById('content').value = post.content;

        // CKEditor에 기존 콘텐츠 로드
        if (editor) {
            editor.setData(post.content);
        }

        if (post.imageUrls && post.imageUrls.length > 0) {
            const imagesDiv = document.getElementById('existing-images');
            post.imageUrls.forEach((url, index) => {
                const div = document.createElement('div');
                div.innerHTML = `
                    <img src="${url}" alt="게시글 이미지">
                    <input type="checkbox" name="deleteImages" value="${post.imageIds[index]}"> 삭제
                `;
                imagesDiv.appendChild(div);
            });
        }
    } catch(error) {
        console.error('게시글을 불러오는 중 오류가 발생하였습니다.', error);
    }
};

document.addEventListener('DOMContentLoaded', fetchPost);

document.getElementById('updateForm').addEventListener('submit', function (event) {
    event.preventDefault();

    if (!editor) {
        alert('Editor not initialized yet.');
        return;
    }

    const formData = new FormData();

    const postRequestDto = {
        title: document.getElementById('title').value,
        content: editor.getData(),  // document.getElementById('content').value
        deleteImages: Array.from(document.querySelectorAll('input[name="deleteImages"]:checked'))
            .map(checkbox => parseInt(checkbox.value))
    };

    formData.append('postRequestDto', new Blob([JSON.stringify(postRequestDto)], {type: 'application/json'}));

    const images = document.getElementById('images').files; // 새로운 이미지

    for (let i = 0; i < images.length && i < 5; i++) {  // 이미지 최대 5장
        formData.append('images', images[i]);
    }

    const postId = window.location.pathname.split('/')[2];   // URL에서 postId 추출
    console.log('Extracted postId:', postId);

    fetch(`/api/posts/${postId}/update`, {
        method: 'PUT',
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
            alert('게시글이 수정되었습니다.');
            window.location.href = `/posts/${postId}`; // 수정 후 게시글 상세 페이지로 리다이렉트
        })
        .catch(error => {
            alert(error.message);
            console.error('Error:', error)
        });
});

document.getElementById('images').addEventListener('change', function() {
    const fileInput = document.getElementById('images');
    const fileNameDisplay = document.getElementById('fileName');

    if (fileInput.files.length > 0) {
        fileNameDisplay.textContent = Array.from(fileInput.files).map(file => file.name).join(', ');
    } else {
        fileNameDisplay.textContent = '선택된 파일 없음';
    }
});

document.getElementById('cancel-button').addEventListener('click', function () {
    window.history.back();  // 이전 페이지로 돌아가기
});