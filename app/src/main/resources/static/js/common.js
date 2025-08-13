document.addEventListener('DOMContentLoaded', function() {
  // =============================================
  // PROFILE DROPDOWN FUNCTIONALITY
  // =============================================
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

  // =============================================
  // AUDIO PLAYER CORE FUNCTIONALITY
  // =============================================
  const audioElement = document.getElementById('audio-element');
  const bottomPlayer = document.getElementById('bottom-player');

  if (audioElement && bottomPlayer) {
    // Initialize player state
    let isPlaying = false;
    let currentChapterId = null;

    // Player UI Elements
    const playPauseBtn = document.getElementById('play-pause');
    const coverPlayBtn = document.getElementById('cover-play-button');
    const mainPlayBtn = document.getElementById('main-play-button');
    const progressBar = document.getElementById('progress-bar');
    const currentTimeEl = document.getElementById('current-time');
    const durationEl = document.getElementById('duration');
    const volumeControl = document.getElementById('volume-control');
    const volumeButton = document.getElementById('volume-button');

    // Set up bottom player styles
    bottomPlayer.style.position = 'fixed';
    bottomPlayer.style.bottom = '0';
    bottomPlayer.style.left = '0';
    bottomPlayer.style.right = '0';
    bottomPlayer.style.backgroundColor = '#fff';
    bottomPlayer.style.padding = '10px';
    bottomPlayer.style.boxShadow = '0 -2px 10px rgba(0,0,0,0.2)';
    bottomPlayer.style.zIndex = '1000';
    bottomPlayer.style.display = 'none';

    // =============================================
    // PLAYER CONTROL FUNCTIONS
    // =============================================

    /**
     * Toggle between play and pause states
     */
    async function togglePlayPause() {
      try {
        if (audioElement.paused) {
          await audioElement.play();
          updatePlayPauseButtons(true);
          bottomPlayer.style.display = 'flex';
          isPlaying = true;
        } else {
          audioElement.pause();
          updatePlayPauseButtons(false);
          isPlaying = false;
        }
      } catch (error) {
        console.error('Playback failed:', error);
      }
    }

    /**
     * Update all play/pause buttons in the UI
     * @param {boolean} playing - Whether audio is currently playing
     */
    function updatePlayPauseButtons(playing) {
      const icon = playing ? 'fa-pause' : 'fa-play';
      if (playPauseBtn) playPauseBtn.innerHTML = `<i class="fas ${icon}"></i>`;
      if (coverPlayBtn) coverPlayBtn.innerHTML = `<i class="fas ${icon}"></i>`;
      if (mainPlayBtn) mainPlayBtn.innerHTML = `<i class="fas ${icon} mr-2"></i> ${playing ? 'Pause' : 'Play'}`;
    }

    /**
     * Format seconds into MM:SS display format
     * @param {number} seconds - Time in seconds
     * @returns {string} Formatted time string
     */
    function formatTime(seconds) {
      const minutes = Math.floor(seconds / 60);
      const secs = Math.floor(seconds % 60);
      return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
    }

    /**
     * Update volume icon based on current volume level
     * @param {number} volume - Current volume level (0-1)
     */
    function updateVolumeIcon(volume) {
      const icon = volumeButton.querySelector('i');
      if (volume === 0) {
        icon.className = 'fas fa-volume-mute';
      } else if (volume < 0.5) {
        icon.className = 'fas fa-volume-down';
      } else {
        icon.className = 'fas fa-volume-up';
      }
    }

    /**
     * Load and play a specific chapter
     * @param {string} chapterId - ID of the chapter to play
     * @param {string} audioSrc - URL of the audio file
     */
    async function playChapter(chapterId, audioSrc) {
      try {
        if (audioElement.src !== audioSrc) {
          audioElement.src = audioSrc;
          audioElement.load();
        }
        
        currentChapterId = chapterId;
        updatePlayingChapter(chapterId);
        await togglePlayPause();
      } catch (error) {
        console.error('Error loading chapter:', error);
      }
    }

    // =============================================
    // EVENT LISTENERS FOR PLAYER CONTROLS
    // =============================================

    // Play/Pause buttons
    playPauseBtn?.addEventListener('click', togglePlayPause);
    coverPlayBtn?.addEventListener('click', togglePlayPause);
    mainPlayBtn?.addEventListener('click', togglePlayPause);

    // Progress bar updates
    audioElement.addEventListener('timeupdate', function() {
      const currentTime = audioElement.currentTime;
      const duration = audioElement.duration || 1;

      // Update progress bar width
      const progressPercent = (currentTime / duration) * 100;
      progressBar.style.width = `${progressPercent}%`;

      // Update time displays
      currentTimeEl.textContent = formatTime(currentTime);
      if (duration && !isNaN(duration)) {
        durationEl.textContent = formatTime(duration);
      }
    });

    // Click on progress bar to seek
    document.querySelector('.player-progress')?.addEventListener('click', function(e) {
      const progressBarWidth = this.offsetWidth;
      const clickPosition = e.offsetX;
      const duration = audioElement.duration;
      audioElement.currentTime = (clickPosition / progressBarWidth) * duration;
    });

    // Volume control
    volumeControl?.addEventListener('input', function() {
      audioElement.volume = this.value;
      updateVolumeIcon(this.value);
    });

    // Toggle mute
    volumeButton?.addEventListener('click', function() {
      if (audioElement.volume > 0) {
        audioElement.volume = 0;
        volumeControl.value = 0;
        updateVolumeIcon(0);
      } else {
        audioElement.volume = 1;
        volumeControl.value = 1;
        updateVolumeIcon(1);
      }
    });

    // Skip forward/backward
    document.getElementById('rewind-10')?.addEventListener('click', function() {
      audioElement.currentTime = Math.max(0, audioElement.currentTime - 10);
    });

    document.getElementById('forward-10')?.addEventListener('click', function() {
      audioElement.currentTime = Math.min(audioElement.duration, audioElement.currentTime + 10);
    });

    // Initialize duration when metadata is loaded
    audioElement.addEventListener('loadedmetadata', function() {
      durationEl.textContent = formatTime(audioElement.duration);
    });

    // Handle audio ending
    audioElement.addEventListener('ended', function() {
      updatePlayPauseButtons(false);
      isPlaying = false;
    });
  }

  // =============================================
  // CHAPTER NAVIGATION FUNCTIONALITY
  // =============================================

  /**
   * Update the currently playing chapter indicator in the UI
   * @param {string} chapterId - ID of the chapter to highlight
   */
  function updatePlayingChapter(chapterId) {
    // Reset all buttons
    document.querySelectorAll('.episode-button i').forEach(icon => {
      icon.classList.remove('fa-pause', 'text-blue-400');
      icon.classList.add('fa-play', 'text-white');
    });

    // Set current playing chapter
    if (chapterId) {
      const currentIcon = document.querySelector(`.episode-card[data-chapter-id="${chapterId}"] .episode-button i`);
      if (currentIcon) {
        currentIcon.classList.remove('fa-play', 'text-white');
        currentIcon.classList.add('fa-pause', 'text-blue-400');
      }
    }
  }

  // Handle chapter clicks with event delegation
  document.addEventListener('click', function(e) {
    const card = e.target.closest('.episode-card');
    if (card) {
      const chapterId = card.getAttribute('data-chapter-id');
      
      // If clicking the play button or its icon
      if (e.target.closest('.episode-button') || e.target.classList.contains('fas')) {
        e.preventDefault();
        e.stopPropagation();
        
        // Get chapter details (you would need to implement this based on your data structure)
        const chapterTitle = card.querySelector('h3').textContent;
        const chapterAudioSrc = card.getAttribute('data-audio-src'); // You need to add this attribute
        
        // Play the chapter
        if (audioElement && bottomPlayer) {
          playChapter(chapterId, chapterAudioSrc);
        } else {
          updatePlayingChapter(chapterId);
          // Handle chapter change without player (if needed)
        }
      }
      // Otherwise, the link will follow normally to the chapter page
    }
  });

  // =============================================
  // DYNAMIC BACKGROUND COLOR (for cover art)
  // =============================================
  const bgImage = document.getElementById('bg-image-source');
  const coverBackground = document.getElementById('cover-background');

  bgImage.onload = function() {
    // In a real implementation, use a color extraction library here
    coverBackground.style.background = 'linear-gradient(to bottom, #1e3a8a, #111827)';
  };

  if (bgImage.complete) bgImage.onload();
});