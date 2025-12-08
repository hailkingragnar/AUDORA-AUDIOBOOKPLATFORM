document.addEventListener('DOMContentLoaded', function () {
    const saveBtn = document.getElementById('addToFavoriteBtn');
    const bookId = document.getElementById('bookId').value;
    const userId = document.getElementById('userId').value;

    // ✅ Check if already favorite on load
    checkIfBookAddedToFavourites(bookId, userId);

    // ✅ Click event
    saveBtn.addEventListener('click', function () {
        toggleAddToFavourites(bookId, userId);
    });
});

// ✅ Check if book is already favorite
async function checkIfBookAddedToFavourites(bookId, userId) {
    try {
        const response = await fetch(
            `/user/viewbook/checkbookfav?userId=${userId}&bookId=${bookId}`,
            {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: "include"
            }
        );

        if (response.ok) {
            const data = await response.json();
            updateFavButtonState(data === true);
        }
    } catch (error) {
        console.error("Error checking favorite:", error);
    }
}

// ✅ Update button UI
function updateFavButtonState(isAdded) {
    const saveBtn = document.getElementById('addToFavoriteBtn');
    const saveIcon = document.getElementById('favoriteIcon');
    const saveText = document.getElementById('favoriteText');

    if (isAdded) {
        saveIcon.className = 'fas fa-heart mr-2 text-red-500 transition-all';
        saveText.textContent = 'Added to Favorites';
        saveBtn.setAttribute('data-saved', 'true');

        saveBtn.classList.add('scale-110');
        setTimeout(() => saveBtn.classList.remove('scale-110'), 200);
    } else {
        saveIcon.className = 'far fa-heart mr-2 transition-all';
        saveText.textContent = 'Favorite';
        saveBtn.setAttribute('data-saved', 'false');
    }
}

// ✅ Toggle Favorite
async function toggleAddToFavourites(bookId, userId) {
    const saveBtn = document.getElementById('addToFavoriteBtn');
    const isAdded = saveBtn.getAttribute('data-saved') === 'true';

    saveBtn.disabled = true;

    try {
        if (isAdded) {
            await removeFavBook(bookId, userId);
        } else {
            await addBookToFav(bookId, userId);
        }
    } catch (error) {
        console.error("Error toggling favorite:", error);
    } finally {
        saveBtn.disabled = false;
    }
}

// ✅ Add Book to Favorite
async function addBookToFav(bookId, userId) {
    try {
        const response = await fetch('/user/viewbook/addbooktofav', {
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
            updateFavButtonState(true)
            showNotification('Book added to favorites successfully!', 'success');
        } else {
            updateFavButtonState(false)
            showNotification('Failed to add book', 'error');
        }
    } catch (error) {
        console.error("Failed to save book:", error);
        throw error;
    }
}

// ✅ Remove Favorite Book
async function removeFavBook(bookId, userId) {
    try {
        const response = await fetch('/user/viewbook/removebook', {
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
            updateFavButtonState(false);
            showNotification('Book removed from favorite list', 'success');
        } else {
            showNotification('Failed to remove book', 'error');
        }
    } catch (error) {
        console.error('Error removing book:', error);
        throw error;
    }
}

// ✅ Notification System
function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `fixed top-20 right-4 z-50 px-6 py-3 rounded-lg shadow-lg transition-all transform translate-x-0 ${
        type === 'success' ? 'bg-green-600' : 'bg-red-600'
    } text-white font-medium`;

    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 10);

    setTimeout(() => {
        notification.style.transform = 'translateX(400px)';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}
