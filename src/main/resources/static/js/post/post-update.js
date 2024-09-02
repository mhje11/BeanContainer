import { initEditor } from './editor.js';

let editorInstance;
let initialImageUrls = [];  // 기존 이미지 url 저장 변수

document.addEventListener('DOMContentLoaded', function() {
    initEditor('#editor')
        .then(({editor, getUploadedImages}) => {
            editorInstance = {
                editor: editor,
                getUploadedImages: getUploadedImages
            };
            fetchPost();
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
        document.getElementById('title').value = post.title;

        // CKEditor에 기존 콘텐츠 로드
        if (editorInstance.editor) {
            editorInstance.editor.setData(post.content);
        }

        // 기존 이미지 url 추출
        initialImageUrls = extractImagesFromContent(post.content);

    } catch(error) {
        console.error('게시글을 불러오는 중 오류가 발생하였습니다.', error);
    }
};


document.getElementById('updateForm').addEventListener('submit', function (event) {
    event.preventDefault();

    if (!editorInstance || !editorInstance.editor) {
        alert('에디터가 아직 초기화되지 않았습니다.');
        return;
    }

    // const formData = new FormData();
    const editorContent = editorInstance.editor.getData();
    const uploadedImages = editorInstance.getUploadedImages();

    // 수정된 내용에서 이미지 url 추출
    const updatedImagesUrls = extractImagesFromContent(editorContent);

    // 사용된 이미지 정보
    const imageInfo = uploadedImages.filter(image => updatedImagesUrls.includes(image.url));

    // 사용되지 않은 이미지 url 찾기
    const unusedImages = initialImageUrls.filter(url => !updatedImagesUrls.includes(url));

    const postRequestDto = {
        title: document.getElementById('title').value,
        content: editorContent,
        imageInfos: imageInfo,
        unusedImageUrls: unusedImages
    };

    const postId = window.location.pathname.split('/')[2];   // URL에서 postId 추출

    fetch(`/api/posts/${postId}/update`, {
        method: 'PUT',
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
            console.log(data);
            alert('게시글이 수정되었습니다.');
            window.location.href = `/posts/${postId}`; // 수정 후 게시글 상세 페이지로 리다이렉트
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


document.getElementById('cancel-button').addEventListener('click', function () {
    window.history.back();  // 이전 페이지로 돌아가기
});