  const shareBtn = document.getElementById("shareBtn");
  const shareModal = document.getElementById("shareModal");
  const closeShare = document.getElementById("closeShare");

  const shareUrl = window.location.href;
  const shareText = "Check out this awesome audiobook!";

  // Open modal
  shareBtn.addEventListener("click", () => {
    shareModal.classList.remove("hidden");
    shareModal.classList.add("flex");

    document.getElementById("whatsappShare").href =
      `https://wa.me/?text=${encodeURIComponent(shareText + " " + shareUrl)}`;

    document.getElementById("messengerShare").href =
      `https://www.facebook.com/dialog/send?link=${encodeURIComponent(shareUrl)}`;
  });

  // Close modal
  closeShare.addEventListener("click", () => {
    shareModal.classList.add("hidden");
  });

  // ✅ Copy Link Function
  function copyLink() {
    navigator.clipboard.writeText(shareUrl).then(() => {
      alert("Link copied to clipboard!");
    });
  }

  // ✅ Instagram (Copy + Open)
  function shareInstagram() {
    navigator.clipboard.writeText(shareUrl).then(() => {
      window.open("https://www.instagram.com", "_blank");
      alert("Link copied! Paste it on Instagram.");
    });
  }
