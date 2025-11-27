  document.getElementById("rateBtn").addEventListener("click", function () {

    // ✅ Switch to Reviews Tab
    document.querySelectorAll(".tab-button").forEach(btn => {
      btn.classList.remove("border-blue-500", "text-white");
      btn.classList.add("text-gray-400", "border-transparent");
    });

    const reviewTabBtn = document.querySelector('[data-tab="reviews"]');
    reviewTabBtn.classList.add("border-blue-500", "text-white");
    reviewTabBtn.classList.remove("text-gray-400", "border-transparent");

    // ✅ Hide all tabs
    document.querySelectorAll(".tab-panel").forEach(tab => {
      tab.classList.add("hidden");
    });

    // ✅ Show reviews tab
    const reviewTab = document.getElementById("reviews-tab");
    reviewTab.classList.remove("hidden");

    // ✅ Scroll a LITTLE ABOVE the review form
    setTimeout(() => {
      const reviewSection = document.getElementById("review-section");
      const yOffset = -120; // 👈 adjust this value (smaller = less up)
      const y = reviewSection.getBoundingClientRect().top + window.pageYOffset + yOffset;

      window.scrollTo({ top: y, behavior: "smooth" });
    }, 200);

  });
