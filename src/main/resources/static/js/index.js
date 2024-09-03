let map, ps, infowindow, markers = {};
let lastCenter = null;
let selectedCategories = new Set();
let isCategorySearch = false; // isCategorySearch 변수를 선언하고 초기화
let excludeBrands = false; // 브랜드 제외 여부

const brandNames = [
    "스타벅스", "메가커피", "투썸플레이스", "빽다방", "컴포즈커피",
    "이디야", "할리스", "파스쿠찌", "더벤티", "폴바셋",
    "커피빈", "엔제리너스", "카페베네", "하삼동커피", "감성커피",
    "매머드커피", "달콤커피", "탐앤탐스", "커피나무", "커피베이",
    "커피명가", "드롭탑", "커피에반하다", "커피스미스", "커피마마",
    "더카페", "만랩커피", "셀렉토커피", "토프레소", "빈스빈스",
    "그라찌에", "전광수커피", "카페보니또", "더착한커피"
];

const categories = [
    "대형카페", "편한 좌석", "주차가 가능한", "24시간 카페", "룸", "테라스",
    "와이파이", "데이트 하기 좋은", "혼자가기 좋은", "공부하기 좋은", "비즈니스 미팅",
    "애견 동반", "분위기 좋은", "인스타 감성", "풍경이 좋은", "새로 오픈", "조용한",
    "커피가 맛있는", "디저트가 맛있는", "직원이 친절한"
];

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
    const mapContainer = document.getElementById('map'),
        mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 3
        };

    map = new kakao.maps.Map(mapContainer, mapOption);

    var zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.LEFT);

    ps = new kakao.maps.services.Places();
    infowindow = new kakao.maps.InfoWindow({zIndex: 1});

    // 지도 클릭 시 인포창 닫기
    kakao.maps.event.addListener(map, 'click', function () {
        infowindow.close();
    });

    kakao.maps.event.addListener(map, 'dragend', function () {
        const center = map.getCenter();
        if (!isCategorySearch && (!lastCenter || !center.equals(lastCenter))) {
            lastCenter = center;
            searchPlacesByCenter(center);
        }
    });

    kakao.maps.event.addListener(map, 'zoom_changed', function () {
        const center = map.getCenter();
        if (!isCategorySearch && (!lastCenter || !center.equals(lastCenter))) {
            lastCenter = center;
            searchPlacesByCenter(center);
        }
    });

    document.getElementById('districts').addEventListener('change', function () {
        const district = this.value;
        if (district && districts[district]) {
            const center = new kakao.maps.LatLng(districts[district].lat, districts[district].lng);
            map.setCenter(center);
            map.setLevel(3);
            searchPlacesByCenter(center);
        }
    });

    document.getElementById('brand-exclude-checkbox').addEventListener('change', function () {
        excludeBrands = this.checked;
        console.log('excludeBrands:', excludeBrands);
        clearMarkers();
        searchPlacesByCenter(map.getCenter());
    });

    document.getElementById('search-btn').addEventListener('click', searchCafeByName);

    searchPlacesByCenter(map.getCenter());
    createCategoryButtons();
    loadRandomMaps();

}

function toggleExcludeBrands() {
    excludeBrands = !excludeBrands;
    clearMarkers();
    searchPlacesByCenter(map.getCenter());
}

function searchPlacesByCenter(center) {
    const keyword = '카페';


    ps.keywordSearch(keyword, function (data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
            for (var i = 0; i < data.length; i++) {
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

function isExcludedCategory(categoryName) {
    const excludedCategories = ['보드게임카페', '룸카페', '방탈출카페', '테마카페', '고양이카페', '북카페', '레드버튼', '벌툰', '키즈'];
    return excludedCategories.some(excluded => categoryName.includes(excluded));
}

function isExcludedBrand(placeName) {
    if (!excludeBrands) return false;
    return brandNames.some(brand => placeName.includes(brand));
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

    fetch(`/api/cafes/${kakaoId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text)
            });
        }
        return response.json();
    })
        .then(data => {
            // 리뷰 페이지를 새 창으로 띄우기
            window.open(`/review/${data.id}`, '_blank');
        }).catch(error => {
        alert('Error: ' + error.message);
    });
}

function displayMarker(place) {
    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
    });

    markers[place.id] = marker;

    kakao.maps.event.addListener(marker, 'click', function () {
        const parsedAddress = parseAddress(place.road_address_name || place.address_name);

        var content = `
        <div id="custom-infowindow" class="kakao-infowindow-content">
            <strong class="place-name">${place.place_name}</strong><br>
        <button class="infowindow-link" onclick="checkAndSaveCafe('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x}, '${parsedAddress.city}', '${parsedAddress.district}')">리뷰 보기</button>
        </div>
    `;
        infowindow.setContent(content);
        infowindow.open(map, marker);
    });
}

function displayDbMarker(cafe) {
    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(cafe.latitude, cafe.longitude)
    });

    markers[cafe.id] = marker;

    kakao.maps.event.addListener(marker, 'click', function () {
        var content = `
        <div style="padding:5px;
         font-size:12px;
         max-width: 200px;
          white-space: normal;
          word-wrap: break-word;
          word-break: break-all;
          overflow: hidden;
         ">
       
            <strong>${cafe.name}</strong><br>
            <button class="infowindow-link" onclick="window.open('/review/${cafe.id}', '_blank')">리뷰 보기</button>
        </div>
    `;
        infowindow.setContent(content);
        infowindow.open(map, marker);
    });
}

function clearMarkers() {
    for (let key in markers) {
        markers[key].setMap(null);
    }
    markers = {};
}

function searchPlacesByCategory(categories) {
    clearMarkers();

    const excludeBrands = document.getElementById('brand-exclude-checkbox').checked;

    fetch(`/api/cafes?` + new URLSearchParams({
        categories: Array.from(categories).join(','),
        excludeBrands: excludeBrands // 브랜드 제외 파라미터 추가
    }))
        .then(response => response.json())
        .then(data => {
            data.forEach(cafe => {
                displayDbMarker(cafe);
            });
        })
        .catch(error => {
            alert('Error: ' + error.message);
        });
}

// 랜덤 지도를 불러와서 카드 형태로 표시하는 함수
async function loadRandomMaps() {
    try {
        const response = await fetch('/api/maps/random');
        if (!response.ok) {
            throw new Error('랜덤 지도를 불러오는데 실패했습니다.');
        }
        const mapList = await response.json();

        // 최대 3개의 카드만 표시
        displayMapCards(mapList.slice(0, 3));
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

function displayMapCards(mapList) {
    const container = document.getElementById('map-cards-container');
    container.innerHTML = ''; // 기존의 카드를 초기화

    mapList.forEach(map => {
        const card = document.createElement('div');
        card.className = 'map-card';
        card.innerHTML = `
            <h3>${map.mapName}</h3>
            <p>작성자: ${map.username}</p>
        `;
        card.addEventListener('click', () => {
            window.location.href = `/mymap/${map.mapId}`;
        });
        container.appendChild(card);
    });
}


function onCategorySelect() {
    if (selectedCategories && selectedCategories.size > 0) {
        isCategorySearch = true;  // 카테고리 검색이 활성화됨
        searchPlacesByCategory(selectedCategories);
    } else {
        alert("최소 한 개의 카테고리를 선택해주세요.");
    }
}

function createCategoryButtons() {
    const availableList = document.querySelector('.available-categories');
    const selectedList = document.querySelector('.selected-categories');

    // 기존 카테고리 버튼을 초기화
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

function resetCategories() {
    // 선택된 카테고리 초기화
    selectedCategories.clear();
    createCategoryButtons();
    isCategorySearch = false;  // 카테고리 검색 비활성화

    // 브랜드 제외 체크박스 해제
    const brandExcludeCheckbox = document.getElementById('brand-exclude-checkbox');
    brandExcludeCheckbox.checked = false;
    excludeBrands = false; // Boolean 값도 false로 설정

    // 지도 상에서 다시 카페 검색 수행
    searchPlacesByCenter(map.getCenter());
}


async function searchCafeByName() {
    const keyword = document.getElementById('search-input').value;
    if (!keyword) {
        alert('카페 이름을 입력하세요.');
        return;
    }

    ps.keywordSearch(keyword, function (data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
            if (data.length === 1) {
                const place = data[0];
                const center = new kakao.maps.LatLng(place.y, place.x);
                map.setCenter(center);
                map.setLevel(3);
                searchPlacesByCenter(center);
                openInfoWindow(place);
            } else {
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

            // 마커가 이미 존재하는지 확인
            if (markers[place.id]) {
                openInfoWindow(place);
            } else {
                // 마커가 아직 없을 경우 약간의 지연 후 재시도
                setTimeout(() => {
                    if (markers[place.id]) {
                        openInfoWindow(place);
                    }
                }, 200);
            }

            searchResultsContainer.innerHTML = '';
        };
        searchResultsContainer.appendChild(resultItem);
    });
}

function openInfoWindow(place) {
    const marker = markers[place.id];
    const parsedAddress = parseAddress(place.road_address_name || place.address_name);
    const content = `
        <div style="padding:5px; font-size:12px;">
            <strong>${place.place_name}</strong><br>
            <button class="infowindow-link" onclick="checkAndSaveCafe('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x}, '${parsedAddress.city}', '${parsedAddress.district}')">리뷰 보기</button>
        </div>
    `;

    infowindow.setContent(content);
    infowindow.open(map, marker);
}



function toggleDropdown() {
    var dropdown = document.getElementById('dropdown');
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
}

function selectDistrict(district) {
    document.getElementById('dropdown').style.display = 'none';
    if (districts[district]) {
        const center = new kakao.maps.LatLng(districts[district].lat, districts[district].lng);
        map.setCenter(center);
        map.setLevel(3);
        lastCenter = center;
        searchPlacesByCenter(center);
    }
}

function toggleMenu() {
    var menu = document.getElementById("menu");
    menu.style.display = menu.style.display === "block" ? "none" : "block";
}

function closeMenu() {
    var menu = document.getElementById("menu");
    menu.style.display = "none";
}

document.addEventListener('click', function (event) {
    var hamburger = document.getElementById('hamburger');
    var menu = document.getElementById('menu');
    if (hamburger && menu && !hamburger.contains(event.target) && !menu.contains(event.target)) {
        closeMenu();
    }
});

window.onload = function () {
    initMap();
};


function createCustomOverlay(content, position) {
    return new kakao.maps.CustomOverlay({
        map: map,
        position: position,
        content: content,
        yAnchor: 1,
        zIndex: 3
    });
}


