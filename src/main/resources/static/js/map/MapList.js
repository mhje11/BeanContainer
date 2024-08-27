    // 페이지 로드 시 지도 목록 가져오기
    document.addEventListener('DOMContentLoaded', function() {
    fetchMyMaps();
});

    function fetchMyMaps() {
    fetch('/api/mymap')
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.json();
        })
        .then(data => {
            displayMapList(data);
        })
        .catch(error => {
            console.error(error);
            alert(error.message); // 백엔드에서 보낸 메시지를 그대로 사용자에게 표시
        });
}


    function displayMapList(mapList) {
    const mapListElement = document.getElementById('map-list');
    mapListElement.innerHTML = ''; // 기존 목록 초기화

    mapList.forEach(map => {
    const listItem = document.createElement('li');
    listItem.innerHTML = `
                <a href="/mymap/${map.mapId}">${map.mapName}</a>
                <div class="button-container">
                    <a href="/mymap/update/${map.mapId}" class="btn btn-primary">수정</a>
                    <button data-map-id="${map.mapId}" class="btn btn-danger" onclick="deleteMap(this)">삭제</button>
                </div>
            `;
    mapListElement.appendChild(listItem);
});
}

    function deleteMap(button) {
    const mapId = button.getAttribute('data-map-id');
    const confirmed = confirm('정말 이 지도를 삭제하시겠습니까?');

    if (confirmed) {
    fetch(`/api/mymap/delete/${mapId}`, {
    method: 'DELETE',
})
    .then(response => {
    if (response.ok) {
    alert('지도 삭제 성공');
    fetchMyMaps(); // 삭제 후 목록을 다시 불러옴
} else {
    alert('지도 삭제 실패');
}
})
    .catch(error => {
    console.error('Error:', error);
    alert('지도 삭제 실패');
});
}
}
