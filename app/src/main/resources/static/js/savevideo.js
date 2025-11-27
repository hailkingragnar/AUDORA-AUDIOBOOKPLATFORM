// Wait for DOM to load
document.addEventListener('DOMContentLoaded', function() {
    const saveBtn = document.getElementById('saveBookBtn');
    const saveIcon = document.getElementById('saveIcon');
    const saveText = document.getElementById('saveText');
    const bookId = document.getElementById('bookId').value;
    const userId = document.getElementById('userId').value;

    // Check if book is already saved on page load
    checkIfBookSaved(bookId, userId);

    // Add click event listener
    saveBtn.addEventListener('click', function() {
        toggleSaveBook(bookId, userId);
    });
});

// Function to check if book is already saved
async function checkIfBookSaved(bookId, userId) {
    try {
        const response = await fetch(`/user/viewbook/checkbook?userId=${userId}&bookId=${bookId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
                 credentials: "include",

        });

        if (response.ok) {
            const data = await response.json();
            updateButtonState(data);
        }
    } catch (error) {
        console.error('Error checking saved status:', error);
    }
}

// Function to toggle save/unsave
async function toggleSaveBook(bookId, userId) {
    const saveBtn = document.getElementById('saveBookBtn');
    const isSaved = saveBtn.getAttribute('data-saved') === 'true';

    // Disable button during request
    saveBtn.disabled = true;

    try {
        if (isSaved) {
            // Unsave the book
            await unsaveBook(bookId, userId);
        } else {
            // Save the book
            await saveBook(bookId, userId);
        }
    } catch (error) {
        console.error('Error toggling save:', error);
        showNotification('Error processing request. Please try again.', 'error');
    } finally {
        // Re-enable button
        saveBtn.disabled = false;
    }
}

// Function to save book
async function saveBook(bookId, userId) {
    try {
        const response = await fetch('/user/viewbook/savebook', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: "include",
            body: JSON.stringify({
                userId: userId,
                bookId: bookId
            })
        });

        if (response.ok) {
            updateButtonState(true);
            showNotification('Book saved successfully!', 'success');
        } else {
            const error = await response.json();
            showNotification(error.message || 'Failed to save book', 'error');
        }
    } catch (error) {
        console.error('Error saving book:', error);
        throw error;
    }
}

// Function to unsave book
async function unsaveBook(bookId, userId) {
    try {
        const response = await fetch('/user/viewbook/deletebook', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: "include",
            body: JSON.stringify({
                userId: userId,
                bookId: bookId
            })
        });

        if (response.ok) {
            updateButtonState(false);
            showNotification('Book removed from saved list', 'success');
        } else {
            const error = await response.json();
            showNotification(error.message || 'Failed to remove book', 'error');
        }
    } catch (error) {
        console.error('Error removing book:', error);
        throw error;
    }
}

// Function to update button appearance
function updateButtonState(isSaved) {
    const saveBtn = document.getElementById('saveBookBtn');
    const saveIcon = document.getElementById('saveIcon');
    const saveText = document.getElementById('saveText');

    if (isSaved) {
        // Change to filled bookmark (saved state)
        saveIcon.className = 'fas fa-bookmark mr-2'; // Filled icon
        saveText.textContent = 'Saved';
        saveBtn.setAttribute('data-saved', 'true');
        saveBtn.classList.add('bg-blue-600', 'hover:bg-blue-500');
        saveBtn.classList.remove('bg-gray-800', 'hover:bg-gray-700');

        // Add scale animation
        saveBtn.classList.add('scale-110');
        setTimeout(() => saveBtn.classList.remove('scale-110'), 200);
    } else {
        // Change to hollow bookmark (unsaved state)
        saveIcon.className = 'far fa-bookmark mr-2'; // Hollow icon
        saveText.textContent = 'Save';
        saveBtn.setAttribute('data-saved', 'false');
        saveBtn.classList.remove('bg-blue-600', 'hover:bg-blue-500');
        saveBtn.classList.add('bg-gray-800', 'hover:bg-gray-700');
    }
}

// Function to show notification (optional but recommended)
function showNotification(message, type) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `fixed top-20 right-4 z-50 px-6 py-3 rounded-lg shadow-lg transition-all transform translate-x-0 ${
        type === 'success' ? 'bg-green-600' : 'bg-red-600'
    } text-white font-medium`;
    notification.textContent = message;

    // Add to page
    document.body.appendChild(notification);

    // Animate in
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 10);

    // Remove after 3 seconds
    setTimeout(() => {
        notification.style.transform = 'translateX(400px)';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}