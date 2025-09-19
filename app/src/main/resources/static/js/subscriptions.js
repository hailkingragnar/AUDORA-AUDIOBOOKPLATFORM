  // You can add your JavaScript here
    document.addEventListener('DOMContentLoaded', function() {
        console.log('Subscription page loaded');

        // Example: Add click handlers for plan selection
        document.querySelectorAll('.plan-card button').forEach(button => {
            button.addEventListener('click', function() {
                const plan = this.closest('.plan-card').querySelector('h3').textContent;
                console.log(`Selected plan: ${plan}`);
                // Add your plan selection logic here
            });
        });
    });