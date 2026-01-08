// Function to initialize Dashboard Logic
function initDashboard() {
    // -------------------------------------------------------------------------
    // 0. SIDEBAR ACTIVE STATE MANAGEMENT
    // -------------------------------------------------------------------------
    const currentPath = window.location.pathname;
    const sidebarLinks = document.querySelectorAll('#sidebar-wrapper .list-group-item');

    sidebarLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath === '/' && (currentPath === '/' || currentPath.startsWith('/dashboard'))) {
            link.classList.add('active-item');
        } else if (linkPath !== '/' && currentPath.includes(linkPath)) {
            link.classList.add('active-item');
        }
    });

    // -------------------------------------------------------------------------
    // 0.5 GLOBAL DARK MODE TOGGLE
    // -------------------------------------------------------------------------
    const themeSwitch = document.getElementById('theme-toggle-switch');

    // Remove existing listeners to prevent duplicates if init is called twice (safety)
    if (themeSwitch) {
        // Clone node to strip old listeners
        // const newSwitch = themeSwitch.cloneNode(true);
        // themeSwitch.parentNode.replaceChild(newSwitch, themeSwitch);
        // Actually, just binding click is fine if we ensure init runs once. 

        themeSwitch.onclick = function () {
            document.body.classList.toggle('dark-mode');
            let theme = 'light';
            if (document.body.classList.contains('dark-mode')) {
                theme = 'dark';
            }
            localStorage.setItem('theme', theme);
        };
    }

    // -------------------------------------------------------------------------
    // 1. STUDENTS DOUGHNUT CHART
    // -------------------------------------------------------------------------
    const ctxStudents = document.getElementById('studentsChart')?.getContext('2d');
    if (ctxStudents) {
        fetch('/api/dashboard/chart/gender')
            .then(response => response.json())
            .then(data => {
                const maleCount = data[0] || 0;
                const femaleCount = data[1] || 0;
                const total = maleCount + femaleCount;

                const maleEl = document.getElementById('male-count');
                const femaleEl = document.getElementById('female-count');
                if (maleEl) maleEl.innerText = maleCount;
                if (femaleEl) femaleEl.innerText = femaleCount;

                const malePct = total > 0 ? Math.round((maleCount / total) * 100) : 0;
                const femalePct = total > 0 ? Math.round((femaleCount / total) * 100) : 0;

                const malePctEl = document.getElementById('male-pct');
                const femalePctEl = document.getElementById('female-pct');
                if (malePctEl) malePctEl.innerText = malePct;
                if (femalePctEl) femalePctEl.innerText = femalePct;

                new Chart(ctxStudents, {
                    type: 'doughnut',
                    data: {
                        labels: ['Boys', 'Girls'],
                        datasets: [{
                            data: [maleCount, femaleCount],
                            backgroundColor: ['#AEE2FF', '#FFB7B2'],
                            borderWidth: 0,
                            hoverOffset: 4
                        }]
                    },
                    options: {
                        cutout: '75%',
                        plugins: { legend: { display: false } },
                        responsive: true,
                        maintainAspectRatio: false
                    }
                });
            })
            .catch(error => console.error('Error:', error));
    }

    // -------------------------------------------------------------------------
    // 2. ATTENDANCE BAR CHART
    // -------------------------------------------------------------------------
    const attendanceChartEl = document.getElementById('attendanceChart');
    if (attendanceChartEl) {
        const ctxAttendance = attendanceChartEl.getContext('2d');
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
                                data: data.present,
                                backgroundColor: '#DEDDFF',
                                borderRadius: 5,
                                barPercentage: 0.5
                            },
                            {
                                label: 'Absent',
                                data: data.absent,
                                backgroundColor: '#FEEFC3',
                                borderRadius: 5,
                                barPercentage: 0.5
                            }
                        ]
                    },
                    options: {
                        plugins: { legend: { display: false } },
                        scales: {
                            y: { beginAtZero: true, max: 100, grid: { color: '#f0f0f0', borderDash: [5, 5] }, ticks: { color: '#aaa' } },
                            x: { grid: { display: false }, ticks: { color: '#aaa' } }
                        },
                        responsive: true,
                        maintainAspectRatio: false
                    }
                });
            })
            .catch(error => console.error('Error:', error));
    }
}

// Ensure Init runs whether loaded now or later
if (document.readyState === 'loading') {
    document.addEventListener("DOMContentLoaded", initDashboard);
} else {
    initDashboard();
}
