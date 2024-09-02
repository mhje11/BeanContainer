class PostOperations {
    constructor(postId) {
        this.postId = postId;
    }

    async fetchPost() {
        try {
            const response = await fetch(`/api/posts/${this.postId}`);
            if (!response.ok) {
                throw new Error('게시글을 가져오는 데 실패하였습니다.');
            }
            const post = await response.json();

            // HTML 요소에 값 설정
            document.getElementById('title').innerText = post.title;
            document.getElementById('nickname').innerText = post.nickname;
            document.getElementById('createdAt').innerText = post.updatedAt ? new Date(post.updatedAt).toLocaleString() + ' (수정됨)' : new Date(post.createdAt).toLocaleString();
            document.getElementById('content').innerHTML = post.content;    // editor가 적용되어 있기 때문에 text가 아닌 html 자체를 가져와야 함
            document.getElementById('views').innerText = post.views;

            // 프로필 이미지 설정
            const profileImage = document.getElementById('profile-image');
            if (post.profileImageUrl) {
                profileImage.src = post.profileImageUrl;
            } else {
                profileImage.src = '/images/BeanContainer.png';
            }

            // 좋아요
            document.getElementById('like-count').innerText = post.likes;

            const btnList = document.getElementById('btn-list');
            if (post.authorCheck) {  // DTO에서 isAuthor라는 변수명으로 return 했지만 Json으로 직렬화 되는 과정에서 필드명 변환될 수 있음
                btnList.innerHTML = `
                <button type="button" onclick="location.href='/posts/list'">목록</button>
                <button type="button" onclick="location.href='/posts/${this.postId}/update'">수정</button>
                <button type="button" onclick="postOps.deletePost()">삭제</button>
            `;  /*GET이 아닌 DELETE 요청이기 때문에 함수로 호출*/
            } else {
                btnList.innerHTML = `<button type="button" onclick="location.href='/posts/list'">목록</button>`;
            }

            // 댓글 불러오기
            await this.commentAll();

        } catch (error) {
            console.error('Error fetching post', error);
            const postDiv = document.getElementById('post-box');
            postDiv.innerHTML = '<h3>게시글을 불러오는 중 오류가 발생하였습니다.</h3>';
        }
    }

    async deletePost() {
        if(confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                const response = await fetch(`/api/posts/${this.postId}/delete`, {
                    method: 'DELETE',
                });

                if(response.ok) {
                    alert('게시글이 삭제되었습니다.');
                    window.location.href = '/posts/list';    // 삭제 후 게시판으로 리다이렉트
                } else {
                    const errorMsg = await response.text();
                    alert(errorMsg || '게시글 삭제에 실패하였습니다.');
                }
            } catch (error) {
                console.error('Error deleting post: ', error);
                alert('게시글 삭제 중 오류가 발생하였습니다.');
            }
        }
    }

    async toggleLike() {
        try {
            const response = await fetch(`/api/likes/${this.postId}`, {
                method: 'POST'
            });

            if (response.ok) {
                await this.updateLikeCount();
            } else {
                // 이미 좋아요를 누른 상태라면 좋아요 삭제
                const removeResponse = await fetch(`/api/likes/${this.postId}/delete`, {
                    method: 'DELETE'
                });

                if (removeResponse.ok) {
                    await this.updateLikeCount();
                }
            }
        } catch (error) {
            console.error('Error toggling like: ', error);
            alert('좋아요를 처리하는 중 오류가 발생하였습니다.');
        }
    }

    async updateLikeCount() {
        try {
            const response = await fetch(`/api/likes/${this.postId}/count`);
            const likeCount = await response.json();
            document.getElementById('like-count').innerText = likeCount;
        } catch (error) {
            console.error('Error updating like count: ', error);
        }
    }

    async commentAll() {
        try {
            const response = await fetch(`/api/comments/list/${this.postId}`);
            if (!response.ok) {
                throw new Error('댓글을 가져오는 데 실패하였습니다.');
            }
            const comments = await response.json();

            const commentsDiv = document.getElementById('comments');
            commentsDiv.innerHTML = '';  // 기존 내용 삭제

            comments.forEach(comment => {
                const div = document.createElement('div');
                div.className = 'comment';
                div.innerHTML = `
                    <div class="comment-profile">
                        <img src="${comment.profileImageUrl || '/images/BeanContainer.png'}" alt="프로필 이미지"/>
                    </div>
                    <div class="comment-content">
                        <div class="comment-header" style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <span class="comment-author"><strong>${comment.nickname}</strong></span>
                                <span class="comment-date">${new Date(comment.createdAt).toLocaleString()}</span>
                            </div>
                            ${comment.authorCheck ? `
                            <i class="fas fa-ellipsis-v" onclick="postOps.toggleCommentOptions(${comment.id})" style="cursor: pointer;"></i>
                            ` : ''}
                        </div>
                        <div class="comment-body" id="comment-content-${comment.id}">${comment.content}</div>
                        <div class="comment-actions" id="comment-actions-${comment.id}" style="display: none; margin-top: 5px;">
                            <button onclick="postOps.updateComment(${comment.id}, '${comment.content}')">수정</button>
                            <button onclick="postOps.deleteComment(${comment.id})">삭제</button>
                        </div>
                    </div>
                `;

                commentsDiv.appendChild(div);
            });

        } catch (error) {
            console.error('Error fetching comments', error);
            const commentsDiv = document.getElementById('comments');
            commentsDiv.innerHTML = '<p>댓글을 불러오는 중 오류가 발생하였습니다.</p>';
        }
    }

    toggleCommentOptions(commentId) {
        const commentActions = document.getElementById(`comment-actions-${commentId}`);
        if (commentActions.style.display === 'none' || commentActions.style.display === '') {
            commentActions.style.display = 'block';
        } else {
            commentActions.style.display = 'none';
        }
    }

    async saveOrUpdateComment(isUpdate, commentId = null) {
        let content;

        if (isUpdate) {
            content = document.getElementById(`update-content-${commentId}`).value;
        } else {
            content = document.getElementById('comment-input').value;
        }

        const endpoint = isUpdate
            ? `/api/comments/${this.postId}/${commentId}/update`
            : `/api/comments/${this.postId}`;

        const method = isUpdate ? 'PUT' : 'POST';

        const response = await fetch(endpoint, {
            method: method,
            headers: {
                'Content-Type' : 'application/json'
            },
            body: JSON.stringify({content})
        });

        if (response.ok) {
            if (!isUpdate) {
                document.getElementById('comment-input').value = '';    // 입력 초기화
            }
            await this.commentAll();    // 댓글 새로 고침
        } else {
            alert(isUpdate ? '댓글 수정 실패' : '댓글 등록 실패');
        }
    }

    cancelUpdate(commentId, content) {
        const commentBody = document.getElementById(`comment-content-${commentId}`);
        commentBody.innerHTML = content;

        const commentActions = document.getElementById(`comment-actions-${commentId}`);
        commentActions.innerHTML = `
        <button onclick="postOps.updateComment(${commentId}, '${content}')">수정</button>
        <button onclick="postOps.deleteComment(${commentId})">삭제</button>
    `;
        commentActions.style.display = 'none';  // 취소 시에는 다시 숨김
    }

    async deleteComment(commentId) {
        if(confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
            try {
                const response = await fetch(`/api/comments/${this.postId}/${commentId}/delete`, {
                    method: 'DELETE',
                });

                if(response.ok) {
                    alert('댓글이 삭제되었습니다.');
                    await this.commentAll();    // 댓글 새로고침
                } else {
                    const errorMsg = await response.text();
                    alert(errorMsg || '댓글 삭제에 실패하였습니다.');
                }
            } catch (error) {
                console.error('Error deleting comment: ', error);
                alert('댓글 삭제 중 오류가 발생하였습니다.');
            }
        }
    }

    async updateComment(commentId, currentContent) {
        const commentBody = document.getElementById(`comment-content-${commentId}`);
        const commentActions = document.getElementById(`comment-actions-${commentId}`);

        // 댓글 내용을 텍스트 영역으로 변경하여 수정할 수 있게 함
        commentBody.innerHTML = `
        <textarea id="update-content-${commentId}">${currentContent}</textarea>
    `;

        // "수정" 버튼을 "저장" 버튼으로 변경하고 취소 버튼 추가
        commentActions.innerHTML = `
        <button onclick="postOps.saveOrUpdateComment(true, ${commentId})">저장</button>
        <button onclick="postOps.cancelUpdate(${commentId}, '${currentContent}')">취소</button>
    `;
        commentActions.style.display = 'block';  // 수정 시에는 항상 보이도록 함
    }

}

const postId = window.location.pathname.split('/').pop();   // URL에서 postId 추출
const postOps = new PostOperations(postId);

document.addEventListener('DOMContentLoaded', () =>  {
    postOps.fetchPost();    // 페이지 로드 후 함수 호출
    document.getElementById('like-button').addEventListener('click', () => postOps.toggleLike());
    document.getElementById('submit-comment').addEventListener('click', () => postOps.saveOrUpdateComment(false));
});

