import { initEditor } from './editor.js';

let editorInstance;

document.addEventListener('DOMContentLoaded', function() {
    initEditor('#editor')
        .then(({ editor, getUploadedImages }) => {
            editorInstance = {
                editor: editor,
                getUploadedImages: getUploadedImages
            };
        })
        .catch(error => {
            console.log('CKEditor 초기화 오류: ', error);
        });
});

document.getElementById('postForm').addEventListener('submit', function (event) {
    event.preventDefault();

    if (!editorInstance || !editorInstance.editor) {
        alert('에디터가 아직 초기화되지 않았습니다.');
        return;
    }

    const editorContent = editorInstance.editor.getData();
    const uploadedImages = editorInstance.getUploadedImages();

    // 에디터 내용에서 이미지 url 추출
    const usedImagesUrls = extractImagesFromContent(editorContent);

    // 사용된 이미지 정보
    const imageInfo = uploadedImages.filter(image => usedImagesUrls.includes(image.url));

    // 사용되지 않은 이미지 url 찾기
    const unusedImages = uploadedImages.map(image => image.url).filter(url => !usedImagesUrls.includes(url));

    const postRequestDto = {
        title: document.getElementById('title').value,
        content: editorContent,
        imageInfos: imageInfo,
        usedImageUrls: usedImagesUrls
    }

    fetch('/api/posts', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(postRequestDto)
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
            console.log('게시글 생성 완료:', data);
            window.location.href = '/posts/' + data.postId;
        })
        .catch(error => {
            alert(error.message);
            console.error('Error:', error)
        });
});

function extractImagesFromContent(content) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(content, 'text/html');
    const images = doc.getElementsByTagName('img');
    return Array.from(images).map(img => img.src);
}
