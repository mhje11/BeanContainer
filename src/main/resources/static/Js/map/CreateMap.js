let map, ps, infowindow, markers = {}, addedCafes = [];
let isMoving = false;

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

    kakao.maps.event.addListener(map, 'dragstart', function () {
        isMoving = true;
    });

    kakao.maps.event.addListener(map, 'dragend', function () {
        isMoving = false;
        searchPlacesByCenter(map.getCenter());
    });

    kakao.maps.event.addListener(map, 'zoom_start', function () {
        isMoving = true;
    });

    kakao.maps.event.addListener(map, 'zoom_changed', function () {
        isMoving = false;
        searchPlacesByCenter(map.getCenter());
    });

    document.getElementById('search-btn').addEventListener('click', searchCafeByName);
}

function searchPlacesByCenter(center) {
    if (isMoving) return;

    const keyword = '카페';
    ps.keywordSearch(keyword, function (data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
            for (var i = 0; i < data.length; i++) {
                if (!isExcludedCategory(data[i].category_name) && !markers[data[i].id]) {
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
    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
    });
    markers[place.id] = marker;

    kakao.maps.event.addListener(marker, 'click', function () {
        const parsedAddress = parseAddress(place.road_address_name || place.address_name);

        var content = `
                <div style="padding:10px; font-size:14px;">
                    <strong>${place.place_name}</strong><br>
                    ${place.road_address_name || place.address_name}<br>
                    <button onclick="checkAndSaveCafe('${place.id}', '${place.place_name}', '${place.road_address_name || place.address_name}', ${place.y}, ${place.x}, '${parsedAddress.city}', '${parsedAddress.district}')">지도에 추가하기</button>
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
                throw new Error(text)
            });
        }
        return response.json();
    })
        .then(data => {
            addCafeToList(data, kakaoId);
        }).catch(error => {
        console.error('Error:', error);
        alert('Error: ' + error.message);
    });
}

function addCafeToList(cafe, kakaoId) {
    addedCafes.push({
        kakaoId: kakaoId,
        name: cafe.name,
        address: cafe.address,
        latitude: cafe.latitude,
        longitude: cafe.longitude,
        city: cafe.city,
        district: cafe.district
    });
    const li = document.createElement('li');
    li.textContent = cafe.name;
    li.dataset.kakaoId = kakaoId; // li 요소에 kakaoId 데이터 속성 추가
    const button = document.createElement('button');
    button.textContent = '삭제';
    button.onclick = () => removeCafe(kakaoId);
    li.appendChild(button);
    document.getElementById('cafe-list').appendChild(li);

    console.log("Added cafes:", addedCafes); // 로그 추가
}

function removeCafe(kakaoId) {
    addedCafes = addedCafes.filter(cafe => cafe.kakaoId !== kakaoId);
    const cafeList = document.getElementById('cafe-list');
    const items = cafeList.getElementsByTagName('li');
    for (let i = 0; i < items.length; i++) {
        if (items[i].dataset.kakaoId === kakaoId) { // li 요소의 데이터 속성을 사용하여 비교
            cafeList.removeChild(items[i]);
            break;
        }
    }
    console.log("After removal cafes:", addedCafes); // 로그 추가
}

function searchCafeByName() {
    const keyword = document.getElementById('search-input').value;
    if (!keyword) {
        alert('카페 이름을 입력하세요.');
        return;
    }

    ps.keywordSearch(keyword, function(data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
            if (data.length === 1) {
                const place = data[0];
                const center = new kakao.maps.LatLng(place.y, place.x);
                map.setCenter(center);
                map.setLevel(3);

                // 지도를 미세하게 이동시키는 코드
                const offsetCenter = new kakao.maps.LatLng(place.y + 0.00001, place.x + 0.00001);
                map.setCenter(offsetCenter);
                map.setCenter(center);

                // 강제로 마커 로딩
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
        resultItem.textContent = place.place_name;
        resultItem.style.cursor = 'pointer';
        resultItem.onclick = () => {
            map.setCenter(new kakao.maps.LatLng(place.y, place.x));
            map.setLevel(3);
            searchResultsContainer.innerHTML = ''; // 선택 후 검색 결과 제거

        };
        searchResultsContainer.appendChild(resultItem);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    initMap();

    document.getElementById('create-map-btn').addEventListener('click', function () {
        const mapName = document.getElementById('map-name').value;
        if (!mapName) {
            alert('지도 이름을 입력하세요.');
            return;
        }

        const mapData = {
            mapName: mapName,
            kakaoIds: addedCafes.map(cafe => cafe.kakaoId)
        };

        console.log("Sending map data:", mapData); // 로그 추가
        fetch('/api/mymap', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(mapData)
        }).then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            alert('지도 생성 성공');
        }).catch(error => {
            console.error('Error:', error);
            alert('Error: ' + error.message);
        });
    });
});
