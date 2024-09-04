
let map, ps, infowindow, markers = {}, addedCafes = new Map(), isCategorySearch = false;
let excludeBrands = false;
const categories = [
    "대형카페", "편한 좌석", "주차가 가능한", "24시간 카페", "룸", "테라스",
    "와이파이", "데이트 하기 좋은", "혼자가기 좋은", "공부하기 좋은", "비즈니스 미팅",
    "애견 동반", "분위기 좋은", "인스타 감성", "풍경이 좋은", "새로 오픈", "조용한",
    "커피가 맛있는", "디저트가 맛있는", "직원이 친절한"
];
const selectedCategories = new Set();

const districts = {
    '강남구': {lat: 37.5172, lng: 127.0473},
    '강동구': {lat: 37.5301, lng: 127.1238},
    '강북구': {lat: 37.6396, lng: 127.0255},
    '강서구': {lat: 37.5509, lng: 126.8495},
    '관악구': {lat: 37.4789, lng: 126.9516},
    '광진구': {lat: 37.5385, lng: 127.0823},
    '구로구': {lat: 37.4955, lng: 126.8879},
    '금천구': {lat: 37.4600, lng: 126.9002},
    '노원구': {lat: 37.6543, lng: 127.0563},
    '도봉구': {lat: 37.6688, lng: 127.0477},
    '동대문구': {lat: 37.5743, lng: 127.0398},
    '동작구': {lat: 37.5124, lng: 126.9392},
    '마포구': {lat: 37.5637, lng: 126.9087},
    '서대문구': {lat: 37.5791, lng: 126.9368},
    '서초구': {lat: 37.4837, lng: 127.0323},
    '성동구': {lat: 37.5634, lng: 127.0371},
    '성북구': {lat: 37.5894, lng: 127.0167},
    '송파구': {lat: 37.5145, lng: 127.1056},
    '양천구': {lat: 37.5162, lng: 126.8665},
    '영등포구': {lat: 37.5265, lng: 126.8965},
    '용산구': {lat: 37.5323, lng: 126.9908},
    '은평구': {lat: 37.6027, lng: 126.9290},
    '종로구': {lat: 37.5733, lng: 126.9793},
    '중구': {lat: 37.5636, lng: 126.9976},
    '중랑구': {lat: 37.6063, lng: 127.0928}
};

function initMap() {
    const mapContainer = document.getElementById('map');
    const mapOption = {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 3
    };

    map = new kakao.maps.Map(mapContainer, mapOption);
    infowindow = new kakao.maps.InfoWindow({zIndex: 1});

    const zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.LEFT);

    ps = new kakao.maps.services.Places();

    kakao.maps.event.addListener(map, 'click', function () {
        infowindow.close();
    });

    kakao.maps.event.addListener(map, 'dragend', function () {
        if (!isCategorySearch) searchPlacesByCenter(map.getCenter());
    });

    kakao.maps.event.addListener(map, 'zoom_changed', function () {
        if (!isCategorySearch) searchPlacesByCenter(map.getCenter());
    });

    document.getElementById('brand-exclude-checkbox').addEventListener('change', function () {
        excludeBrands = this.checked;
        console.log('excludeBrands:', excludeBrands);
        clearMarkers();
        searchPlacesByCenter(map.getCenter());
    });

    document.getElementById('districts').addEventListener('change', function () {
        const district = this.value;
        if (district && districts[district]) {
            const center = new kakao.maps.LatLng(districts[district].lat, districts[district].lng);
            map.setCenter(center);
            map.setLevel(3);
            if (!isCategorySearch) {
                searchPlacesByCenter(center);
            }
        }
    });

    loadMapDetails();
    searchPlacesByCenter(map.getCenter());
    createCategoryButtons();
}

function toggleExcludeBrands() {
    excludeBrands = !excludeBrands;
    clearMarkers();
    searchPlacesByCenter(map.getCenter());
}


function isExcludedBrand(placeName) {
    if (!excludeBrands) return false;
    const brandNames = [
        "스타벅스", "메가커피", "투썸플레이스", "빽다방", "컴포즈커피",
        "이디야", "할리스", "파스쿠찌", "더벤티", "폴바셋",
        "커피빈", "엔제리너스", "카페베네", "하삼동커피", "감성커피",
        "매머드커피", "달콤커피", "탐앤탐스", "커피나무", "커피베이",
        "커피명가", "드롭탑", "커피에반하다", "커피스미스", "커피마마",
        "더카페", "만랩커피", "셀렉토커피", "토프레소", "빈스빈스",
        "그라찌에", "전광수커피", "카페보니또", "더착한커피"
    ];
    return brandNames.some(brand => placeName.includes(brand));
}

function loadMapDetails() {
    const mapId = window.location.pathname.split("/").pop();

    fetch(`/api/maps/${mapId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('map-name').value = data.mapName;
            // 공개 여부가 false이면 체크박스를 체크 상태로 설정
            document.getElementById('is-public').checked = !data.isPublic;
            data.cafes.forEach(cafe => {
                addCafeToSet(cafe.kakaoId, cafe.name, cafe.address, cafe.latitude, cafe.longitude);
            });
        })
        .catch(error => {
            console.error('지도 세부 정보 로드 오류:', error);
        });
}


function isExcludedCategory(categoryName) {
    const excludedCategories = ['보드게임카페', '룸카페', '방탈출카페', '테마카페', '고양이카페', '북카페', '레드버튼', '벌툰', '키즈'];
    return excludedCategories.some(excluded => categoryName.includes(excluded));
}

function searchPlacesByCenter(center) {
    const keyword = '카페';
    ps.keywordSearch(keyword, function (data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
            for (let i = 0; i < data.length; i++) {
                if (!isExcludedCategory(data[i].category_name) && !isExcludedBrand(data[i].place_name) && !markers[data[i].id]) {
                    displayMarker(data[i]);
                }
            }
            if (pagination.hasNextPage) {
                pagination.nextPage();
            }
        }
    }, {location: center, radius: 2000});
}


function searchCafeByName() {
    const keyword = document.getElementById('search-input').value;
    if (!keyword) {
        alert('카페 이름을 입력하세요.');
        return;
    }

    ps.keywordSearch(keyword, function (data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
            // 검색 결과가 하나일 때도 displaySearchResults 호출
            if (data.length === 1) {
                displaySearchResults(data); // 결과가 하나라도 result 창에 표시
            } else if (data.length > 1) {
                displaySearchResults(data);
            }
        } else {
            alert('카페를 찾을 수 없습니다.');
        }
    });
}

function displaySearchResults(places) {
    const searchResultsContainer = document.getElementById('search-results');
    searchResultsContainer.innerHTML = '';

    places.forEach(place => {
        const resultItem = document.createElement('div');
        resultItem.textContent = `${place.place_name} (${place.road_address_name || place.address_name})`;
        resultItem.style.cursor = 'pointer';
        resultItem.onclick = () => {
            const center = new kakao.maps.LatLng(place.y, place.x);
            map.setCenter(center);
            map.setLevel(3);

            searchPlacesByCenter(center);

            if (markers[place.id]) {
                openInfoWindow(markers[place.id], place);
            } else {
                setTimeout(() => {
                    if (markers[place.id]) {
                        openInfoWindow(markers[place.id], place);
                    }
                }, 200);
            }

            searchResultsContainer.innerHTML = '';
        };
        searchResultsContainer.appendChild(resultItem);
    });
}

function displayMarker(place) {
    const isAddedCafe = addedCafes.has(String(place.id));

    let markerImageUrl = isAddedCafe
        ? "http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png"
        : null;

    let markerImage = markerImageUrl
        ? new kakao.maps.MarkerImage(markerImageUrl, new kakao.maps.Size(24, 35), {offset: new kakao.maps.Point(12, 35)})
        : null;

    const marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x),
        image: markerImage
    });

    markers[place.id] = marker;

    kakao.maps.event.addListener(marker, 'click', function () {
        openInfoWindow(marker, place);
    });
}

function openInfoWindow(marker, place) {
    const parsedAddress = parseAddress(place.road_address_name || place.address_name);
    const content = `
            <div style="padding:5px; font-size:12px; justify-content: center">
                <strong>${place.place_name}</strong><br>
                <button class="infowindow-link" onclick="checkAndSaveCafe('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x}, '${parsedAddress.city}', '${parsedAddress.district}')">지도에 추가</button>
                <button class="infowindow-link" onclick="openReviewPage('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x})">리뷰 보기</button>
            </div>
        `;
    infowindow.setContent(content);
    infowindow.open(map, marker);
}

function addCafeToSet(kakaoId, name, address, latitude, longitude) {
    kakaoId = String(kakaoId);

    if (!addedCafes.has(kakaoId)) {
        const cafeObj = {kakaoId, name, address, latitude, longitude};
        addedCafes.set(kakaoId, cafeObj);
        console.log(`Added cafe to map: ${kakaoId}, Current map size: ${addedCafes.size}`);

        const card = document.createElement('div');
        card.className = 'cafe-card';
        card.setAttribute('data-id', kakaoId);
        card.innerHTML = `
                <h4>${name}</h4>
                <p>${address}</p>
                <button class="remove-btn font" onclick="removeCafe('${kakaoId}')">삭제</button>
            `;

        card.addEventListener('click', () => handleCardClick(kakaoId));
        document.getElementById('cafe-cards').appendChild(card);

        const marker = markers[kakaoId];
        if (marker) {
            const markerImageUrl = "http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
            const markerImage = new kakao.maps.MarkerImage(
                markerImageUrl,
                new kakao.maps.Size(24, 35),
                {offset: new kakao.maps.Point(12, 35)}
            );
            marker.setImage(markerImage);
        }
    } else {
        console.log(`Cafe with ID ${kakaoId} already exists in map.`);
    }
}

function handleCardClick(kakaoId) {
    kakaoId = String(kakaoId);
    console.log(`Handling click for Cafe ID: ${kakaoId}`);
    const cafe = addedCafes.get(kakaoId);
    if (cafe) {
        const position = new kakao.maps.LatLng(cafe.latitude, cafe.longitude);
        map.setCenter(position);

        if (!markers[kakaoId]) {
            displayMarker({
                id: kakaoId,
                place_name: cafe.name,
                road_address_name: cafe.address,
                y: cafe.latitude,
                x: cafe.longitude
            });
        } else {
            openInfoWindow(markers[kakaoId], cafe);
        }

        searchPlacesByCenter(position);
    } else {
        console.error(`Cafe with ID ${kakaoId} not found in map.`);
    }
}

function removeCafe(kakaoId) {
    kakaoId = String(kakaoId);
    if (addedCafes.delete(kakaoId)) {
        console.log(`Removed cafe from map: ${kakaoId}, Current map size: ${addedCafes.size}`);
    } else {
        console.error(`Cafe with ID ${kakaoId} not found in map.`);
    }

    const card = document.querySelector(`.cafe-card[data-id="${kakaoId}"]`);
    if (card) {
        card.remove();
    }

    const marker = markers[kakaoId];
    if (marker) {
        marker.setMap(null);
        delete markers[kakaoId];
    }
}

function saveMap() {
    const mapId = window.location.pathname.split("/").pop();
    const mapName = document.getElementById('map-name').value;
    // 체크박스가 체크된 경우 isPublic을 false로, 체크되지 않은 경우 true로 설정
    const isPublic = !document.getElementById('is-public').checked;
    const kakaoIds = Array.from(addedCafes.keys());

    const mapData = {
        mapId: mapId,
        mapName: mapName,
        kakaoIds: kakaoIds,
        isPublic: isPublic // 공개 여부 포함
    };

    fetch(`/api/maps/${mapId}/update`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(mapData)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.text();
        })
        .then(data => {
            alert('지도 업데이트 성공!');
            window.location.href = '/';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('지도 업데이트 실패: ' + error.message);
        });
}


function createCategoryButtons() {
    const availableList = document.querySelector('.available-categories');
    const selectedList = document.querySelector('.selected-categories');

    availableList.innerHTML = '';
    selectedList.innerHTML = '';

    categories.forEach(category => {
        const item = document.createElement('div');
        item.textContent = category;
        item.classList.add('category-item');

        item.onclick = () => {
            if (selectedCategories.has(category)) {
                selectedCategories.delete(category);
                selectedList.removeChild(item);
                availableList.appendChild(item);
            } else {
                if (selectedCategories.size < 3) {
                    selectedCategories.add(category);
                    availableList.removeChild(item);
                    selectedList.appendChild(item);
                } else {
                    alert("카테고리는 최대 3개까지만 선택할 수 있습니다.");
                }
            }
        };

        availableList.appendChild(item);
    });
}

function onCategorySelect() {
    if (selectedCategories.size > 0) {
        isCategorySearch = true;
        const selectedCategoryArray = Array.from(selectedCategories);

        // 카테고리 값을 쉼표로 조인하고, 브랜드 제외 여부를 쿼리 스트링에 포함
        const queryString = `categories=${encodeURIComponent(selectedCategoryArray.join(','))}&excludeBrands=${excludeBrands}`;

        // 요청을 보내는 부분
        fetch(`/api/cafes?${queryString}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('카페 데이터를 불러오는 중 오류가 발생했습니다.');
                }
                return response.json();
            })
            .then(data => {
                clearMarkers();
                data.forEach(cafe => {
                    displayMarker({
                        id: cafe.kakaoId,
                        place_name: cafe.name,
                        road_address_name: cafe.address,
                        y: cafe.latitude,
                        x: cafe.longitude
                    });
                });
            })
            .catch(error => {
                console.error('Error:', error);
                alert('카테고리 검색에 실패했습니다.');
            });
    } else {
        alert("카테고리를 선택하세요.");
    }
}




function resetCategories() {
    isCategorySearch = false;
    selectedCategories.clear();
    createCategoryButtons();
    const brandExcludeCheckbox = document.getElementById('brand-exclude-checkbox');
    brandExcludeCheckbox.checked = false;
    excludeBrands = false;
    searchPlacesByCenter(map.getCenter());
}


function clearMarkers() {
    for (let id in markers) {
        markers[id].setMap(null);
    }
    markers = {};
}

function parseAddress(address) {
    const parts = address.split(' ');
    let city = parts[0];
    let district = '';
    for (let i = 1; i < parts.length; i++) {
        if (parts[i].endsWith('구') || parts[i].endsWith('군')) {
            district = parts[i];
            break;
        }
    }
    return {city, district};
}

function checkAndSaveCafe(kakaoId, name, address, latitude, longitude, city, district) {
    const data = {
        kakaoId: kakaoId,
        name: name,
        address: address,
        latitude: latitude,
        longitude: longitude,
        city: city,
        district: district
    };

    console.log("Sending data:", data);

    fetch(`/api/cafes/${kakaoId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text);
            });
        }
        return response.json();
    })
        .then(data => {
            console.log("Cafe saved or found:", data);
            addCafeToSet(kakaoId, name, address, latitude, longitude);
        }).catch(error => {
        console.error('Error:', error);
        alert('Error: ' + error.message);
    });
}

function openReviewPage(kakaoId, name, address, latitude, longitude) {
    const parsedAddress = parseAddress(address);

    const data = {
        kakaoId: kakaoId,
        name: name,
        address: address,
        latitude: latitude,
        longitude: longitude,
        city: parsedAddress.city,
        district: parsedAddress.district
    };

    console.log("Sending data for review page:", data);

    fetch(`/api/cafes/${kakaoId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text);
            });
        }
        return response.json();
    })
        .then(data => {
            console.log("Cafe saved or found:", data);
            window.open(`/review/${data.id}`, '_blank');
        }).catch(error => {
        console.error('Error:', error);
        alert('Error: ' + error.message);
    });
}

document.getElementById('save-btn').addEventListener('click', saveMap);
document.getElementById('search-btn').addEventListener('click', searchCafeByName);
document.addEventListener('DOMContentLoaded', initMap);
