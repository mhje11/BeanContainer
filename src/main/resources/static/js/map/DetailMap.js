let map, infowindow, markers = {};
let selectedMarker = null; // 선택된 마커를 추적하는 변수

async function initMap() {
    var mapContainer = document.getElementById('map'),
        mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 5
        };

    map = new kakao.maps.Map(mapContainer, mapOption);
    infowindow = new kakao.maps.InfoWindow({zIndex: 1});

    var zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.LEFT);

    const mapId = window.location.pathname.split("/").pop();
    try {
        const response = await fetch(`/api/mymap/${mapId}`);
        const mapDetail = await response.json();

        document.getElementById('map-name').innerText = `${mapDetail.mapName}`;
        document.getElementById('username').innerText = `작성자: ${mapDetail.username}`;

        mapDetail.cafes.forEach(cafe => {
            console.log("Displaying cafe:", cafe); // Debugging
            displayMarker(cafe);
            addCafeToSidebar(cafe);
        });
    } catch (error) {
        console.error('Error fetching map details:', error);
    }

    // 맵을 클릭하면 인포윈도우 닫기
    kakao.maps.event.addListener(map, 'click', function () {
        infowindow.close();
    });
}

function displayMarker(cafe) {
    const markerPosition = new kakao.maps.LatLng(cafe.latitude, cafe.longitude);

    const marker = new kakao.maps.Marker({
        map: map,
        position: markerPosition
    });

    markers[cafe.id] = marker;
    console.log("Original averageScore:", cafe.averageScore);


    // 마커 클릭 시 인포윈도우 열기
    kakao.maps.event.addListener(marker, 'click', function () {
        // 여기서 averageScore를 명확히 부동소수점으로 변환하여 처리
        const averageScore = cafe.averageScore !== null ? Number(parseFloat(cafe.averageScore).toFixed(1)) : '0.0';
        console.log("Formatted averageScore:", averageScore);

        const content = `
            <div id="custom-infowindow" class="kakao-infowindow-content" style="padding:5px; font-size:12px;">
                <strong>${cafe.name}</strong><br>
                ${cafe.address}<br>
                <class="place-name">평점:</> ${averageScore} / 5.0<br>
                <class="place-address">카테고리:</> ${Array.from(cafe.topCategories).join(', ')}<br>
                <a href="/review/${cafe.id}" class="infowindow-link" target="_blank">리뷰 페이지로 이동</a>
            </div>
        `;

        infowindow.setContent(content);
        infowindow.open(map, marker);
    });
}

function addCafeToSidebar(cafe) {
    const sidebar = document.getElementById('sidebar');

    const cafeBox = document.createElement('div');
    cafeBox.className = 'cafe-box';

    cafeBox.innerHTML = `
            <h4>${cafe.name}</h4>
            <p><strong>카테고리:</strong> ${Array.from(cafe.topCategories).join(', ')}</p>
            <p><strong>평점:</strong> ${cafe.averageScore.toFixed(1)} / 5.0</p>
            <a href="/review/${cafe.id}" target="_blank">리뷰 페이지로 이동</a>
        `;

    // 카드 클릭 시 해당 마커 위치로 이동하고 인포윈도우 닫기 및 마커 이미지 변경
    cafeBox.addEventListener('click', () => {
        const marker = markers[cafe.id];
        if (marker) {
            // 이전에 선택된 마커가 있으면 기본 이미지로 되돌림
            if (selectedMarker) {
                selectedMarker.setImage(null);  // 기본 이미지로 돌아감 (null로 설정 시 기본 마커로 변경)
            }

            // 현재 선택된 마커의 이미지를 변경
            map.setCenter(marker.getPosition());
            const markerImage = new kakao.maps.MarkerImage(
                "http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png", // 선택된 마커 이미지 URL
                new kakao.maps.Size(24, 35), // 마커 이미지 크기
                {offset: new kakao.maps.Point(12, 35)} // 마커 이미지 위치 보정
            );
            marker.setImage(markerImage);

            // 현재 선택된 마커를 추적
            selectedMarker = marker;
        }
        infowindow.close();
    });

    sidebar.appendChild(cafeBox);
}

document.addEventListener('DOMContentLoaded', initMap);
