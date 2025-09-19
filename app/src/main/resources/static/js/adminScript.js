/**
 * MAIN INITIALIZATION
 */
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Feather Icons
    feather.replace();

    // Initialize all components
    initViewToggle();
    initSelectAllCheckbox();
    initImageUploads();
    initChapterManagement();
    initMobileMenu();
    profileDropdownButton();
});

/**
 * VIEW TOGGLE FUNCTIONALITY
 */
function initViewToggle() {
    const gridViewBtn = document.getElementById('gridViewBtn');
    const tableViewBtn = document.getElementById('tableViewBtn');
    const gridView = document.getElementById('gridView');
    const tableView = document.getElementById('tableView');

    if (!gridViewBtn || !tableViewBtn || !gridView || !tableView) return;

    gridViewBtn.addEventListener('click', function() {
        gridView.classList.remove('hidden');
        tableView.classList.add('hidden');
        updateButtonStyles(gridViewBtn, tableViewBtn);
        feather.replace();
    });

    tableViewBtn.addEventListener('click', function() {
        tableView.classList.remove('hidden');
        gridView.classList.add('hidden');
        updateButtonStyles(tableViewBtn, gridViewBtn);
        feather.replace();
    });

    function updateButtonStyles(activeBtn, inactiveBtn) {
        activeBtn.classList.add('text-primary-600', 'bg-primary-50');
        activeBtn.classList.remove('text-gray-500');
        inactiveBtn.classList.remove('text-primary-600', 'bg-primary-50');
        inactiveBtn.classList.add('text-gray-500');
    }
}

/**
 * SELECT ALL CHECKBOX FUNCTIONALITY
 */
function initSelectAllCheckbox() {
    const selectAllCheckbox = document.getElementById('selectAll');
    const individualCheckboxes = document.querySelectorAll('input[type="checkbox"]:not(#selectAll)');

    if (!selectAllCheckbox) return;

    selectAllCheckbox.addEventListener('change', function() {
        individualCheckboxes.forEach(checkbox => {
            checkbox.checked = selectAllCheckbox.checked;
        });
    });
}

/**
 * IMAGE UPLOAD FUNCTIONALITY
 */
function initImageUploads() {
    // Author image upload
    initImageUpload(
        'authorImageUpload',
        'authorPreviewContainer',
        'authorImagePreview',
        'authorUploadArea',
        'authorImageFilename',
        'authorFileError',
        'removeAuthorImage',
        2 // 2MB limit
    );

    // Book cover upload
    initImageUpload(
        'bookImageUpload',
        'bookPreviewContainer',
        'bookImagePreview',
        'bookUploadArea',
        'bookImageFilename',
        'bookFileError',
        'removeBookImage',
        5 // 5MB limit
    );
}

function initImageUpload(uploadId, previewContainerId, previewId, uploadAreaId, filenameId, errorId, removeBtnId, maxSizeMB) {
    const elements = {
        upload: document.getElementById(uploadId),
        previewContainer: document.getElementById(previewContainerId),
        preview: document.getElementById(previewId),
        uploadArea: document.getElementById(uploadAreaId),
        filename: document.getElementById(filenameId),
        error: document.getElementById(errorId),
        removeBtn: document.getElementById(removeBtnId)
    };

    if (!elements.upload) return;

    // Event listeners
    elements.upload.addEventListener('change', (event) => handleFileSelect(event, elements, maxSizeMB));

    if (elements.removeBtn) {
        elements.removeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            resetImageUpload(elements);
        });
    }

    if (elements.uploadArea) {
        setupDragAndDrop(elements);
    }
}

function handleFileSelect(event, elements, maxSizeMB) {
    const file = event.target.files[0];
    resetError(elements);

    if (!file) {
        resetImageUpload(elements);
        return;
    }

    // Validate file type
    const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
    if (!validTypes.includes(file.type)) {
        showError(`Only JPG, PNG, or WEBP images are allowed`, elements);
        return;
    }

    // Validate file size
    if (file.size > maxSizeMB * 1024 * 1024) {
        showError(`File size exceeds ${maxSizeMB}MB limit`, elements);
        return;
    }

    // Create preview
    createImagePreview(file, elements);
}

function createImagePreview(file, elements) {
    const reader = new FileReader();
    reader.onload = function(e) {
        if (elements.preview) elements.preview.src = e.target.result;
        if (elements.previewContainer) elements.previewContainer.classList.remove('hidden');
        if (elements.uploadArea) elements.uploadArea.classList.add('hidden');
        if (elements.filename) elements.filename.textContent = file.name;
    };
    reader.readAsDataURL(file);
}

function setupDragAndDrop(elements) {
    elements.uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        elements.uploadArea.classList.add('border-primary-500', 'bg-gray-600');
    });

    elements.uploadArea.addEventListener('dragleave', () => {
        elements.uploadArea.classList.remove('border-primary-500', 'bg-gray-600');
    });

    elements.uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        elements.uploadArea.classList.remove('border-primary-500', 'bg-gray-600');
        elements.upload.files = e.dataTransfer.files;
        elements.upload.dispatchEvent(new Event('change'));
    });
}

function showError(message, elements) {
    if (elements.error) {
        elements.error.textContent = message;
        elements.error.classList.remove('hidden');
    }
    resetFileInput(elements);
}

function resetFileInput(elements) {
    elements.upload.value = '';
}

function resetError(elements) {
    if (elements.error) {
        elements.error.classList.add('hidden');
    }
}

function resetImageUpload(elements) {
    resetFileInput(elements);
    if (elements.previewContainer) elements.previewContainer.classList.add('hidden');
    if (elements.preview) elements.preview.src = '';
    if (elements.uploadArea) elements.uploadArea.classList.remove('hidden');
    if (elements.filename) elements.filename.textContent = '';
    resetError(elements);
}

/**
 * CHAPTER MANAGEMENT
 */
function initChapterManagement() {
    const chaptersContainer = document.getElementById('chapters-container');
    const addChapterBtn = document.getElementById('add-chapter');
    let chapterCount = 0;

    if (!addChapterBtn) return;

    addChapterBtn.addEventListener('click', function() {
        const chapterDiv = document.createElement('div');
        chapterDiv.className = 'grid grid-cols-1 lg:grid-cols-3 gap-6 mb-4';
        chapterDiv.innerHTML = `
            <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">
                    Chapter Title <span class="text-red-500">*</span>
                </label>
                <input type="text"
                       name="chapters[${chapterCount}].title"
                       class="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-gray-100 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                       placeholder="Chapter title"
                       required>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">
                    Duration <span class="text-red-500">*</span>
                </label>
                <input type="text"
                       name="chapters[${chapterCount}].duration"
                       class="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-gray-100 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                       placeholder="HH:MM:SS"
                       required>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">
                    Audio File <span class="text-red-500">*</span>
                </label>
                <input type="file"
                       name="chapters[${chapterCount}].audioFile"
                       class="w-full px-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-gray-100 focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                       accept="audio/*"
                       required>
            </div>
            <div class="flex items-end lg:col-span-3">
                <button type="button" class="remove-chapter px-4 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors">
                    <i class="fas fa-trash mr-2"></i>Remove
                </button>
            </div>
        `;

        chaptersContainer.appendChild(chapterDiv);

        // Add remove functionality
        chapterDiv.querySelector('.remove-chapter').addEventListener('click', function() {
            chapterDiv.remove();
        });

        chapterCount++;
    });

    // Add initial chapter
    addChapterBtn.click();
}

/**
 * MOBILE MENU TOGGLE
 */
function initMobileMenu() {
    const mobileMenuButton = document.querySelector('[aria-controls="mobile-menu"]');
    const mobileMenu = document.getElementById('mobile-menu');

    if (!mobileMenuButton || !mobileMenu) return;

    mobileMenuButton.addEventListener('click', function() {
        const expanded = this.getAttribute('aria-expanded') === 'true';
        this.setAttribute('aria-expanded', !expanded);
        mobileMenu.classList.toggle('hidden');
    });
}


//Profile Dropdown


  // =============================================
  // PROFILE DROPDOWN FUNCTIONALITY
  // =============================================
  function profileDropdownButton(){
  const profileDropdownButton = document.getElementById('profileDropdownButton');
  const profileDropdownMenu = document.getElementById('profileDropdownMenu');

  if (profileDropdownButton && profileDropdownMenu) {
    profileDropdownButton.addEventListener('click', function(e) {
      e.stopPropagation();
      profileDropdownMenu.classList.toggle('opacity-100');
      profileDropdownMenu.classList.toggle('invisible');
      profileDropdownMenu.classList.toggle('scale-95');
    });

    // Close when clicking outside
    document.addEventListener('click', function() {
      profileDropdownMenu.classList.add('opacity-0', 'invisible', 'scale-95');
    });

    // Prevent dropdown from closing when clicking inside
    profileDropdownMenu.addEventListener('click', function(e) {
      e.stopPropagation();
    });
  }
  }

  //Delete Book

function confirmDelete(bookId) {
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel'
    }).then((result) => {
       if (result.isConfirmed) {
           fetch('/admin/deletebook/' + bookId, { method: 'DELETE' })
               .then(res => res.json())
               .then(data => {
                   if (data.success) {
                       Swal.fire({
                           title: 'Deleted!',
                           text: 'The book has been deleted successfully.',
                           icon: 'success',
                           confirmButtonText: 'OK'
                       }).then(() => {
                           // redirect only after user clicks OK
                           window.location.href = "/admin/home";
                       });
                   } else {
                       Swal.fire({
                           title: 'Error!',
                           text: data.message || 'Failed to delete the book.',
                           icon: 'error',
                           confirmButtonText: 'OK'
                       }).then(() => {
                           window.location.href = "/admin/home";
                       });
                   }
               })
               .catch(err => {
                   Swal.fire({
                       title: 'Error!',
                       text: 'Something went wrong.',
                       icon: 'error',
                       confirmButtonText: 'OK'
                   }).then(() => {
                       window.location.href = "/admin/home";
                   });
               });
       }

    });
}

