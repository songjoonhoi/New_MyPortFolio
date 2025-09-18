// src/main/resources/static/js/project-detail.js

/**
 * 프로젝트 상세 페이지 전용 JavaScript
 * - 드래그 앤 드롭 파일 업로드
 * - 이미지 미리보기 및 모달
 * - 폼 유효성 검사
 * - 문자 수 카운터
 * - 삭제 확인
 */

class ProjectDetailManager {
    constructor() {
        this.init();
    }

    /**
     * 초기화
     */
    init() {
        this.setupDragAndDrop();
        this.setupImagePreview();
        this.setupFormValidation();
        this.setupCharacterCounter();
        this.setupDeleteConfirmation();
        this.setupImageModal();
        this.setupFormSubmission();
        this.setupKeyboardNavigation();
    }

    /**
     * 드래그 앤 드롭 설정
     */
    setupDragAndDrop() {
        const dragDropArea = document.getElementById('dragDropArea');
        const fileInput = document.getElementById('detailImage');
        const filePreview = document.getElementById('filePreview');

        if (!dragDropArea || !fileInput) return;

        // 드래그 앤 드롭 이벤트
        dragDropArea.addEventListener('click', () => fileInput.click());
        
        dragDropArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.stopPropagation();
            dragDropArea.classList.add('dragover');
        });
        
        dragDropArea.addEventListener('dragleave', (e) => {
            e.preventDefault();
            e.stopPropagation();
            // 드래그가 완전히 영역을 벗어났을 때만 클래스 제거
            if (!dragDropArea.contains(e.relatedTarget)) {
                dragDropArea.classList.remove('dragover');
            }
        });
        
        dragDropArea.addEventListener('drop', (e) => {
            e.preventDefault();
            e.stopPropagation();
            dragDropArea.classList.remove('dragover');
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                fileInput.files = files;
                this.handleFileSelect(files[0]);
            }
        });

        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                this.handleFileSelect(e.target.files[0]);
            }
        });
    }

    /**
     * 파일 선택 처리
     */
    handleFileSelect(file) {
        const dragDropArea = document.getElementById('dragDropArea');
        const filePreview = document.getElementById('filePreview');
        
        // 파일 유효성 검사
        if (!this.validateFile(file)) {
            return;
        }
        
        dragDropArea.classList.add('has-file');
        
        // 파일 미리보기
        const reader = new FileReader();
        reader.onload = (e) => {
            filePreview.innerHTML = `
                <img src="${e.target.result}" alt="미리보기">
                <div class="file-info">
                    <p><strong>파일명:</strong> ${file.name}</p>
                    <p><strong>크기:</strong> ${this.formatFileSize(file.size)}</p>
                    <p><strong>타입:</strong> ${file.type}</p>
                    <p><strong>업로드 시간:</strong> ${new Date().toLocaleString('ko-KR')}</p>
                </div>
            `;
            filePreview.style.display = 'block';
            
            // 스크롤하여 미리보기 보이기
            filePreview.scrollIntoView({ 
                behavior: 'smooth', 
                block: 'nearest' 
            });
        };
        reader.readAsDataURL(file);
    }

    /**
     * 파일 유효성 검사
     */
    validateFile(file) {
        const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
        const maxSize = 10 * 1024 * 1024; // 10MB

        if (!allowedTypes.includes(file.type)) {
            this.showNotification('JPEG, PNG, WebP 파일만 업로드 가능합니다.', 'error');
            return false;
        }

        if (file.size > maxSize) {
            this.showNotification('파일 크기는 10MB를 초과할 수 없습니다.', 'error');
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
     * 이미지 미리보기 설정 (썸네일)
     */
    setupImagePreview() {
        const thumbnailInput = document.getElementById('thumbnailImage');
        const thumbnailPreview = document.getElementById('thumbnailPreview');

        if (!thumbnailInput || !thumbnailPreview) return;

        thumbnailInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file && this.validateFile(file)) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    thumbnailPreview.innerHTML = `
                        <img src="${e.target.result}" alt="썸네일 미리보기" style="max-width: 100%; max-height: 200px; border-radius: 8px;">
                        <div class="file-info">
                            <p><strong>새 썸네일:</strong> ${file.name}</p>
                            <p><strong>크기:</strong> ${this.formatFileSize(file.size)}</p>
                        </div>
                    `;
                    thumbnailPreview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            } else {
                thumbnailPreview.style.display = 'none';
            }
        });
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

                input.addEventListener('input', () => {
                    // 에러 상태인 경우에만 실시간 검사
                    if (input.classList.contains('error')) {
                        this.validateField(input);
                    }
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

        // 파일 업로드 폼인 경우 파일 검사
        if (form.id === 'addDetailForm') {
            const fileInput = form.querySelector('#detailImage');
            if (fileInput && fileInput.files.length === 0) {
                this.showFieldError(fileInput, '이미지 파일을 선택해주세요.');
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * 필드 유효성 검사
     */
    validateField(field) {
        const value = field.value.trim();
        let isValid = true;
        let errorMessage = '';

        // 필수 필드 검사
        if (field.hasAttribute('required') && value.length === 0) {
            errorMessage = '이 필드는 필수입니다.';
            isValid = false;
        }
        // 최대 길이 검사
        else if (field.hasAttribute('maxlength')) {
            const maxLength = parseInt(field.getAttribute('maxlength'));
            if (value.length > maxLength) {
                errorMessage = `최대 ${maxLength}자까지 입력 가능합니다.`;
                isValid = false;
            }
        }
        // URL 형식 검사
        else if (field.type === 'url' && value.length > 0) {
            const urlPattern = /^https?:\/\/.+/;
            if (!urlPattern.test(value)) {
                errorMessage = '올바른 URL 형식을 입력해주세요. (http:// 또는 https://)';
                isValid = false;
            }
        }

        if (isValid) {
            field.classList.remove('error');
            field.classList.add('success');
            this.removeFieldError(field);
        } else {
            field.classList.remove('success');
            field.classList.add('error');
            this.showFieldError(field, errorMessage);
        }

        return isValid;
    }

    /**
     * 필드 에러 표시
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
     * 필드 에러 제거
     */
    removeFieldError(field) {
        const existingError = field.parentNode.querySelector('.field-error');
        if (existingError) {
            existingError.remove();
        }
    }

    /**
     * 문자 수 카운터 설정
     */
    setupCharacterCounter() {
        const textarea = document.getElementById('detailDescription');
        const charCount = document.getElementById('charCount');

        if (!textarea || !charCount) return;

        const maxLength = parseInt(textarea.getAttribute('maxlength')) || 2000;

        const updateCharCount = () => {
            const currentLength = textarea.value.length;
            charCount.textContent = currentLength;

            // 색상 변경
            const charCountContainer = charCount.parentElement;
            charCountContainer.classList.remove('warning', 'danger');

            if (currentLength > maxLength * 0.9) {
                charCountContainer.classList.add('danger');
            } else if (currentLength > maxLength * 0.8) {
                charCountContainer.classList.add('warning');
            }
        };

        textarea.addEventListener('input', updateCharCount);
        updateCharCount(); // 초기 실행
    }

    /**
     * 삭제 확인 설정
     */
    setupDeleteConfirmation() {
        document.addEventListener('click', (e) => {
            if (e.target.closest('.delete-detail-btn')) {
                e.preventDefault();
                
                const deleteBtn = e.target.closest('.delete-detail-btn');
                const description = deleteBtn.getAttribute('data-description') || '이 항목';
                const shortDescription = description.length > 50 
                    ? description.substring(0, 50) + '...' 
                    : description;
                
                if (confirm(`"${shortDescription}"\n\n이 상세 정보를 정말 삭제하시겠습니까?`)) {
                    // 삭제 진행
                    window.location.href = deleteBtn.href;
                }
            }
        });
    }

    /**
     * 이미지 모달 설정
     */
    setupImageModal() {
        const modal = document.getElementById('imageModal');
        if (!modal) return;

        // ESC 키로 모달 닫기
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.classList.contains('show')) {
                this.closeImageModal();
            }
        });

        // 모달 배경 클릭으로 닫기
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                this.closeImageModal();
            }
        });
    }

    /**
     * 폼 제출 설정
     */
    setupFormSubmission() {
        const addDetailForm = document.getElementById('addDetailForm');
        const basicInfoForm = document.getElementById('basicInfoForm');
        const progressIndicator = document.getElementById('progressIndicator');
        const submitBtn = document.getElementById('submitBtn');

        if (addDetailForm && progressIndicator && submitBtn) {
            addDetailForm.addEventListener('submit', (e) => {
                if (this.validateForm(addDetailForm)) {
                    // 진행 상황 표시
                    progressIndicator.classList.add('show');
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<span class="btn-icon">⏳</span> 추가 중...';
                    
                    // 스크롤 위치 저장
                    sessionStorage.setItem('projectDetailScrollPosition', window.pageYOffset.toString());
                }
            });
        }

        if (basicInfoForm) {
            basicInfoForm.addEventListener('submit', (e) => {
                sessionStorage.setItem('projectDetailScrollPosition', window.pageYOffset.toString());
            });
        }

        // 페이지 로드 시 스크롤 위치 복원
        const savedScrollPosition = sessionStorage.getItem('projectDetailScrollPosition');
        if (savedScrollPosition) {
            setTimeout(() => {
                window.scrollTo(0, parseInt(savedScrollPosition));
                sessionStorage.removeItem('projectDetailScrollPosition');
            }, 100);
        }
    }

    /**
     * 키보드 네비게이션 설정
     */
    setupKeyboardNavigation() {
        document.addEventListener('keydown', (e) => {
            // Ctrl + S로 기본 정보 저장
            if (e.ctrlKey && e.key === 's') {
                e.preventDefault();
                const basicInfoForm = document.getElementById('basicInfoForm');
                if (basicInfoForm) {
                    basicInfoForm.submit();
                }
            }
            
            // Ctrl + Enter로 상세 정보 추가
            if (e.ctrlKey && e.key === 'Enter') {
                const addDetailForm = document.getElementById('addDetailForm');
                if (addDetailForm && this.validateForm(addDetailForm)) {
                    addDetailForm.submit();
                }
            }
        });
    }

    /**
     * 알림 표시
     */
    showNotification(message, type = 'info') {
        // 기존 알림 제거
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }

        // 새 알림 생성
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 10000;
            max-width: 300px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            transform: translateX(100%);
            transition: transform 0.3s ease;
            ${type === 'error' ? 'background-color: #dc3545;' : 
              type === 'success' ? 'background-color: #28a745;' : 
              'background-color: #17a2b8;'}
        `;

        document.body.appendChild(notification);

        // 애니메이션 표시
        setTimeout(() => {
            notification.style.transform = 'translateX(0)';
        }, 100);

        // 자동 제거
        setTimeout(() => {
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }, 5000);
    }
}

// ===============================================
// Global Functions (모달 등에서 사용)
// ===============================================

/**
 * 이미지 모달 열기
 */
function openImageModal(imageUrl, description) {
    const modal = document.getElementById('imageModal');
    const modalImage = document.getElementById('modalImage');
    const modalDescription = document.getElementById('modalDescription');

    if (modal && modalImage && modalDescription) {
        modalImage.src = imageUrl;
        modalImage.alt = '상세 이미지';
        modalDescription.textContent = description;
        
        modal.classList.add('show');
        document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    }
}

/**
 * 이미지 모달 닫기
 */
function closeImageModal() {
    const modal = document.getElementById('imageModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = ''; // 배경 스크롤 복원
    }
}

/**
 * 상세 정보 추가 섹션으로 스크롤
 */
function scrollToAddSection() {
    const addSection = document.getElementById('add-detail-section');
    if (addSection) {
        addSection.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
        
        // 포커스를 파일 입력으로 이동
        setTimeout(() => {
            const dragDropArea = document.getElementById('dragDropArea');
            if (dragDropArea) {
                dragDropArea.focus();
                dragDropArea.style.boxShadow = '0 0 0 3px rgba(61, 90, 128, 0.3)';
                setTimeout(() => {
                    dragDropArea.style.boxShadow = '';
                }, 2000);
            }
        }, 500);
    }
}

/**
 * 추가 폼 초기화
 */
function resetAddForm() {
    const form = document.getElementById('addDetailForm');
    const dragDropArea = document.getElementById('dragDropArea');
    const filePreview = document.getElementById('filePreview');
    const charCount = document.getElementById('charCount');
    
    if (form) form.reset();
    if (dragDropArea) dragDropArea.classList.remove('has-file');
    if (filePreview) filePreview.style.display = 'none';
    if (charCount) charCount.textContent = '0';
    
    // 에러 상태 제거
    const errorFields = document.querySelectorAll('.form-control.error');
    errorFields.forEach(field => {
        field.classList.remove('error');
    });
    
    const errorMessages = document.querySelectorAll('.field-error');
    errorMessages.forEach(error => error.remove());
}

// ===============================================
// 초기화
// ===============================================

// DOM 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    new ProjectDetailManager();
});

// 전역 함수로 내보내기
window.ProjectDetailManager = ProjectDetailManager;
window.openImageModal = openImageModal;
window.closeImageModal = closeImageModal;
window.scrollToAddSection = scrollToAddSection;
window.resetAddForm = resetAddForm;