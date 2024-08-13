const selectedCategories = new Set();
const editFormCategories = new Map();
let isEditing = false;
let currentEditId = null;
let reviewsData = [];

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
    const rating = document.querySelector('.review-form input[name="rating"]:checked').value;
    const content = document.getElementById('review-content').value;
    const categoryNames = Array.from(selectedCategories);

    const path = window.location.pathname;
    const cafeId = path.split('/').filter(Boolean).pop();

    const reviewData = {
        score: rating,
        content: content,
        categoryNames: categoryNames,
        cafeId: cafeId
    };

    const url = '/api/review/create';
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
            alert('리뷰가 등록 됐습니다.');
            fetchReviews();
            document.getElementById('review-content').value = '';
            document.querySelector('.review-form input[name="rating"]:checked').checked = false;
            selectedCategories.clear();
            updateSelectedCategories('review', []);
        } else {
            alert('리뷰 작성에 실패 했습니다.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('리뷰 작성에 실패 했습니다.');
    }
});

async function fetchReviews() {
    const path = window.location.pathname;
    const cafeId = path.split('/').filter(Boolean).pop();

    try {
        const response = await fetch(`/api/reviewlist/${cafeId}`);
        const reviews = await response.json();
        reviewsData = reviews;

        displayReviews(reviews);
    } catch (error) {
        console.error('Error:', error);
        alert('리뷰 정보를 불러오는데 실패 했습니다.');
    }
}

async function fetchCafeInfo() {
    const path = window.location.pathname;
    const cafeId = path.split('/').filter(Boolean).pop();

    try {
        const response = await fetch(`/api/cafe/${cafeId}`);
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
            <p><strong>별점 : </strong> ${review.score}</p>
            <p><strong>${review.nickName}:</strong> ${review.content}</p>
            <p><strong>카테고리 : </strong> ${review.categoryNames ? Array.from(review.categoryNames).join(', ') : 'None'}</p>
            <button onclick="showEditForm(${review.id})">Edit</button>
            <button onclick="deleteReview(${review.id})">Delete</button>
        `;

        const editForm = document.createElement('div');
        editForm.className = `edit-form edit-form-${review.id}`;
        editForm.style.display = 'none';
        editForm.innerHTML = `
            <textarea id="edit-content-${review.id}" rows="4" cols="50"></textarea>
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
                <label><input type="checkbox" value="대형카페" /> 대형카페</label>
                <label><input type="checkbox" value="편한 좌석" /> 편한 좌석</label>
                <label><input type="checkbox" value="주차가 가능한" /> 주차가 가능한</label>
                <label><input type="checkbox" value="24시간 카페" /> 24시간 카페</label>
                <label><input type="checkbox" value="룸" /> 룸</label>
                <label><input type="checkbox" value="테라스" /> 테라스</label>
                <label><input type="checkbox" value="와이파이" /> 와이파이</label>
                <label><input type="checkbox" value="데이트 하기 좋은" /> 데이트 하기 좋은</label>
                <label><input type="checkbox" value="혼자가기 좋은" /> 혼자가기 좋은</label>
                <label><input type="checkbox" value="공부하기 좋은" /> 공부하기 좋은</label>
                <label><input type="checkbox" value="비즈니스 미팅" /> 비즈니스 미팅</label>
                <label><input type="checkbox" value="애견 동반" /> 애견 동반</label>
                <label><input type="checkbox" value="분위기 좋은" /> 분위기 좋은</label>
                <label><input type="checkbox" value="인스타 감성" /> 인스타 감성</label>
                <label><input type="checkbox" value="풍경이 좋은" /> 풍경이 좋은</label>
                <label><input type="checkbox" value="새로 오픈" /> 새로 오픈</label>
                <label><input type="checkbox" value="조용한" /> 조용한</label>
                <label><input type="checkbox" value="커피가 맛있는" /> 커피가 맛있는</label>
        <label><input type="checkbox" value="디저트가 맛있는" /> 디저트가 맛있는</label>
        <label><input type="checkbox" value="직원이 친절한" /> 직원이 친절한</label>
            </div>
            <button onclick="submitEditReview(${review.id})">Submit</button>
            <button onclick="cancelEditReview(${review.id})">Cancel</button>
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
        const response = await fetch(`/api/review/update/${reviewId}`, {
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
            alert('리뷰를 수정하는데 실패 했습니다.');
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
        const response = await fetch(`/api/review/delete/${reviewId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('리뷰가 성공적으로 삭제되었습니다!');
            fetchReviews();
        } else {
            alert('리뷰 삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('리뷰 삭제에 실패했습니다.');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchCafeInfo();
    fetchReviews();
});
