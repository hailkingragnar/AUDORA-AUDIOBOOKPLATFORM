
const stars = document.querySelectorAll("#starRating i");
let selectedRating = 0;

// Detect if hover is in left or right half of the star
function getStarValue(event, star) {
  const rect = star.getBoundingClientRect();
  const x = event.clientX - rect.left;
  return x < rect.width / 2 ? 0.5 : 1;
}

// Handle hover preview
stars.forEach(star => {
  star.addEventListener("mousemove", (e) => {
    const baseValue = Number(star.dataset.value);
    const half = getStarValue(e, star);

    const hoverValue = baseValue - (1 - half);

    updateStars(hoverValue);
  });

  star.addEventListener("mouseleave", () => {
    updateStars(selectedRating);
  });

  // Click to set rating
  star.addEventListener("click", (e) => {
    const baseValue = Number(star.dataset.value);
    const half = getStarValue(e, star);

    selectedRating = baseValue - (1 - half);
    document.getElementById("ratingValue").value = selectedRating;

    updateStars(selectedRating);
  });
});

// Function to show correct stars
function updateStars(rating) {
  stars.forEach(star => {
    const value = Number(star.dataset.value);

    if (rating >= value) {
      // Full star
      star.className = "fa-solid fa-star text-yellow-400";
    }
    else if (rating + 0.5 >= value) {
      // Half star
      star.className = "fa-solid fa-star-half-stroke text-yellow-400";
    }
    else {
      // Empty star
      star.className = "fa-regular fa-star text-gray-400";
    }
  });
}


//Star and reviews

document.getElementById("submitReviewBtn").addEventListener("click", async () => {
  console.log("Loaded...");
  console.log(document.getElementById("submitReviewBtn"));
  const rating = document.getElementById("ratingValue").value;
  const review = document.getElementById("reviewText").value;
  const userId = document.getElementById("userId").value
  const bookId = document.getElementById("bookId").value


  if (!rating || rating == 0) {
    alert("Please select a star rating!");
    return;
  }
  if (review.trim().length === 0) {
    alert("Please write a review!");
    return;
  }

  const data = {
    rating: rating,
    review: review,
    bookId: bookId,
    userId: userId
  };

  try {

    const response = await fetch("/user/viewbook/setreview", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify(data)
    });

    if (response.ok) {
      alert("Review submitted!");
      location.reload();
    } else {
      alert("Error submitting review!");
    }

  } catch (err) {
    console.error(err);
    alert("Network error!");
  }
});
