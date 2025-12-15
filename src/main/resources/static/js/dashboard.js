document.addEventListener("DOMContentLoaded", function () {

    // 0. SIDEBAR ACTIVE STATE
    // Get current path
    const currentPath = window.location.pathname;

    // Select all sidebar list items
    const sidebarLinks = document.querySelectorAll('#sidebar-wrapper .list-group-item');

    sidebarLinks.forEach(link => {
        const href = link.getAttribute('href');

        // Exact match for home '/'
        if (href === '/' && currentPath === '/') {
            link.classList.add('active-item');
        }
        // Starts with match for other sections (e.g. /teachers matches /teachers/new)
        else if (href !== '/' && currentPath.startsWith(href)) {
            link.classList.add('active-item');
        }
    });

    // 1. STUDENTS DOUGHNUT CHART
    const ctxStudents = document.getElementById('studentsChart')?.getContext('2d');
    if (ctxStudents) {
        fetch('/api/dashboard/chart/gender')
            .then(response => response.json())
            .then(data => {
                // Update Legend
                const boys = data[0];
                const girls = data[1];
                const total = boys + girls;

                // Update Counts
                const boysCountEl = document.getElementById('boys-count');
                const girlsCountEl = document.getElementById('girls-count');
                if (boysCountEl) boysCountEl.innerText = boys;
                if (girlsCountEl) girlsCountEl.innerText = girls;

                // Update Percentages (handle division by zero)
                const boysPct = total > 0 ? Math.round((boys / total) * 100) : 0;
                const girlsPct = total > 0 ? Math.round((girls / total) * 100) : 0;

                const boysPctEl = document.getElementById('boys-pct');
                const girlsPctEl = document.getElementById('girls-pct');
                if (boysPctEl) boysPctEl.innerText = boysPct;
                if (girlsPctEl) girlsPctEl.innerText = girlsPct;

                new Chart(ctxStudents, {
                    type: 'doughnut',
                    data: {
                        labels: ['Boys', 'Girls'],
                        datasets: [{
                            data: data, // [Boys, Girls] from API
                            backgroundColor: [
                                '#AEE2FF', // Light Blue for Boys
                                '#FEEFC3'  // Light Yellow for Girls
                            ],
                            borderWidth: 0,
                            hoverOffset: 4
                        }]
                    },
                    options: {
                        cutout: '75%', // Makes the donut thinner/thicker
                        plugins: {
                            legend: { display: false } // We built a custom legend in HTML
                        },
                        responsive: true,
                        maintainAspectRatio: false
                    }
                });
            })
            .catch(error => console.error('Error fetching student chart data:', error));
    }

    // 2. ATTENDANCE BAR CHART
    const ctxAttendance = document.getElementById('attendanceChart').getContext('2d');
    if (ctxAttendance) {
        fetch('/api/dashboard/chart/attendance')
            .then(response => response.json())
            .then(data => {
                new Chart(ctxAttendance, {
                    type: 'bar',
                    data: {
                        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
                        datasets: [
                            {
                                label: 'Present',
                                data: data.present, // From API
                                backgroundColor: '#DEDDFF', // Purple Bar
                                borderRadius: 5,
                                barPercentage: 0.5
                            },
                            {
                                label: 'Absent',
                                data: data.absent, // From API
                                backgroundColor: '#FEEFC3', // Yellow Bar
                                borderRadius: 5,
                                barPercentage: 0.5
                            }
                        ]
                    },
                    options: {
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                max: 100, // Or let it auto-scale? User code had max 100.
                                grid: {
                                    color: '#f0f0f0', // Very light grid lines
                                    borderDash: [5, 5]
                                },
                                ticks: { color: '#aaa' }
                            },
                            x: {
                                grid: { display: false },
                                ticks: { color: '#aaa' }
                            }
                        },
                        responsive: true,
                        maintainAspectRatio: false
                    }
                });
            })
            .catch(error => console.error('Error fetching attendance chart data:', error));
    }
});
