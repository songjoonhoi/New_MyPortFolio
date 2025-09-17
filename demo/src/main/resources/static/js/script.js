// src/main/resources/static/js/script.js

document.addEventListener('DOMContentLoaded', () => {
    const projectCards = document.querySelectorAll('.project-card');
    const modalOverlay = document.getElementById('project-modal');
    const modalCloseButton = document.querySelector('.modal-close-button');

    // 각 프로젝트 카드에 클릭 이벤트 추가
    projectCards.forEach(card => {
        card.addEventListener('click', () => {
            // 카드에서 data 속성 값 읽어오기
            const data = card.dataset;

            // 모달에 데이터 채우기
            document.getElementById('modal-name').textContent = data.name;
            document.getElementById('modal-period').textContent = data.period;
            document.getElementById('modal-problem').textContent = data.problem;
            document.getElementById('modal-action').textContent = data.action;
            document.getElementById('modal-result').textContent = data.result;
            document.getElementById('modal-tech').textContent = data.tech;
            
            const urlLink = document.getElementById('modal-url');
            if (data.url) {
                urlLink.href = data.url;
                urlLink.style.display = 'inline-block';
            } else {
                urlLink.style.display = 'none';
            }

            // 모달 보이기
            modalOverlay.classList.add('active');
        });
    });

    // 모달 닫기 버튼 이벤트
    modalCloseButton.addEventListener('click', () => {
        modalOverlay.classList.remove('active');
    });

    // 모달 배경 클릭 시 닫기 이벤트
    modalOverlay.addEventListener('click', (event) => {
        if (event.target === modalOverlay) {
            modalOverlay.classList.remove('active');
        }
    });
});