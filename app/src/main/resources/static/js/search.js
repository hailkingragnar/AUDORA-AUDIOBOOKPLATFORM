document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById("searchInput");
    const autocompleteResults = document.getElementById("autocompleteResults");
    let debounceTimer;

    const MIN_CHARS = 2;

    searchInput.addEventListener('input', function (e) {
        clearTimeout(debounceTimer);
        const query = e.target.value.trim();

        if (query.length >= MIN_CHARS) {
            debounceTimer = setTimeout(() => {
                fetchAutocompleteResults(query);
            }, 300);
        } else {
            autocompleteResults.classList.add('hidden');
        }
    });

   document.addEventListener('click', function (e) {
       if (!e.target.closest("#searchInput") && !e.target.closest("#autocompleteResults")) {
           autocompleteResults.classList.add('hidden');
       }
   });

    async function fetchAutocompleteResults(query) {
        try {
            const response = await fetch(`/api/search/autocomplete?q=${encodeURIComponent(query)}`);
            const results = await response.json();

            if (results.length > 0) {
                renderAutocompleteResults(results);
                autocompleteResults.classList.remove('hidden');
            } else {
                autocompleteResults.classList.add('hidden');
            }
        } catch (error) {
            console.error('Search Failed:', error);
            autocompleteResults.classList.add('hidden');
        }
    }

    function renderAutocompleteResults(books) {
        autocompleteResults.innerHTML = books.map(book => `
            <a href="/user/viewbook/${book.audiobookId}" class="block px-4 py-3 hover:bg-gray-700 transition-colors">
                <div class="flex items-center">
                    <img src="/Images/Books/${book.image}"
                         alt="${book.title}"
                         class="w-10 h-10 object-cover rounded mr-3">
                    <div>
                        <h3 class="text-white font-medium">${book.title}</h3>
                        <p class="text-gray-400 text-sm">${book.author}</p>
                    </div>
                </div>
            </a>
        `).join('');
    }
});
