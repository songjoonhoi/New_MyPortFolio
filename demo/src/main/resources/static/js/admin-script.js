// src/main/resources/static/js/admin.js

/**
 * 관리자 페이지 JavaScript
 * - AJAX 폼 제출
 * - 이미지 미리보기
 * - 스크롤 위치 유지
 * - 동적 UI 업데이트
 */

class AdminPageManager {
    constructor() {
        this.init();
    }

    /**
     * 초기화
     */
    init() {
        this.setupEventListeners();
        this.setupImagePreviews();
        this.restoreScrollPosition();
        this.setupFormValidation();
        this.setupAccessibility();
    }

    /**
     * 이벤트 리스너 설정
     */
    setupEventListeners() {
        // AJAX 폼 제출
        document.addEventListener('submit', this.handleFormSubmit.bind(this));
        
        // 삭제 링크 클릭
        document.addEventListener('click', this.handleDeleteClick.bind(this));
        
        // 파일 입력 변경
        document.addEventListener('change', this.handleFileChange.bind(this));
        
        // 페이지 언로드 시 스크롤 위치 저장
        window.addEventListener('beforeunload', this.saveScrollPosition.bind(this));
        
        // 키보드 네비게이션
        document.addEventListener('keydown', this.handleKeyboardNavigation.bind(this));
    }

    /**
     * 이미지 미리보기 설정
     */
    setupImagePreviews() {
        // 프로필 이미지 미리보기
        this.setupImagePreview('profileImage', 'profileImagePreview');
        
        // 동적으로 추가되는 이미지 입력들을 위한 델리게이션
        document.addEventListener('change', (e) => {
            if (e.target.type === 'file' && e.target.accept && e.target.accept.includes('image/')) {
                this.handleImagePreview(e.target);
            }
        });
    }

    /**
     * 개별 이미지 미리보기 설정
     */
    setupImagePreview(inputId, previewId) {
        const input = document.getElementById(inputId);
        const preview = document.getElementById(previewId);
        
        if (input && preview) {
            input.addEventListener('change', () => {
                this.updateImagePreview(input, preview);
            });
        }
    }

    /**
     * 이미지 미리보기 업데이트
     */
    updateImagePreview(input, preview) {
        const file = input.files[0];
        
        if (!file) {
            this.resetImagePreview(preview);
            return;
        }

        // 파일 유효성 검사
        if (!this.validateImageFile(file)) {
            this.resetImagePreview(preview);
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            this.displayImagePreview(preview, e.target.result, file);
        };
        reader.readAsDataURL(file);
    }

    /**
     * 파일 변경 핸들러
     */
    handleImagePreview(input) {
        const preview = this.findOrCreatePreview(input);
        this.updateImagePreview(input, preview);
    }

    /**
     * 미리보기 요소 찾기 또는 생성
     */
    findOrCreatePreview(input) {
        let preview = input.parentNode.querySelector('.image-preview');
        
        if (!preview) {
            preview = document.createElement('div');
            preview.className = 'image-preview';
            preview.innerHTML = '<p>이미지를 선택하면 미리보기가 여기에 표시됩니다</p>';
            input.parentNode.appendChild(preview);
        }
        
        return preview;
    }

    /**
     * 이미지 미리보기 표시
     */
    displayImagePreview(preview, src, file) {
        const img = document.createElement('img');
        img.src = src;
        img.alt = '이미지 미리보기';
        img.style.maxWidth = '100%';
        img.style.maxHeight = '300px';
        img.style.borderRadius = '8px';
        img.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)';

        const info = document.createElement('div');
        info.className = 'image-info';
        info.innerHTML = `
            <p><strong>파일명:</strong> ${file.name}</p>
            <p><strong>크기:</strong> ${this.formatFileSize(file.size)}</p>
            <p><strong>타입:</strong> ${file.type}</p>
        `;

        preview.innerHTML = '';
        preview.appendChild(img);
        preview.appendChild(info);
        preview.classList.add('has-image');
    }

    /**
     * 이미지 미리보기 초기화
     */
    resetImagePreview(preview) {
        preview.innerHTML = '<p>이미지를 선택하면 미리보기가 여기에 표시됩니다</p>';
        preview.classList.remove('has-image');
    }

    /**
     * 이미지 파일 유효성 검사
     */
    validateImageFile(file) {
        const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
        const maxSize = 10 * 1024 * 1024; // 10MB

        if (!allowedTypes.includes(file.type)) {
            this.showMessage('JPEG, PNG, WebP 파일만 업로드 가능합니다.', 'error');
            return false;
        }

        if (file.size > maxSize) {
            this.showMessage('파일 크기는 10MB를 초과할 수 없습니다.', 'error');
            return false;
        }

        return true;
    }

    /**
     * 파일 크기 포맷팅
     */
    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    /**
     * 폼 제출 핸들러
     */
    handleFormSubmit(e) {
        const form = e.target;
        
        // AJAX 폼이 아닌 경우 기본 동작
        if (!form.hasAttribute('data-ajax-form')) {
            this.saveScrollPosition();
            return;
        }

        e.preventDefault();
        this.submitFormWithAjax(form);
    }

    /**
     * AJAX 폼 제출
     */
    async submitFormWithAjax(form) {
        const formData = new FormData(form);
        const url = form.action;
        const method = form.method || 'POST';
        
        // 현재 스크롤 위치 저장
        const scrollPosition = window.pageYOffset;
        
        // 로딩 상태 표시
        this.setFormLoading(form, true);

        try {
            const response = await fetch(url, {
                method: method,
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (response.ok) {
                await this.handleSuccessResponse(form, response);
                
                // 스크롤 위치 복원
                setTimeout(() => {
                    window.scrollTo(0, scrollPosition);
                }, 100);
            } else {
                throw new Error(`서버 오류: ${response.status}`);
            }
        } catch (error) {
            console.error('AJAX 요청 실패:', error);
            this.showMessage('오류가 발생했습니다: ' + error.message, 'error');
        } finally {
            this.setFormLoading(form, false);
        }
    }

    /**
     * 성공 응답 처리
     */
    async handleSuccessResponse(form, response) {
        const actionUrl = form.action;
        
        if (actionUrl.includes('/add')) {
            // 추가 폼인 경우
            await this.handleAddFormSuccess(form);
        } else if (actionUrl.includes('/update')) {
            // 업데이트 폼인 경우
            this.showMessage('정보가 업데이트되었습니다.', 'success');
        }
    }

    /**
     * 추가 폼 성공 처리
     */
    async handleAddFormSuccess(form) {
        const formData = new FormData(form);
        const actionUrl = form.action;
        
        if (actionUrl.includes('/career/add')) {
            this.addCareerToList({
                company: formData.get('company'),
                position: formData.get('position'),
                period: formData.get('period'),
                description: formData.get('description')
            });
        } else if (actionUrl.includes('/education/add')) {
            this.addEducationToList({
                institution: formData.get('institution'),
                course: formData.get('course'),
                period: formData.get('period'),
                description: formData.get('description')
            });
        } else if (actionUrl.includes('/certification/add')) {
            this.addCertificationToList({
                name: formData.get('name'),
                issuer: formData.get('issuer'),
                acquisitionDate: formData.get('acquisitionDate')
            });
        }
        
        // 폼 초기화
        form.reset();
        this.resetAllImagePreviews(form);
        this.showMessage('항목이 추가되었습니다.', 'success');
    }

    /**
     * 경력 목록에 추가
     */
    addCareerToList(career) {
        const careerList = document.getElementById('career-list');
        if (!careerList) return;

        const newCareerCard = this.createItemCard({
            content: `
                <p><strong>회사명:</strong> ${this.escapeHtml(career.company)}</p>
                <p><strong>직책:</strong> ${this.escapeHtml(career.position)}</p>
                <p><strong>기간:</strong> ${this.escapeHtml(career.period)}</p>
                ${career.description ? `<p><strong>주요 업무:</strong> ${this.escapeHtml(career.description)}</p>` : ''}
            `,
            actions: `
                <a href="#" class="btn btn-link btn-sm">수정</a>
                <a href="#" class="btn btn-danger btn-sm" data-delete="true">삭제</a>
            `
        });

        careerList.appendChild(newCareerCard);
        this.animateNewItem(newCareerCard);
    }

    /**
     * 학력 목록에 추가
     */
    addEducationToList(education) {
        const educationList = document.getElementById('education-list');
        if (!educationList) return;

        const newEducationCard = this.createItemCard({
            content: `
                <p><strong>기관명:</strong> ${this.escapeHtml(education.institution)}</p>
                <p><strong>과정/전공:</strong> ${this.escapeHtml(education.course)}</p>
                <p><strong>기간:</strong> ${this.escapeHtml(education.period)}</p>
                ${education.description ? `<p><strong>세부 내용:</strong> ${this.escapeHtml(education.description)}</p>` : ''}
            `,
            actions: `
                <a href="#" class="btn btn-link btn-sm">수정</a>
                <a href="#" class="btn btn-danger btn-sm" data-delete="true">삭제</a>
            `
        });

        educationList.appendChild(newEducationCard);
        this.animateNewItem(newEducationCard);
    }

    /**
     * 자격증 목록에 추가
     */
    addCertificationToList(certification) {
        const certificationList = document.getElementById('certification-list');
        if (!certificationList) return;

        const newCertificationCard = this.createItemCard({
            content: `
                <p><strong>자격증명:</strong> ${this.escapeHtml(certification.name)}</p>
                <p><strong>발급기관:</strong> ${this.escapeHtml(certification.issuer)}</p>
                <p><strong>취득일:</strong> ${this.escapeHtml(certification.acquisitionDate)}</p>
            `,
            actions: `
                <a href="#" class="btn btn-link btn-sm">수정</a>
                <a href="#" class="btn btn-danger btn-sm" data-delete="true">삭제</a>
            `
        });

        certificationList.appendChild(newCertificationCard);
        this.animateNewItem(newCertificationCard);
    }

    /**
     * 아이템 카드 생성
     */
    createItemCard({content, actions}) {
        const cardDiv = document.createElement('div');
        cardDiv.className = 'item-card';
        cardDiv.innerHTML = `
            <div class="item-card-content">
                ${content}
            </div>
            <div class="item-card-actions">
                ${actions}
            </div>
        `;
        return cardDiv;
    }

    /**
     * 새 아이템 애니메이션
     */
    animateNewItem(element) {
        element.style.opacity = '0';
        element.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            element.style.transition = 'all 0.3s ease';
            element.style.opacity = '1';
            element.style.transform = 'translateY(0)';
        }, 50);
    }

    /**
     * 삭제 클릭 핸들러
     */
    handleDeleteClick(e) {
        if (!e.target.hasAttribute('data-delete') && !e.target.href?.includes('/delete/')) {
            return;
        }

        e.preventDefault();
        
        if (!confirm('정말로 삭제하시겠습니까?')) {
            return;
        }

        if (e.target.hasAttribute('data-delete')) {
            // 동적으로 추가된 항목 삭제
            const itemCard = e.target.closest('.item-card');
            if (itemCard) {
                this.removeItemWithAnimation(itemCard);
            }
        } else {
            // 서버에서 삭제
            this.deleteItemFromServer(e.target.href, e.target);
        }
    }

    /**
     * 서버에서 항목 삭제
     */
    async deleteItemFromServer(deleteUrl, deleteLink) {
        const scrollPosition = window.pageYOffset;
        const itemCard = deleteLink.closest('.item-card');
        
        this.setItemLoading(itemCard, true);

        try {
            const response = await fetch(deleteUrl, {
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (response.ok) {
                this.removeItemWithAnimation(itemCard);
                this.showMessage('삭제되었습니다.', 'success');
                
                // 스크롤 위치 복원
                setTimeout(() => {
                    window.scrollTo(0, scrollPosition);
                }, 100);
            } else {
                throw new Error('삭제 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('삭제 실패:', error);
            this.showMessage('삭제 중 오류가 발생했습니다.', 'error');
            this.setItemLoading(itemCard, false);
        }
    }

    /**
     * 애니메이션과 함께 항목 제거
     */
    removeItemWithAnimation(element) {
        element.style.transition = 'all 0.3s ease';
        element.style.opacity = '0';
        element.style.transform = 'translateX(-100%)';
        
        setTimeout(() => {
            element.remove();
        }, 300);
    }

    /**
     * 폼 로딩 상태 설정
     */
    setFormLoading(form, loading) {
        if (loading) {
            form.classList.add('loading');
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = '처리 중...';
            }
        } else {
            form.classList.remove('loading');
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = submitBtn.getAttribute('data-original-text') || '저장';
            }
        }
    }

    /**
     * 아이템 로딩 상태 설정
     */
    setItemLoading(element, loading) {
        if (loading) {
            element.classList.add('loading');
        } else {
            element.classList.remove('loading');
        }
    }

    /**
     * 폼 내 모든 이미지 미리보기 초기화
     */
    resetAllImagePreviews(form) {
        const previews = form.querySelectorAll('.image-preview');
        previews.forEach(preview => {
            this.resetImagePreview(preview);
        });
    }

    /**
     * 메시지 표시
     */
    showMessage(message, type = 'success') {
        // 기존 메시지 제거
        const existingMessage = document.querySelector('.flash-message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // 새 메시지 생성
        const messageDiv = document.createElement('div');
        messageDiv.className = `flash-message ${type}`;
        messageDiv.textContent = message;
        messageDiv.setAttribute('role', 'alert');
        messageDiv.setAttribute('aria-live', 'polite');

        document.body.appendChild(messageDiv);

        // 애니메이션 표시
        setTimeout(() => {
            messageDiv.classList.add('show');
        }, 100);

        // 자동 제거
        setTimeout(() => {
            messageDiv.classList.remove('show');
            setTimeout(() => {
                if (messageDiv.parentNode) {
                    messageDiv.remove();
                }
            }, 300);
        }, 5000);
    }

    /**
     * 스크롤 위치 저장
     */
    saveScrollPosition() {
        sessionStorage.setItem('adminScrollPosition', window.pageYOffset.toString());
    }

    /**
     * 스크롤 위치 복원
     */
    restoreScrollPosition() {
        const savedScrollPosition = sessionStorage.getItem('adminScrollPosition');
        if (savedScrollPosition) {
            setTimeout(() => {
                window.scrollTo(0, parseInt(savedScrollPosition));
                sessionStorage.removeItem('adminScrollPosition');
            }, 100);
        }
    }

    /**
     * 폼 유효성 검사 설정
     */
    setupFormValidation() {
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            form.addEventListener('submit', (e) => {
                if (!this.validateForm(form)) {
                    e.preventDefault();
                }
            });

            // 실시간 유효성 검사
            const inputs = form.querySelectorAll('input[required], textarea[required]');
            inputs.forEach(input => {
                input.addEventListener('blur', () => {
                    this.validateField(input);
                });
            });
        });
    }

    /**
     * 폼 유효성 검사
     */
    validateForm(form) {
        let isValid = true;
        const requiredFields = form.querySelectorAll('input[required], textarea[required]');
        
        requiredFields.forEach(field => {
            if (!this.validateField(field)) {
                isValid = false;
            }
        });

        return isValid;
    }

    /**
     * 필드 유효성 검사
     */
    validateField(field) {
        const value = field.value.trim();
        const isValid = value.length > 0;

        if (isValid) {
            field.classList.remove('invalid');
            this.removeFieldError(field);
        } else {
            field.classList.add('invalid');
            this.showFieldError(field, '이 필드는 필수입니다.');
        }

        return isValid;
    }

    /**
     * 필드 오류 표시
     */
    showFieldError(field, message) {
        this.removeFieldError(field);
        
        const errorDiv = document.createElement('div');
        errorDiv.className = 'field-error';
        errorDiv.textContent = message;
        errorDiv.setAttribute('role', 'alert');
        
        field.parentNode.appendChild(errorDiv);
    }

    /**
     * 필드 오류 제거
     */
    removeFieldError(field) {
        const existingError = field.parentNode.querySelector('.field-error');
        if (existingError) {
            existingError.remove();
        }
    }

    /**
     * 접근성 설정
     */
    setupAccessibility() {
        // 버튼에 원래 텍스트 저장
        const submitButtons = document.querySelectorAll('button[type="submit"]');
        submitButtons.forEach(btn => {
            btn.setAttribute('data-original-text', btn.textContent);
        });

        // 스킵 링크 추가
        this.addSkipLink();
    }

    /**
     * 스킵 링크 추가
     */
    addSkipLink() {
        const skipLink = document.createElement('a');
        skipLink.href = '#main-content';
        skipLink.textContent = '본문으로 건너뛰기';
        skipLink.className = 'skip-link';
        
        document.body.insertBefore(skipLink, document.body.firstChild);
        
        // 메인 컨텐츠에 ID 추가
        const mainContent = document.querySelector('.admin-container');
        if (mainContent && !mainContent.id) {
            mainContent.id = 'main-content';
        }
    }

    /**
     * 키보드 네비게이션 핸들러
     */
    handleKeyboardNavigation(e) {
        // ESC 키로 메시지 닫기
        if (e.key === 'Escape') {
            const flashMessage = document.querySelector('.flash-message');
            if (flashMessage) {
                flashMessage.remove();
            }
        }
    }

    /**
     * HTML 이스케이프
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// DOM 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    new AdminPageManager();
});

// 전역 함수로 내보내기 (필요한 경우)
window.AdminPageManager = AdminPageManager;