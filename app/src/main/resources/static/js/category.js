document.addEventListener('DOMContentLoaded', function() {
    // Categories dropdown functionality
    const categoriesDropdownButton = document.getElementById('categoriesDropdownButton');
    const categoriesDropdown = document.getElementById('categoriesDropdown');

    if (categoriesDropdownButton && categoriesDropdown) {
        categoriesDropdownButton.addEventListener('click', function(e) {
            e.stopPropagation();
            categoriesDropdown.classList.toggle('hidden');
        });

        // Close when clicking outside
        document.addEventListener('click', function() {
            categoriesDropdown.classList.add('hidden');
        });

        // Prevent dropdown from closing when clicking inside
        categoriesDropdown.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    }

    // Existing dropdown functionality (keep your existing code for other dropdowns)
    // ...
});