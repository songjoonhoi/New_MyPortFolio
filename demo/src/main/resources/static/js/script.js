// src/main/resources/static/js/script.js

document.addEventListener('DOMContentLoaded', () => {
    const projectCards = document.querySelectorAll('.project-card');
    const modalOverlay = document.getElementById('project-modal');
    const modalCloseButton = document.querySelector('.modal-close-button');
    const modalDetailsContainer = document.getElementById('modal-details-container');

    projectCards.forEach(card => {
        card.addEventListener('click', () => {
            const data = card.dataset;

            // 1. 모달에 기본 정보 채우기
            document.getElementById('modal-name').textContent = data.name;
            document.getElementById('modal-period').textContent = data.period;
            document.getElementById('modal-tech').textContent = data.tech;
            
            const urlLink = document.getElementById('modal-url');
            if (data.url && data.url.trim() !== '') {
                urlLink.href = data.url;
                urlLink.style.display = 'inline-block';
            } else {
                urlLink.style.display = 'none';
            }

            // 2. 모달에 상세 정보(이미지+설명) 채우기
            modalDetailsContainer.innerHTML = ''; // 기존 내용 초기화
            const detailsId = data.detailsId;
            const detailsSourceContainer = document.getElementById(detailsId);
            
            if (detailsSourceContainer) {
                const detailSources = detailsSourceContainer.querySelectorAll('.detail-item-source');
                detailSources.forEach(source => {
                    const detailItem = document.createElement('div');
                    detailItem.classList.add('detail-item');
                    detailItem.innerHTML = source.innerHTML;
                    modalDetailsContainer.appendChild(detailItem);
                });
            }

            // 3. 모달 보이기
            modalOverlay.classList.add('active');
        });
    });

    // 모달 닫기 이벤트
    const closeModal = () => {
        modalOverlay.classList.remove('active');
    };

    modalCloseButton.addEventListener('click', closeModal);

    modalOverlay.addEventListener('click', (event) => {
        if (event.target === modalOverlay) {
            closeModal();
        }
    });
});