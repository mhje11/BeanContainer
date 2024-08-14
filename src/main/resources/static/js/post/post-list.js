let currentPage = 0;
const pageSize = 15;
let sortBy = 'createdAt';

document.getElementById('sort-select').addEventListener('change', function() {
    sortBy = this.value;
    fetchPosts();
});

// 게시글 데이터를 가져오는 함수
async function fetchPosts(page = 0) {
    try {
        const response = await fetch(`/api/postList?page=${page + 1}&size=${pageSize}&sortBy=${sortBy}`);
        if (!response.ok) {
            throw new Error('게시글 목록을 가져오는 데 실패하였습니다.');
        }
        const data = await response.json();
        const posts = data.content;

        // 게시글 목록 표시
        const postListDiv = document.getElementById('post-list');
        postListDiv.innerHTML = ''; // 기존 내용 초기화

        posts.forEach(post => {
            const row = document.createElement('tr');
            row.innerHTML = `
                    <td>${post.id}</td>
                    <td><a href="/postList/${post.id}">${post.title}</a></td>
                    <td>${post.nickname}</td>
                    <td>${post.commentCount}</td>
                    <td>${post.likeCount}</td>
                    <td>${post.updatedAt ? new Date(post.updatedAt).toLocaleString() + ' (수정됨)' : new Date(post.createdAt).toLocaleString()}</td>
                    <td>${post.views}</td>
                `;
            postListDiv.appendChild(row);
        });

        // 페이지 번호 표시
        updatePagination(data.totalPages, page);
    } catch (error) {
        console.error('Error fetching posts:', error);
        const postListDiv = document.getElementById('post-list');
        postListDiv.innerHTML = '<tr><td colspan="5">게시글을 불러오는 중 오류가 발생했습니다.</td></tr>';
    }
}

// 페이지 번호를 업데이트하는 함수
function updatePagination(totalPages, currentPage) {
    const paginationDiv = document.getElementById('pagination');
    paginationDiv.innerHTML = ''; // 기존 페이지 번호 초기화

    for (let i = 0; i < totalPages; i++) {
        const pageItem = document.createElement('span');
        pageItem.innerHTML = `<a href="#">${i + 1}</a>`;
        pageItem.addEventListener('click', (event) => {
            event.preventDefault();
            fetchPosts(i);
        });

        if (i === currentPage) {
            pageItem.style.fontWeight = 'bold'; // 현재 페이지는 굵게 표시
        }

        paginationDiv.appendChild(pageItem);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchPosts(currentPage); // 페이지 로드 후 첫 페이지의 게시글을 가져옴
});