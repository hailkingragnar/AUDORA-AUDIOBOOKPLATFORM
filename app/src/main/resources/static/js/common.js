document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded - script executing'); // Debug log

    // =============================================
    // PROFILE DROPDOWN FUNCTIONALITY
    // =============================================
    const profileDropdownButton = document.getElementById('profileDropdownButton');
    const profileDropdownMenu = document.getElementById('profileDropdownMenu');

    // =============================================
    // TAB FUNCTIONALITY
    // =============================================
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabPanels = document.querySelectorAll('.tab-panel');

    if (tabButtons.length > 0 && tabPanels.length > 0) {
        console.log('Tab elements found'); // Debug log
        tabButtons.forEach(button => {
            button.addEventListener('click', () => {
                const targetTab = button.getAttribute('data-tab');
                console.log('Tab clicked:', targetTab); // Debug log

                // Update button states
                tabButtons.forEach(btn => {
                    btn.classList.remove('border-blue-500', 'text-white');
                    btn.classList.add('border-transparent', 'text-gray-400');
                });
                button.classList.add('border-blue-500', 'text-white');
                button.classList.remove('border-transparent', 'text-gray-400');

                // Show/hide tab panels
                tabPanels.forEach(panel => {
                    panel.classList.add('hidden');
                    panel.classList.remove('active');
                });

                const targetPanel = document.getElementById(`${targetTab}-tab`);
                if (targetPanel) {
                    targetPanel.classList.remove('hidden');
                    targetPanel.classList.add('active');
                }
            });
        });
    } else {
        console.log('Tab elements not found'); // Debug log
    }

    // =============================================
    // PROFILE DROPDOWN
    // =============================================
    if (profileDropdownButton && profileDropdownMenu) {
        console.log('Profile dropdown elements found'); // Debug log
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
    } else {
        console.log('Profile dropdown elements not found'); // Debug log
    }

    // =============================================
    // AUDIO PLAYER FUNCTIONALITY
    // =============================================
    const audioElement = document.getElementById('audio-element');
    const bottomPlayer = document.getElementById('bottom-player');

    if (audioElement && bottomPlayer) {
        console.log('Audio player elements found'); // Debug log

        // Initialize player state
        let isPlaying = false;
        let currentChapterId = null;

        // Player UI Elements with null checks
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

        function updatePlayPauseButtons(playing) {
            const icon = playing ? 'fa-pause' : 'fa-play';
            if (playPauseBtn) playPauseBtn.innerHTML = `<i class="fas ${icon}"></i>`;
            if (coverPlayBtn) coverPlayBtn.innerHTML = `<i class="fas ${icon}"></i>`;
            if (mainPlayBtn) mainPlayBtn.innerHTML = `<i class="fas ${icon} mr-2"></i> ${playing ? 'Pause' : 'Play'}`;
        }

        function formatTime(seconds) {
            const minutes = Math.floor(seconds / 60);
            const secs = Math.floor(seconds % 60);
            return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
        }

        function updateVolumeIcon(volume) {
            if (volumeButton) {
                const icon = volumeButton.querySelector('i');
                if (volume === 0) {
                    icon.className = 'fas fa-volume-mute';
                } else if (volume < 0.5) {
                    icon.className = 'fas fa-volume-down';
                } else {
                    icon.className = 'fas fa-volume-up';
                }
            }
        }

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
        // EVENT LISTENERS
        // =============================================
        if (playPauseBtn) playPauseBtn.addEventListener('click', togglePlayPause);
        if (coverPlayBtn) coverPlayBtn.addEventListener('click', togglePlayPause);
        if (mainPlayBtn) mainPlayBtn.addEventListener('click', togglePlayPause);

        audioElement.addEventListener('timeupdate', function() {
            const currentTime = audioElement.currentTime;
            const duration = audioElement.duration || 1;

            if (progressBar) {
                const progressPercent = (currentTime / duration) * 100;
                progressBar.style.width = `${progressPercent}%`;
            }

            if (currentTimeEl) currentTimeEl.textContent = formatTime(currentTime);
            if (durationEl && duration && !isNaN(duration)) {
                durationEl.textContent = formatTime(duration);
            }
        });

        const playerProgressEl = document.querySelector('.player-progress');
        if (playerProgressEl) {
            playerProgressEl.addEventListener('click', function(e) {
                const progressBarWidth = this.offsetWidth;
                const clickPosition = e.offsetX;
                const duration = audioElement.duration;
                audioElement.currentTime = (clickPosition / progressBarWidth) * duration;
            });
        }

        if (volumeControl) {
            volumeControl.addEventListener('input', function() {
                audioElement.volume = this.value;
                updateVolumeIcon(this.value);
            });
        }

        if (volumeButton) {
            volumeButton.addEventListener('click', function() {
                if (audioElement.volume > 0) {
                    audioElement.volume = 0;
                    if (volumeControl) volumeControl.value = 0;
                    updateVolumeIcon(0);
                } else {
                    audioElement.volume = 1;
                    if (volumeControl) volumeControl.value = 1;
                    updateVolumeIcon(1);
                }
            });
        }

        const rewindBtn = document.getElementById('rewind-10');
        const forwardBtn = document.getElementById('forward-10');

        if (rewindBtn) {
            rewindBtn.addEventListener('click', function() {
                audioElement.currentTime = Math.max(0, audioElement.currentTime - 10);
            });
        }

        if (forwardBtn) {
            forwardBtn.addEventListener('click', function() {
                audioElement.currentTime = Math.min(audioElement.duration, audioElement.currentTime + 10);
            });
        }

        audioElement.addEventListener('loadedmetadata', function() {
            if (durationEl) durationEl.textContent = formatTime(audioElement.duration);
        });

        audioElement.addEventListener('ended', function() {
            updatePlayPauseButtons(false);
            isPlaying = false;
        });

    } else {
        console.log('Audio player elements not found'); // Debug log
    } // <-- This closing brace was missing in your original code

    // =============================================
    // CHAPTER NAVIGATION FUNCTIONALITY
    // =============================================
    function updatePlayingChapter(chapterId) {
        document.querySelectorAll('.episode-button i').forEach(icon => {
            icon.classList.remove('fa-pause', 'text-blue-400');
            icon.classList.add('fa-play', 'text-white');
        });

        if (chapterId) {
            const currentIcon = document.querySelector(`.episode-card[data-chapter-id="${chapterId}"] .episode-button i`);
            if (currentIcon) {
                currentIcon.classList.remove('fa-play', 'text-white');
                currentIcon.classList.add('fa-pause', 'text-blue-400');
            }
        }
    }

    // Handle chapter clicks
    document.addEventListener('click', function(e) {
        const card = e.target.closest('.episode-card');
        if (card) {
            const chapterId = card.getAttribute('data-chapter-id');

            if (e.target.closest('.episode-button') || e.target.classList.contains('fas')) {
                e.preventDefault();
                e.stopPropagation();

                const chapterTitle = card.querySelector('h3').textContent;
                const chapterAudioSrc = card.getAttribute('data-audio-src');

                if (audioElement && bottomPlayer) {
                    playChapter(chapterId, chapterAudioSrc);
                } else {
                    updatePlayingChapter(chapterId);
                }
            }
        }
    });

    console.log('Script initialization complete'); // Debug log
});