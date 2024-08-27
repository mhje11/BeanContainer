let map, ps, infowindow, markers = {}, addedCafes = new Set(), isMoving = false, isCategorySearch = false;
let excludeBrands = false;


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
    var mapContainer = document.getElementById('map'),
        mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 3
        };

    map = new kakao.maps.Map(mapContainer, mapOption);
    ps = new kakao.maps.services.Places();
    infowindow = new kakao.maps.InfoWindow({zIndex: 1});

    var zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.LEFT);

    searchPlacesByCenter(map.getCenter());

    kakao.maps.event.addListener(map, 'click', function () {
        infowindow.close();
    });

    kakao.maps.event.addListener(map, 'dragstart', function () {
        isMoving = true;
    });

    kakao.maps.event.addListener(map, 'dragend', function () {
        isMoving = false;
        if (!isCategorySearch) {
            searchPlacesByCenter(map.getCenter());
        }
    });

    kakao.maps.event.addListener(map, 'zoom_start', function () {
        isMoving = true;
    });

    kakao.maps.event.addListener(map, 'zoom_changed', function () {
        isMoving = false;
        if (!isCategorySearch) {
            searchPlacesByCenter(map.getCenter());
        }
    });

    document.getElementById('search-btn').addEventListener('click', searchCafeByName);

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

    createCategoryButtons();
}

function toggleExcludeBrands() {
    excludeBrands = !excludeBrands;
    clearMarkers();
    searchPlacesByCenter(map.getCenter());
}

function searchPlacesByCenter(center) {
    if (isMoving || isCategorySearch) return;

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


function isExcludedCategory(categoryName) {
    const excludedCategories = ['보드게임카페', '룸카페', '방탈출카페', '테마카페', '고양이카페', '북카페', '레드버튼', '벌툰', '키즈'];
    return excludedCategories.some(excluded => categoryName.includes(excluded));
}

function parseAddress(address) {
    const parts = address.split(' ');
    let city = parts[0], district = '';
    for (let i = 1; i < parts.length; i++) {
        if (parts[i].endsWith('구') || parts[i].endsWith('군')) {
            district = parts[i];
            break;
        }
    }
    return {city, district};
}

function displayMarker(place) {
    let markerImageUrl;

    if (addedCafes.has(place.id)) {
        markerImageUrl = "http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
    } else {
        markerImageUrl = null;
    }

    let markerImage = null;
    if (markerImageUrl) {
        markerImage = new kakao.maps.MarkerImage(
            markerImageUrl,
            new kakao.maps.Size(24, 35),
            {offset: new kakao.maps.Point(12, 35)}
        );
    }

    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x),
        image: markerImage
    });

    markers[place.id] = marker;

    kakao.maps.event.addListener(marker, 'click', function () {
        const parsedAddress = parseAddress(place.road_address_name || place.address_name);

        var content = `
                <div style="padding:10px; font-size:14px;">
                    <strong>${place.place_name}</strong><br>
                    ${place.road_address_name || place.address_name}<br>
                    <button onclick="checkAndSaveCafe('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x}, '${parsedAddress.city}', '${parsedAddress.district}')">지도에 추가하기</button>
                    <button onclick="openReviewPage('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x})">리뷰 페이지로 이동</button>
                </div>
            `;
        infowindow.setContent(content);
        infowindow.open(map, marker);
    });
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

    fetch(`/review/${kakaoId}`, {
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
            const cafeCardsContainer = document.getElementById('cafe-cards');
            if (cafeCardsContainer) {
                addCafeToSet(data);  // 카페 정보를 세트에 추가
            } else {
                console.error('Error: Cannot find cafe-cards container');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error: ' + error.message);
        });
}

function addCafeToSet(cafe) {
    if (!addedCafes.has(cafe.kakaoId)) {
        addedCafes.add(cafe.kakaoId);  // 카페 ID만 세트에 추가

        const card = document.createElement('div');
        card.className = 'cafe-card';
        card.innerHTML = `
            <h4>${cafe.name}</h4>
            <p>${cafe.address}</p>
            <button onclick="removeCafe('${cafe.kakaoId}')">삭제</button>
        `;

        card.addEventListener('click', () => {
            const marker = markers[cafe.kakaoId];
            if (marker) {
                map.setCenter(marker.getPosition());
                if (infowindow.getMap()) {
                    infowindow.close();
                }
            }
        });

        document.getElementById('cafe-cards').appendChild(card);

        const marker = markers[cafe.kakaoId];
        if (marker) {
            const markerImageUrl = "http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";
            const markerImage = new kakao.maps.MarkerImage(
                markerImageUrl,
                new kakao.maps.Size(24, 35),
                {offset: new kakao.maps.Point(12, 35)}
            );
            marker.setImage(markerImage);
        }

        console.log("Added cafes:", Array.from(addedCafes));
    }
}

function removeCafe(kakaoId) {
    addedCafes.delete(kakaoId);

    const cafeCards = document.getElementById('cafe-cards');
    const cards = cafeCards.getElementsByClassName('cafe-card');
    for (let i = 0; i < cards.length; i++) {
        if (cards[i].querySelector('button').getAttribute('onclick').includes(kakaoId)) {
            cafeCards.removeChild(cards[i]);
            break;
        }
    }

    const marker = markers[kakaoId];
    if (marker) {
        marker.setImage(null);
    }

    console.log("After removal cafes:", Array.from(addedCafes));
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

            if (markers[place.id]) {
                openInfoWindow(place);
            } else {
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
        <div style="padding:10px; font-size:14px;">
            <strong>${place.place_name}</strong><br>
            ${place.road_address_name || place.address_name}<br>
            <button onclick="checkAndSaveCafe('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x}, '${parsedAddress.city}', '${parsedAddress.district}')">지도에 추가하기</button>
            <button onclick="openReviewPage('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x})">리뷰 페이지로 이동</button>
        </div>
    `;
    infowindow.setContent(content);
    infowindow.open(map, marker);
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

    fetch(`/review/${kakaoId}`, {
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

function createCategoryButtons() {
    const availableList = document.querySelector('.available-categories');
    const selectedList = document.querySelector('.selected-categories');
    const selectedCategories = new Set();

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
    isCategorySearch = false;
    createCategoryButtons();
    const brandExcludeCheckbox = document.getElementById('brand-exclude-checkbox');
    brandExcludeCheckbox.checked = false;
    excludeBrands = false;
    searchPlacesByCenter(map.getCenter());
}


function onCategorySelect() {
    const selectedList = document.querySelector('.selected-categories');
    const selectedCategories = new Set([...selectedList.children].map(item => item.textContent));
    if (selectedCategories.size > 0) {
        isCategorySearch = true;
        clearMarkers();
        searchPlacesByCategory(selectedCategories);
    } else {
        alert("최소 한 개의 카테고리를 선택해주세요.");
    }
}

function clearMarkers() {
    for (let key in markers) {
        markers[key].setMap(null);
    }
    markers = {};
}

function searchPlacesByCategory(categories) {
    const categoryArray = Array.from(categories);
    const params = new URLSearchParams({
        categories: categoryArray.join(','),
        excludeBrands: excludeBrands
    });

    fetch('/api/map/category?' + params.toString())
        .then(response => response.json())
        .then(data => {
            data.forEach(cafe => {
                if (!isExcludedBrand(cafe.name)) {
                    displayDbMarker(cafe);
                }
            });
        })
        .catch(error => {
            alert('Error: ' + error.message);
        });
}


function displayDbMarker(cafe) {
    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(cafe.latitude, cafe.longitude)
    });

    markers[cafe.kakaoId] = marker;

    kakao.maps.event.addListener(marker, 'click', function () {
        var content = `
        <div style="padding:10px; font-size:14px;">
            <strong>${cafe.name}</strong><br>
            ${cafe.address}<br>
            <button onclick="checkAndSaveCafe('${cafe.kakaoId}', '${cafe.name}', '${cafe.address}', ${cafe.latitude}, ${cafe.longitude}, '${cafe.city}', '${cafe.district}')">지도에 추가하기</button>
            <a href="#" onclick="window.open('/review/${cafe.kakaoId}', '_blank')">리뷰 페이지로 이동</a>
        </div>
    `;
        infowindow.setContent(content);
        infowindow.open(map, marker);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    initMap();

    document.getElementById('save-btn').addEventListener('click', async function () {
        const mapName = document.getElementById('map-name').value;
        const isPublicCheckbox = document.getElementById('is-public');

        // 콘솔에 체크박스 상태를 출력
        console.log('Checkbox checked:', isPublicCheckbox.checked);

        // isPublic 값 설정: 체크되면 공개(false), 체크 안되면 비공개(true)
        const isPublic = !isPublicCheckbox.checked;

        // isPublic 값 출력
        console.log('isPublic value:', isPublic);

        if (!mapName) {
            alert('지도 이름을 입력하세요.');
            return;
        }

        const mapData = {
            mapName: mapName,
            kakaoIds: Array.from(addedCafes),
            isPublic: isPublic
        };

        console.log("Sending map data:", mapData);

        try {
            const response = await fetch('/api/mymap', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(mapData)
            });

            if (!response.ok) {
                const text = await response.text();
                throw new Error(text);
            }

            alert('지도 생성 성공');
            window.location.href = "/";
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    });
});
