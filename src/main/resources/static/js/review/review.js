const selectedCategories = new Set();
const editFormCategories = new Map();
let isEditing = false;
let currentEditId = null;
let reviewsData = [];
let totalPages = 0;
let currentPage = 0;  // 현재 페이지 번호
const pageSize = 10;  // 한 페이지에 보여줄 리뷰 수

// 리뷰 작성 폼의 체크박스 이벤트 리스너
document.querySelectorAll('.review-form .categories input[type="checkbox"]').forEach(checkbox => {
    checkbox.addEventListener('change', (event) => {
        if (event.target.checked) {
            selectedCategories.add(event.target.value);
        } else {
            selectedCategories.delete(event.target.value);
        }
    });
});

function updateSelectedCategories(formType, categories, reviewId) {
    const categorySet = formType === 'edit' ? editFormCategories.get(reviewId) : selectedCategories;
    categorySet.clear();
    categories.forEach(category => {
        categorySet.add(category);
    });

    document.querySelectorAll(`.${formType}-form-${reviewId} .categories input[type="checkbox"]`).forEach(checkbox => {
        checkbox.checked = categorySet.has(checkbox.value);
    });
}

document.getElementById('submit-review').addEventListener('click', async () => {
    const ratingElement = document.querySelector('.review-form input[name="rating"]:checked');
    const content = document.getElementById('review-content').value.trim();
    const categoryNames = Array.from(selectedCategories);

    // 별점, 카테고리, 내용이 모두 비어있는지 확인
    if (!ratingElement && categoryNames.length === 0 && content === '') {
        alert('별점, 카테고리, 내용을 하나 이상 입력해주세요.');
        return;
    }

    // 별점만 없는 경우
    if (!ratingElement) {
        alert('별점을 선택해주세요.');
        return;
    }

    // 내용만 없는 경우
    if (content === '') {
        alert('리뷰 내용을 입력해주세요.');
        return;
    }

    // 카테고리만 없는 경우
    if (categoryNames.length === 0) {
        alert('카테고리를 하나 이상 선택해주세요.');
        return;
    }

    const path = window.location.pathname;
    const cafeId = path.split('/').filter(Boolean).pop();

    const reviewData = {
        score: ratingElement.value,
        content: content,
        categoryNames: categoryNames,
        cafeId: cafeId
    };

    const url = '/api/reviews';
    const method = 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reviewData)
        });

        if (response.ok) {
            alert('리뷰가 등록되었습니다.');
            fetchReviews();

            // 리뷰 작성 후 입력 필드와 체크박스 초기화
            document.getElementById('review-content').value = '';
            if (ratingElement) {
                ratingElement.checked = false;
            }
            selectedCategories.clear();

            document.querySelectorAll('.review-form .categories input[type="checkbox"]').forEach(checkbox => {
                checkbox.checked = false;
            });
        } else {
            const errorData = await response.json();
            alert(`${errorData.message}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('리뷰 작성에 실패했습니다.');
    }
});



async function fetchReviews() {
    const path = window.location.pathname;
    const cafeId = path.split('/').filter(Boolean).pop();

    try {
        const response = await fetch(`/api/cafes/${cafeId}/reviews?page=${currentPage}&size=${pageSize}`);
        const reviews = await response.json();
        reviewsData = reviews.content;
        totalPages = reviews.totalPages;  // 총 페이지 수 업데이트
        displayReviews(reviewsData);
        updatePagination();
    } catch (error) {
        console.error('Error:', error);
        alert('리뷰 정보를 불러오는데 실패 했습니다.');
    }
}

function updatePagination() {
    const paginationDiv = document.getElementById('pagination');
    paginationDiv.innerHTML = '';  // 기존 버튼 제거

    // 이전 버튼
    if (currentPage > 0) {
        const prevButton = document.createElement('button');
        prevButton.innerText = '이전';
        prevButton.onclick = previousPage;
        paginationDiv.appendChild(prevButton);
    }

    // 페이지 번호 버튼
    for (let i = 0; i < totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.innerText = i + 1;
        pageButton.className = (i === currentPage) ? 'active' : '';
        pageButton.onclick = () => goToPage(i);
        paginationDiv.appendChild(pageButton);
    }

    // 다음 버튼
    if (currentPage < totalPages - 1) {
        const nextButton = document.createElement('button');
        nextButton.innerText = '다음';
        nextButton.onclick = nextPage;
        paginationDiv.appendChild(nextButton);
    }
}

function nextPage() {
    currentPage++;
    fetchReviews();
}

function previousPage() {
    if (currentPage > 0) {
        currentPage--;
        fetchReviews();
    }
}

function goToPage(page) {
    currentPage = page;
    fetchReviews();
}

// DOM에 페이지 네비게이션 버튼 추가
const paginationDiv = document.createElement('div');
paginationDiv.id = 'pagination';
document.body.appendChild(paginationDiv);

document.addEventListener('DOMContentLoaded', () => {
    fetchCafeInfo();
    fetchReviews();
});

async function fetchCafeInfo() {
    const path = window.location.pathname;
    const cafeId = path.split('/').filter(Boolean).pop();

    try {
        const response = await fetch(`/api/cafes/${cafeId}`);
        const cafe = await response.json();
        displayCafeInfo(cafe);
    } catch (error) {
        console.error('Error:', error);
        alert('카페 정보를 불러오는데 실패 했습니다.');
    }
}

function displayCafeInfo(cafe) {
    const cafeInfoDiv = document.createElement('div');
    cafeInfoDiv.innerHTML = `
        <h3>${cafe.name}</h3>
        <p>${cafe.address}</p>
        <p>카테고리 : ${Array.from(cafe.topCategories).join(', ')}</p>
        <p>별점 : ${cafe.averageScore ? cafe.averageScore.toFixed(2) : '0.0'}</p>
    `;
    document.getElementById('cafe-info').appendChild(cafeInfoDiv);
}

function displayReviews(reviews) {
    const reviewList = document.getElementById('review-list');
    reviewList.innerHTML = '';

    reviews.forEach(review => {
        const div = document.createElement('div');
        div.className = 'review-item';
        div.innerHTML = `
            <p><strong>별점 :</strong> 
            <span class="star-display">
                <span class="filled-stars">${'★'.repeat(review.score)}</span><span class="empty-stars">${'★'.repeat(5 - review.score)}</span>
            </span>
            </p>
            <p><strong>${review.nickName}:</strong> ${review.content}</p>
            <p><strong>카테고리 : </strong> ${review.categoryNames ? Array.from(review.categoryNames).join(', ') : 'None'}</p>
            <button class="btn edit-button" onclick="showEditForm(${review.id})">수정</button>
            <button class="btn delete-button" onclick="deleteReview(${review.id})">삭제</button>
        `;

        const editForm = document.createElement('div');
        editForm.className = `edit-form edit-form-${review.id}`;
        editForm.style.display = 'none';
        editForm.innerHTML = `
    <div class="star-rating">
        <input type="radio" id="edit-5-stars-${review.id}" name="edit-rating-${review.id}" value="5">
        <label for="edit-5-stars-${review.id}"></label>

        <input type="radio" id="edit-4-stars-${review.id}" name="edit-rating-${review.id}" value="4">
        <label for="edit-4-stars-${review.id}"></label>

        <input type="radio" id="edit-3-stars-${review.id}" name="edit-rating-${review.id}" value="3">
        <label for="edit-3-stars-${review.id}"></label>

        <input type="radio" id="edit-2-stars-${review.id}" name="edit-rating-${review.id}" value="2">
        <label for="edit-2-stars-${review.id}"></label>

        <input type="radio" id="edit-1-stars-${review.id}" name="edit-rating-${review.id}" value="1">
        <label for="edit-1-stars-${review.id}"></label>
    </div>

            <div class="categories edit-categories">
                <input type="checkbox" id="edit-category-${review.id}-1" value="대형카페"/>
                <label for="edit-category-${review.id}-1">대형카페</label>
                
                <input type="checkbox" id="edit-category-${review.id}-2" value="편한 좌석"/>
                <label for="edit-category-${review.id}-2">편한 좌석</label>
                
                <input type="checkbox" id="edit-category-${review.id}-3" value="주차가 가능한"/>
                <label for="edit-category-${review.id}-3">주차가 가능한</label>
                
                <input type="checkbox" id="edit-category-${review.id}-4" value="24시간 카페"/>
                <label for="edit-category-${review.id}-4">24시간 카페</label>
                
                <input type="checkbox" id="edit-category-${review.id}-5" value="룸"/>
                <label for="edit-category-${review.id}-5">룸</label>
                
                <input type="checkbox" id="edit-category-${review.id}-6" value="테라스"/>
                <label for="edit-category-${review.id}-6">테라스</label>
                
                <input type="checkbox" id="edit-category-${review.id}-7" value="와이파이"/>
                <label for="edit-category-${review.id}-7">와이파이</label>
                
                <input type="checkbox" id="edit-category-${review.id}-8" value="데이트 하기 좋은"/>
                <label for="edit-category-${review.id}-8">데이트 하기 좋은</label>
                
                <input type="checkbox" id="edit-category-${review.id}-9" value="혼자가기 좋은"/>
                <label for="edit-category-${review.id}-9">혼자가기 좋은</label>
                
                <input type="checkbox" id="edit-category-${review.id}-10" value="공부하기 좋은"/>
                <label for="edit-category-${review.id}-10">공부하기 좋은</label>
                
                <input type="checkbox" id="edit-category-${review.id}-11" value="비즈니스 미팅"/>
                <label for="edit-category-${review.id}-11">비즈니스 미팅</label>
                
                <input type="checkbox" id="edit-category-${review.id}-12" value="애견 동반"/>
                <label for="edit-category-${review.id}-12">애견 동반</label>
                
                <input type="checkbox" id="edit-category-${review.id}-13" value="분위기 좋은"/>
                <label for="edit-category-${review.id}-13">분위기 좋은</label>
                
                <input type="checkbox" id="edit-category-${review.id}-14" value="인스타 감성"/>
                <label for="edit-category-${review.id}-14">인스타 감성</label>
                
                <input type="checkbox" id="edit-category-${review.id}-15" value="풍경이 좋은"/>
                <label for="edit-category-${review.id}-15">풍경이 좋은</label>
                
                <input type="checkbox" id="edit-category-${review.id}-16" value="새로 오픈"/>
                <label for="edit-category-${review.id}-16">새로 오픈</label>
                
                <input type="checkbox" id="edit-category-${review.id}-17" value="조용한"/>
                <label for="edit-category-${review.id}-17">조용한</label>
                
                <input type="checkbox" id="edit-category-${review.id}-18" value="커피가 맛있는"/>
                <label for="edit-category-${review.id}-18">커피가 맛있는</label>
                
                <input type="checkbox" id="edit-category-${review.id}-19" value="디저트가 맛있는"/>
                <label for="edit-category-${review.id}-19">디저트가 맛있는</label>
                
                <input type="checkbox" id="edit-category-${review.id}-20" value="직원이 친절한"/>
                <label for="edit-category-${review.id}-20">직원이 친절한</label>
            </div>
            
                        <textarea id="edit-content-${review.id}" rows="4" cols="50"></textarea>
                        <br>
            <button class="btn edit-button" onclick="submitEditReview(${review.id})">수정</button>
            <button class="btn edit-button" onclick="cancelEditReview(${review.id})">취소</button>
        `;

        div.appendChild(editForm);
        reviewList.appendChild(div);

        // 수정 폼의 체크박스 이벤트 리스너 추가
        const currentEditFormCategories = new Set();
        editFormCategories.set(review.id, currentEditFormCategories);

        editForm.querySelectorAll('.categories input[type="checkbox"]').forEach(checkbox => {
            checkbox.addEventListener('change', (event) => {
                if (event.target.checked) {
                    currentEditFormCategories.add(event.target.value);
                } else {
                    currentEditFormCategories.delete(event.target.value);
                }
            });
        });
    });
}

function showEditForm(reviewId) {
    const review = reviewsData.find(r => r.id === reviewId);
    const reviewItem = document.querySelector(`.review-item button[onclick="showEditForm(${reviewId})"]`).parentElement;
    const editForm = reviewItem.querySelector('.edit-form');
    editForm.style.display = 'block';

    document.getElementById(`edit-content-${reviewId}`).value = review.content || '';
    const ratingInput = document.querySelector(`input[name="edit-rating-${reviewId}"][value="${review.score}"]`);

    if (ratingInput) {
        ratingInput.checked = true;
    } else {
        console.warn(`Rating input with value ${review.score} not found.`);
    }

    // 선택된 카테고리 초기화 및 설정
    const currentEditFormCategories = editFormCategories.get(reviewId);
    currentEditFormCategories.clear();  // 수정 폼 카테고리 초기화
    updateSelectedCategories('edit', review.categoryNames, reviewId);  // 수정 폼 카테고리 업데이트
}

async function submitEditReview(reviewId) {
    const rating = document.querySelector(`.edit-form-${reviewId} input[name="edit-rating-${reviewId}"]:checked`).value;
    const content = document.getElementById(`edit-content-${reviewId}`).value;
    const categoryNames = Array.from(editFormCategories.get(reviewId));

    const reviewData = {
        score: rating,
        content: content,
        categoryNames: categoryNames,
    };

    try {
        const response = await fetch(`/api/reviews/${reviewId}/update`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reviewData)
        });

        if (response.ok) {
            alert('리뷰를 수정했습니다.');
            fetchReviews();
        } else {
            const errorData = await response.json();
            alert(`${errorData.message}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('리뷰를 수정하는데 실패 했습니다.');
    }
}

function cancelEditReview(reviewId) {
    const reviewItem = document.querySelector(`.review-item button[onclick="showEditForm(${reviewId})"]`).parentElement;
    const editForm = reviewItem.querySelector('.edit-form');
    editForm.style.display = 'none';
}

async function deleteReview(reviewId) {
    if (!confirm('정말 리뷰를 삭제하겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/api/reviews/${reviewId}/delete`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('리뷰가 성공적으로 삭제되었습니다!');
            fetchReviews();
        } else {
            const errorData = await response.json();  // JSON 응답 파싱
            alert(`${errorData.message}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('리뷰 삭제에 실패했습니다.');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchReviews();
});