package com.num.management.config;

import com.num.management.dto.UserRegistrationDto;
import com.num.management.model.Attendance;
import com.num.management.model.ClassEntity;
import com.num.management.model.ClassSchedule;
import com.num.management.model.Student;
import com.num.management.model.Subject;
import com.num.management.model.Teacher;
import com.num.management.repository.AttendanceRepository;
import com.num.management.repository.ClassRepository;
import com.num.management.repository.StudentRepository;
import com.num.management.repository.SubjectRepository;
import com.num.management.repository.TeacherRepository;
import com.num.management.repository.UserRepository;
import com.num.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private com.num.management.repository.ClassScheduleRepository classScheduleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Method runs on application startup to seed initial data
        seedUsers(); // Create default admin user
        seedSubjects(); // Create initial subjects
        updateSubjectFaculties(); // Ensure faculties are assigned
        seedTeachers(); // Create dummy teachers
        seedClasses(); // Create class levels
        seedStudents(); // Create dummy students
        seedAttendance();// Generate random attendance for the current week
        fixExistingNames();// Utility to fix/update seeded names to look more authentic
        seedPaginationData(); // Ensure specific class has 11 students
        ensureTotalStudentCount(500); // Ensure total students is around 500
        ensureAllClassesHaveStudents(); // Ensure every class has at least some students
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            // Admin
            UserRegistrationDto admin = new UserRegistrationDto();
            admin.setFirstName("Seang");
            admin.setLastName("Hai");
            admin.setEmail("admin@num.edu.kh");
            admin.setPassword("password");
            admin.setRole("Admin");
            userService.save(admin);
            System.out.println("Seeded Admin: admin@num.edu.kh / password");

            // Teacher
            UserRegistrationDto teacher = new UserRegistrationDto();
            teacher.setFirstName("Sokha");
            teacher.setLastName("Teacher");
            teacher.setEmail("teacher@num.edu.kh");
            teacher.setPassword("password");
            teacher.setRole("Teacher");
            userService.save(teacher);
            System.out.println("Seeded Teacher: teacher@num.edu.kh / password");

            // Student
            UserRegistrationDto student = new UserRegistrationDto();
            student.setFirstName("Dara");
            student.setLastName("Student");
            student.setEmail("student@gmail.com"); // Changed to @gmail.com
            student.setPassword("password");
            student.setRole("Student");
            userService.save(student);
            System.out.println("Seeded Student: student@gmail.com / password");
        }

        // Ensure we have at least 5 admins total
        seedAdditionalAdmins();
    }

    private void seedAdditionalAdmins() {
        // We already have admin@num.edu.kh (Seang Hai)
        // Add 4 more to reach 5
        String[] extraAdmins = { "Virak", "Sophea", "Borey", "Chenda" };

        for (int i = 0; i < extraAdmins.length; i++) {
            String email = "admin" + (i + 2) + "@num.edu.kh"; // admin2, admin3, admin4, admin5
            if (userRepository.findByEmail(email).isEmpty()) {
                UserRegistrationDto admin = new UserRegistrationDto();
                admin.setFirstName(extraAdmins[i]);
                admin.setLastName("Admin");
                admin.setEmail(email);
                admin.setPassword("password");
                admin.setRole("Admin");
                userService.save(admin);
                System.out.println("Seeded Additional Admin: " + email);
            }
        }
    }

    private void seedSubjects() {
        // IT & Computer Science
        createSubjectIfNotFound("Python Programming I & II", "CS-104", "Python Basics & Advanced");
        createSubjectIfNotFound("Networking Essentials (CISCO-ITE)", "NET-101", "Network Fundamentals");
        createSubjectIfNotFound("Web Development (Static & Dynamic)", "WEB-201", "HTML, CSS, JS, PHP");
        createSubjectIfNotFound("Algorithms and Data Structures", "CS-202", "Advanced Algorithms");

        // BIT
        createSubjectIfNotFound("Accounting for Computing", "BIT-101", "Tech Accounting");
        createSubjectIfNotFound("Digital Transformation Strategy", "BIT-201", "Business Tech Strategy");
        createSubjectIfNotFound("E-Commerce Systems", "BIT-301", "Online Business Platforms");

        // Robotics
        createSubjectIfNotFound("Programming with ROS", "ROB-201", "Robot Operating System");
        createSubjectIfNotFound("Control Systems & Power Electronics", "ROB-301", "Electronics Control");
        createSubjectIfNotFound("IoT (Internet of Things) Projects", "IOT-301", "Smart Devices");

        // Legacy/Shared (Keep some if needed or leave as is)
        createSubjectIfNotFound("Advanced Web Development", "WEB-001", "Spring Boot & React");
        createSubjectIfNotFound("Database Systems", "DB-201", "SQL & NoSQL");

        // Management
        createSubjectIfNotFound("Principles of Management", "MGT-101", "Management Basics");
        createSubjectIfNotFound("Marketing Fundamentals", "MKT-101", "Intro to Marketing");
        createSubjectIfNotFound("HR Management", "HRM-201", "Human Resources");
        createSubjectIfNotFound("Marketing Management", "MKT-201", "Advanced Marketing");
        createSubjectIfNotFound("International Marketing", "MKT-301", "Global Marketing");
        createSubjectIfNotFound("International Business (iBBA)", "IBB-101", "Global Business");
        createSubjectIfNotFound("Global Entrepreneurship", "ENT-101", "Innovation & Startups");

        // Finance
        createSubjectIfNotFound("Financial Accounting", "FIN-101", "Accounting Basics");
        createSubjectIfNotFound("Banking Operations", "BNK-201", "Bank Management");
        createSubjectIfNotFound("FinTech", "FIN-301", "Financial Technology");
        createSubjectIfNotFound("Professional Accounting", "ACC-201", "CPA Prep");
        createSubjectIfNotFound("Auditing and Taxation", "ACC-301", "Audit & Tax");

        // Economics
        createSubjectIfNotFound("Microeconomics", "ECO-101", "Micro Theory");
        createSubjectIfNotFound("Macroeconomics", "ECO-102", "Macro Theory");
        createSubjectIfNotFound("Digital Economy", "DEC-201", "Digital Markets");
        createSubjectIfNotFound("Eco-Business", "ECO-201", "Ecological Business");
        createSubjectIfNotFound("Smart City Planning Management", "SCP-101", "Urban Planning");

        // Law
        createSubjectIfNotFound("Civil Law", "LAW-101", "Civil Code");
        createSubjectIfNotFound("Public Law", "LAW-201", "Public Law Framework");
        createSubjectIfNotFound("International Law", "LAW-301", "Intl Legal Systems");
        createSubjectIfNotFound("Private Law", "LAW-102", "Private Rights");
        createSubjectIfNotFound("Commercial Law", "LAW-202", "Business Law");
        createSubjectIfNotFound("International Relations Law", "LAW-302", "Diplomatic Law");

        // Tourism
        createSubjectIfNotFound("Hospitality Management", "HOS-101", "Hotel Ops");
        createSubjectIfNotFound("Tourism Development", "TOU-201", "Tourism Planning");

        // Languages
        createSubjectIfNotFound("English Literature", "ENG-101", "Literature Review");
        createSubjectIfNotFound("Korean Language", "KOR-101", "Korean Basics");
        createSubjectIfNotFound("Japanese Language", "JPN-101", "Japanese Basics");

        System.out.println("Seeded/Verified Subjects");
    }

    private void createSubjectIfNotFound(String name, String code, String desc) {
        if (subjectRepository.findAll().stream().noneMatch(s -> s.getName().equals(name))) {
            subjectRepository.save(new Subject(name, code, desc));
        }
    }

    private void updateSubjectFaculties() {
        List<Subject> allSubjects = subjectRepository.findAll();
        boolean updated = false;

        // Define mappings
        java.util.Map<String, List<String>> facultySubjects = new java.util.HashMap<>();
        facultySubjects.put("Faculty of Information Technology", Arrays.asList(
                "Python Programming I & II", "Networking Essentials (CISCO-ITE)", "Web Development (Static & Dynamic)",
                "Algorithms and Data Structures",
                "Accounting for Computing", "Digital Transformation Strategy", "E-Commerce Systems",
                "Programming with ROS", "Control Systems & Power Electronics", "IoT (Internet of Things) Projects",
                "Java Programming", "Database Systems", "Advanced Web Development"));
        facultySubjects.put("Faculty of Management", Arrays.asList(
                "Principles of Management", "Marketing Fundamentals", "HR Management", "Marketing Management",
                "International Marketing", "International Business (iBBA)", "Global Entrepreneurship"));
        facultySubjects.put("Faculty of Finance & Accounting", Arrays.asList(
                "Financial Accounting", "Banking Operations", "FinTech", "Professional Accounting",
                "Auditing and Taxation"));
        facultySubjects.put("Faculty of Economics", Arrays.asList(
                "Microeconomics", "Macroeconomics", "Digital Economy", "Eco-Business",
                "Smart City Planning Management"));
        facultySubjects.put("Faculty of Law", Arrays.asList(
                "Civil Law", "Public Law", "International Law", "Private Law", "Commercial Law",
                "International Relations Law"));
        facultySubjects.put("Faculty of Tourism & Hospitality", Arrays.asList(
                "Hospitality Management", "Tourism Development"));
        facultySubjects.put("Faculty of Foreign Languages", Arrays.asList(
                "English Literature", "Korean Language", "Japanese Language"));

        for (Subject s : allSubjects) {
            // Check if faculty is missing or unassigned
            if (s.getFaculty() == null || s.getFaculty().equals("Unassigned")) {
                for (java.util.Map.Entry<String, List<String>> entry : facultySubjects.entrySet()) {
                    if (entry.getValue().contains(s.getName())) {
                        s.setFaculty(entry.getKey());
                        subjectRepository.save(s);
                        updated = true;
                        break;
                    }
                }
            }
        }
        if (updated) {
            System.out.println("Updated Subject Faculties");
        }
    }

    private void fixExistingClasses() {
        List<ClassEntity> allClasses = classRepository.findAll();
        List<Subject> allSubjects = subjectRepository.findAll();

        // Helper lists (Live Fetch)

        // Define specific sub-lists for accurate fix
        List<Subject> csSubjects = allSubjects.stream().filter(s -> Arrays.asList(
                "Python Programming I & II", "Networking Essentials (CISCO-ITE)",
                "Web Development (Static & Dynamic)", "Algorithms and Data Structures", "Database Systems")
                .contains(s.getName())).collect(Collectors.toList());

        List<Subject> bitSubjects = allSubjects.stream().filter(s -> Arrays.asList(
                "Accounting for Computing", "Digital Transformation Strategy", "E-Commerce Systems",
                "Database Systems", "Web Development (Static & Dynamic)")
                .contains(s.getName())).collect(Collectors.toList());

        List<Subject> robSubjects = allSubjects.stream().filter(s -> Arrays.asList(
                "Programming with ROS", "Control Systems & Power Electronics", "IoT (Internet of Things) Projects",
                "Python Programming I & II")
                .contains(s.getName())).collect(Collectors.toList());
        List<Subject> mgmtSubs = allSubjects.stream().filter(s -> Arrays
                .asList("Principles of Management", "Marketing Fundamentals", "HR Management", "Marketing Management",
                        "International Marketing", "International Business (iBBA)", "Global Entrepreneurship")
                .contains(s.getName()))
                .collect(Collectors.toList());
        List<Subject> finSubs = allSubjects.stream().filter(
                s -> Arrays.asList("Financial Accounting", "Banking Operations", "FinTech", "Professional Accounting",
                        "Auditing and Taxation").contains(s.getName()))
                .collect(Collectors.toList());
        List<Subject> ecoSubs = allSubjects.stream()
                .filter(s -> Arrays.asList("Microeconomics", "Macroeconomics", "Digital Economy", "Eco-Business",
                        "Smart City Planning Management").contains(s.getName()))
                .collect(Collectors.toList());
        List<Subject> lawSubs = allSubjects.stream()
                .filter(s -> Arrays.asList("Civil Law", "Public Law", "International Law", "Private Law",
                        "Commercial Law", "International Relations Law").contains(s.getName()))
                .collect(Collectors.toList());
        List<Subject> tourSubs = allSubjects.stream()
                .filter(s -> Arrays.asList("Hospitality Management", "Tourism Development").contains(s.getName()))
                .collect(Collectors.toList());
        List<Subject> langSubs = allSubjects.stream().filter(
                s -> Arrays.asList("English Literature", "Korean Language", "Japanese Language").contains(s.getName()))
                .collect(Collectors.toList());

        boolean updated = false;
        for (ClassEntity cls : allClasses) {
            if (cls.getFaculty() == null)
                continue;

            if (cls.getSchedule() == null) {
                cls.setSchedule("Monday - Friday");
                updated = true;
            }

            if (cls.getFaculty().contains("Information Technology")) {
                if (cls.getDepartment() != null && cls.getDepartment().equalsIgnoreCase("BIT")) {
                    cls.setSubjects(bitSubjects);
                } else if (cls.getDepartment() != null && cls.getDepartment().equalsIgnoreCase("Robotic")) {
                    cls.setSubjects(robSubjects);
                } else {
                    cls.setSubjects(csSubjects);
                }
                updated = true;
            } else if (cls.getFaculty().contains("Management")) {
                cls.setSubjects(mgmtSubs);
                updated = true;
            } else if (cls.getFaculty().contains("Finance")) {
                cls.setSubjects(finSubs);
                updated = true;
            } else if (cls.getFaculty().contains("Economics")) {
                cls.setSubjects(ecoSubs);
                updated = true;
            } else if (cls.getFaculty().contains("Law")) {
                cls.setSubjects(lawSubs);
                updated = true;
            } else if (cls.getFaculty().contains("Tourism")) {
                cls.setSubjects(tourSubs);
                updated = true;
            } else if (cls.getFaculty().contains("Languages")) {
                cls.setSubjects(langSubs);
                updated = true;
            }

            // Backfill Detailed Schedules if missing
            if (cls.getSchedules() == null || cls.getSchedules().isEmpty()) {
                if (cls.getSubjects() != null && !cls.getSubjects().isEmpty()) {
                    java.util.List<ClassSchedule> newScheds = new java.util.ArrayList<>();
                    String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri" };
                    for (int i = 0; i < days.length; i++) {
                        if (i < cls.getSubjects().size()) {
                            newScheds.add(new ClassSchedule(days[i], cls.getSubjects().get(i), cls));
                        }
                    }
                    cls.setSchedules(newScheds);
                    updated = true;
                }
            }

            if (updated)
                classRepository.save(cls);
        }
        if (updated)
            System.out.println("Verified/Updated Class Subjects");
    }

    private final String[] TEACHER_NAMES = {
            "Sokha", "Vannak", "Bopha", "Chanthou", "Rithy",
            "Sophea", "Vanna", "Dara", "Sothea", "Chenda"
    };

    private final String[] FAMILY_NAMES = {
            "Sok", "Chan", "Chea", "Heng", "Lim", "Ngoun", "Ou", "Seng", "Tea", "Vong",
            "Keo", "Ouk", "Khim", "Mao", "Ros", "Prum", "Nhem", "Kong", "Tek", "Tob", "Pak",
            "Ly", "Chhun", "Ung", "Tang", "Sim", "Khuon", "Eang", "Ty", "Chhay"
    };

    private final String[] MALE_STUDENT_NAMES = {
            "Dara", "Vuthy", "Sambo", "Visal", "Oudom", "Rith", "Sovann", "Pheakdei", "Khemara",
            "Virak", "Sambath", "Borey", "Narin", "Panha", "Piseth", "Seyha", "Tola", "Veasna", "Vibol",
            "Vichea", "Vidya", "Vireak", "Narith", "Chakriya", "Sareth", "Arun"
    };

    private final String[] FEMALE_STUDENT_NAMES = {
            "Bopha", "Thida", "Neary", "Chantrea", "Devi", "Sreyroth", "Kunthea", "Dalin", "Sreypov", "Kannitha",
            "Kolab", "Leakena", "Malis", "Phirun", "Pich", "Rachana", "Rathana", "Ravy", "Rom Chang", "Samnang",
            "Serey", "Sokea", "Sopheary", "Soriya", "Sotheary", "Sreymom", "Sreynich", "Sokha"
    };

    // Helper to generate full name
    private String generateFullName(String gender) {
        String familyName = FAMILY_NAMES[(int) (Math.random() * FAMILY_NAMES.length)];
        String givenName;
        if ("Male".equalsIgnoreCase(gender)) {
            givenName = MALE_STUDENT_NAMES[(int) (Math.random() * MALE_STUDENT_NAMES.length)];
        } else {
            givenName = FEMALE_STUDENT_NAMES[(int) (Math.random() * FEMALE_STUDENT_NAMES.length)];
        }
        return familyName + " " + givenName;
    }

    private void seedTeachers() {
        if (teacherRepository.count() < 50) {
            List<Subject> subjects = subjectRepository.findAll();
            if (subjects.isEmpty())
                return;

            long currentCount = teacherRepository.count();
            int needed = 50 - (int) currentCount;

            for (int i = 0; i < needed; i++) {
                Teacher t = new Teacher();
                String name;
                String gender;

                // Use fixed names first if available
                if (currentCount + i < TEACHER_NAMES.length) {
                    name = TEACHER_NAMES[(int) (currentCount + i)];
                    // Just a simple guess for gender based on index or random
                    gender = (currentCount + i) % 2 == 0 ? "Female" : "Male";
                } else {
                    // Generate random for the rest
                    boolean isMale = Math.random() > 0.5;
                    gender = isMale ? "Male" : "Female";
                    name = generateFullName(gender);
                }

                t.setName(name);
                // Create unique email
                t.setEmail(name.toLowerCase().replace(" ", "") + (int) (Math.random() * 1000) + "@num.edu.kh");

                // Assign 1 or 2 random subjects
                java.util.Set<Subject> teacherSubjects = new java.util.HashSet<>();
                teacherSubjects.add(subjects.get((int) (Math.random() * subjects.size()))); // Ensure at least one
                if (Math.random() > 0.5) { // 50% chance for a second subject
                    teacherSubjects.add(subjects.get((int) (Math.random() * subjects.size())));
                }
                t.setSubjects(teacherSubjects);

                t.setPhoneNumber(String.format("012%06d", (int) (Math.random() * 1000000)));
                teacherRepository.save(t);
            }
            System.out.println("Seeded/Ensured 50 Teachers.");
        }
    }

    private void seedClasses() {
        fixExistingClasses();
        // Check removed to allow upsert of new classes
        {
            List<Subject> allSubjects = subjectRepository.findAll();

            // Helper lists
            List<Subject> mgmtSubs = allSubjects.stream().filter(s -> Arrays
                    .asList("Principles of Management", "Marketing Fundamentals", "HR Management",
                            "Marketing Management", "International Marketing", "International Business (iBBA)",
                            "Global Entrepreneurship")
                    .contains(s.getName()))
                    .collect(Collectors.toList());
            List<Subject> finSubs = allSubjects.stream().filter(
                    s -> Arrays.asList("Financial Accounting", "Banking Operations", "FinTech",
                            "Professional Accounting", "Auditing and Taxation").contains(s.getName()))
                    .collect(Collectors.toList());
            List<Subject> ecoSubs = allSubjects.stream()
                    .filter(s -> Arrays.asList("Microeconomics", "Macroeconomics", "Digital Economy", "Eco-Business",
                            "Smart City Planning Management").contains(s.getName()))
                    .collect(Collectors.toList());
            List<Subject> lawSubs = allSubjects.stream()
                    .filter(s -> Arrays.asList("Civil Law", "Public Law", "International Law", "Private Law",
                            "Commercial Law", "International Relations Law").contains(s.getName()))
                    .collect(Collectors.toList());
            List<Subject> tourSubs = allSubjects.stream()
                    .filter(s -> Arrays.asList("Hospitality Management", "Tourism Development").contains(s.getName()))
                    .collect(Collectors.toList());
            List<Subject> langSubs = allSubjects.stream().filter(s -> Arrays
                    .asList("English Literature", "Korean Language", "Japanese Language").contains(s.getName()))
                    .collect(Collectors.toList());

            // 1. FACULTY OF INFORMATION TECHNOLOGY (IT)
            String facIT = "Faculty of Information Technology";

            // Specific Lists
            List<Subject> csSubjects = allSubjects.stream().filter(s -> Arrays.asList(
                    "Python Programming I & II", "Networking Essentials (CISCO-ITE)",
                    "Web Development (Static & Dynamic)", "Algorithms and Data Structures", "Database Systems")
                    .contains(s.getName())).collect(Collectors.toList());

            List<Subject> bitSubjects = allSubjects.stream().filter(s -> Arrays.asList(
                    "Accounting for Computing", "Digital Transformation Strategy", "E-Commerce Systems",
                    "Database Systems", "Web Development (Static & Dynamic)")
                    .contains(s.getName())).collect(Collectors.toList());

            List<Subject> robSubjects = allSubjects.stream().filter(s -> Arrays.asList(
                    "Programming with ROS", "Control Systems & Power Electronics", "IoT (Internet of Things) Projects",
                    "Python Programming I & II")
                    .contains(s.getName())).collect(Collectors.toList());

            String[] itSections = { "A", "B", "C" };
            for (String sec : itSections) {
                saveClass("IT - Year 1 - Group " + sec, "Freshman IT", facIT, "IT", csSubjects);
            }
            saveClass("BIT - Year 2", "Sophomore BIT", facIT, "BIT", bitSubjects);
            saveClass("Robotic - Year 1", "Freshman Robotic", facIT, "Robotic", robSubjects);

            // 2. FACULTY OF MANAGEMENT
            String facMgmt = "Faculty of Management";
            saveClass("MGT - Year 2 - Group A", "General Management", facMgmt, "General Management", mgmtSubs);
            saveClass("Mgmt - Year 1 - Group A", "General Management Freshman", facMgmt, "General Management",
                    mgmtSubs);
            saveClass("Marketing - Year 2", "Marketing Sophomore", facMgmt, "Marketing Management", mgmtSubs);
            saveClass("iBBA - Year 1", "International Business Freshman", facMgmt, "International Business (iBBA)",
                    mgmtSubs);

            // 3. FACULTY OF FINANCE & ACCOUNTING
            String facFin = "Faculty of Finance & Accounting";
            saveClass("FIN - Year 1 - Banking", "Banking Class", facFin, "Finance and Banking", finSubs);
            saveClass("Banking - Year 1", "Banking Freshman", facFin, "Banking Operations", finSubs);
            saveClass("Finance - Year 3", "Finance Junior", facFin, "Financial Technology (FinTech)", finSubs);

            // 4. FACULTY OF ECONOMICS
            String facEco = "Faculty of Economics";
            saveClass("ECO - Year 3 - Development", "Development Class", facEco, "Economics for Development", ecoSubs);
            saveClass("Eco - Year 2", "Business Economics", facEco, "Eco-Business", ecoSubs);

            // 5. FACULTY OF LAW
            String facLaw = "Faculty of Law";
            saveClass("LAW - Year 4 - iLaw", "International Law Class", facLaw, "International Law", lawSubs);
            saveClass("Law - Year 1", "Public Law Freshman", facLaw, "Public Law", lawSubs);

            // 6. FACULTY OF TOURISM & HOSPITALITY
            String facTou = "Faculty of Tourism & Hospitality";
            saveClass("TSM - Year 2 - Hospitality", "Hospitality Class", facTou, "Hospitality Management", tourSubs);
            saveClass("Tourism - Year 1", "Tourism Fundamentals", facTou, "Tourism Development", tourSubs);

            // 7. FACULTY OF FOREIGN LANGUAGES
            String facLang = "Faculty of Foreign Languages";
            saveClass("English - Year 1", "English Literature Freshman", facLang, "English Literature", langSubs);
            saveClass("Korean - Year 1", "Korean Basics", facLang, "Korean", langSubs);

            System.out.println("Seeded Classes for all 7 Core Faculties.");
        }
    }

    private void saveClass(String name, String desc, String faculty, String dept, List<Subject> subjects) {
        if (!classRepository.existsByName(name)) {
            ClassEntity c = new ClassEntity(name, desc, faculty, dept);
            c.setSubjects(subjects);
            c.setSchedule("Monday - Friday");
            // Must save class first to get ID (though Cascade might handle it if we added
            // to list, but we are saving schedule separately)
            classRepository.save(c);

            // Seed Detailed Schedule (Mon, Tue, Wed, Thu, Fri)
            String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri" };
            for (int i = 0; i < days.length; i++) {
                if (i < subjects.size()) {
                    ClassSchedule cs = new ClassSchedule(days[i], subjects.get(i), c);
                    classScheduleRepository.save(cs);
                }
            }
            System.out.println("Created Class: " + name);
        }
    }

    private void seedStudents() {
        if (studentRepository.count() == 0) {
            List<ClassEntity> classes = classRepository.findAll();
            if (classes.isEmpty())
                return;

            // Seed Boys
            for (int i = 0; i < MALE_STUDENT_NAMES.length; i++) {
                Student s = new Student();
                String name = generateFullName("Male");
                s.setName(name);
                s.setEmail(name.toLowerCase().replace(" ", "") + (int) (Math.random() * 1000) + "@gmail.com");
                // Random class
                s.setClassEntity(classes.get((int) (Math.random() * classes.size())));
                s.setGender("Male");
                s.setPhoneNumber(String.format("012%06d", (int) (Math.random() * 1000000)));
                studentRepository.save(s);
            }

            // Seed Girls
            for (int i = 0; i < FEMALE_STUDENT_NAMES.length; i++) {
                Student s = new Student();
                String name = generateFullName("Female");
                s.setName(name);
                s.setEmail(name.toLowerCase().replace(" ", "") + (int) (Math.random() * 1000) + "@gmail.com");
                // Random class
                s.setClassEntity(classes.get((int) (Math.random() * classes.size())));
                s.setGender("Female");
                s.setPhoneNumber(String.format("012%06d", (int) (Math.random() * 1000000)));
                studentRepository.save(s);
            }
            System.out.println("Seeded Students with Cambodian Names");
        }
    }

    private void seedAttendance() {
        // Generate attendance for the CURRENT week (Mon-Fri) so the dashboard chart
        // (which shows this week) is populated
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);

        // For a clean demo, let's clear existing attendance and re-seed the current
        // week
        if (attendanceRepository.count() > 0) {
            attendanceRepository.deleteAll();
            System.out.println("Cleared existing attendance data to re-seed current week.");
        }

        List<Student> students = studentRepository.findAll();
        if (students.isEmpty())
            return;

        System.out.println("Seeding Attendance... (Generating data for the current week Mon-Fri)");

        // Loop Mon(0) to Fri(4)
        for (int i = 0; i < 5; i++) {
            LocalDate currentDate = monday.plusDays(i);

            for (Student student : students) {
                Attendance att = new Attendance();
                att.setStudent(student);
                att.setDate(currentDate);

                // 90% Present, 10% Absent
                String status = Math.random() > 0.1 ? "Present" : "Absent";
                att.setStatus(status);

                attendanceRepository.save(att);
            }
        }
        System.out.println("Seeded Attendance for the current week.");
    }

    // New method to fix existing data if it uses the old dummy names
    private void fixExistingNames() {
        // --- FIX CLASSES (Backfill new fields) ---
        List<ClassEntity> classes = classRepository.findAll();
        List<Subject> allSubjects = subjectRepository.findAll();

        for (ClassEntity c : classes) {
            String name = c.getName();

            // GLOBAL FACULTY NORMALIZATION
            if ("Faculty of IT".equals(c.getFaculty())) {
                c.setFaculty("Faculty of Information Technology");
                classRepository.save(c);
            }
            if ("Faculty of Fin & Acc".equals(c.getFaculty())) { // Example of other potential short forms
                c.setFaculty("Faculty of Finance & Accounting");
                classRepository.save(c);
            }

            // Only fix if IT legacy names (and not already fixed)
            if (c.getFaculty() != null && !c.getFaculty().isEmpty() && !"Faculty of IT".equals(c.getFaculty())) {
                // Already has valid faculty (or one we just fixed), so assume correct or
                // requires no action
            } else {
                // Legacy Fix Logic
                if (name.contains("Year 1 - IT") || name.equals("IT - Year 1 - Group A")) {
                    c.setName("IT - Year 1 - Group A");
                    c.setDepartment("IT");
                    c.setFaculty("Faculty of Information Technology");
                    classRepository.save(c);
                } else if (name.contains("CS") || name.contains("Year 2")) {
                    c.setName("BIT - Year 2");
                    c.setDepartment("BIT");
                    c.setFaculty("Faculty of Information Technology");
                    classRepository.save(c);
                } else if (name.contains("SE") || name.contains("Year 3")) {
                    c.setName("Robotic - Year 1");
                    c.setDepartment("Robotic");
                    c.setFaculty("Faculty of Information Technology");
                    classRepository.save(c);
                } else if (name.contains("MIS") || name.contains("Year 4")) {
                    c.setName("MIS - Year 4");
                    c.setDepartment("MIS");
                    c.setFaculty("Faculty of Information Technology");
                    classRepository.save(c);
                } else {
                    // Default fallback
                    if (c.getFaculty() == null) {
                        c.setFaculty("Faculty of Information Technology");
                        c.setDepartment("IT");
                        classRepository.save(c);
                    }
                }
            }

            // Always ensure subjects
            if ((c.getSubjects() == null || c.getSubjects().isEmpty()) && !allSubjects.isEmpty()) {
                c.setSubjects(allSubjects);
                classRepository.save(c);
            }
        }

        // Fix Teachers
        List<Teacher> teachers = teacherRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll(); // specific fix: fetch subjects

        for (int i = 0; i < teachers.size(); i++) {
            Teacher t = teachers.get(i);
            boolean updated = false;

            if (t.getName().startsWith("Teacher ") && Character.isDigit(t.getName().charAt(t.getName().length() - 1))) {
                if (i < TEACHER_NAMES.length) {
                    t.setName(TEACHER_NAMES[i]);
                    t.setEmail(TEACHER_NAMES[i].toLowerCase().replace(" ", "") + "@num.edu.kh");
                    updated = true;
                }
            }

            // Fix missing subject for ANY teacher
            if ((t.getSubjects() == null || t.getSubjects().isEmpty()) && !subjects.isEmpty()) {
                java.util.Set<Subject> teacherSubjects = new java.util.HashSet<>();
                teacherSubjects.add(subjects.get(i % subjects.size()));
                t.setSubjects(teacherSubjects);
                updated = true;
            }

            if (updated) {
                teacherRepository.save(t);
            }
        }

        // Fix Students & Redistribute to Departments
        List<Student> students = studentRepository.findAll();
        // Re-fetch classes as they might have been renamed above
        classes = classRepository.findAll();

        ClassEntity itClass = classes.stream().filter(c -> "IT".equals(c.getDepartment())).findFirst().orElse(null);
        ClassEntity bitClass = classes.stream().filter(c -> "BIT".equals(c.getDepartment())).findFirst().orElse(null);
        ClassEntity roboClass = classes.stream().filter(c -> "Robotic".equals(c.getDepartment())).findFirst()
                .orElse(null);

        int studentCounter = 0;

        for (Student s : students) {
            String name = s.getName();

            // SKIP redistribution for Pagination/Eco students
            if (name.startsWith("Eco Student") || name.startsWith("Pagination Student")) {
                continue;
            }

            // Assign phone number if missing
            if (s.getPhoneNumber() == null || s.getPhoneNumber().isEmpty()) {
                s.setPhoneNumber(String.format("012%06d", (int) (Math.random() * 1000000)));
            }

            // Check if name is single word (likely duplicate candidate) or placeholder
            // This logic forcibly updates ALL names to be "FamilyName GivenName" format if
            // they aren't already
            // preventing the "Serey", "Serey" duplicate issue.
            boolean needsRename = s.getName().split(" ").length < 2 ||
                    s.getName().startsWith("Boy") ||
                    s.getName().startsWith("Girl");

            if (needsRename) {
                // Assign/Re-assign gender if needed
                if (s.getGender() == null)
                    s.setGender(Math.random() > 0.5 ? "Male" : "Female");

                String newName = generateFullName(s.getGender());
                s.setName(newName);
                s.setEmail(newName.toLowerCase().replace(" ", "") + (int) (Math.random() * 1000) + "@gmail.com");
                studentRepository.save(s);
            }

            // --- REDISTRIBUTE TO DEPARTMENTS ---
            // Target: ~30 IT, ~11 BIT, ~10 Robotic. Total = 50.
            // Split: First 30 -> IT, Next 10 -> BIT, Rest -> Robotic

            if (itClass != null && studentCounter < 30) {
                s.setClassEntity(itClass);
            } else if (bitClass != null && studentCounter < 41) {
                s.setClassEntity(bitClass);
            } else if (roboClass != null) {
                s.setClassEntity(roboClass);
            }
            studentCounter++;

            // Ensure gender is set
            if (s.getGender() == null) {
                s.setGender(Math.random() > 0.5 ? "Male" : "Female");
            }
            studentRepository.save(s);

            // Ensure email format is correct (No dots in prefix, changes
            // @student.num.edu.kh to @gmail.com)
            String email = s.getEmail();
            if (email != null) {
                boolean changed = false;

                // Fix Domain
                if (email.endsWith("@student.num.edu.kh")) {
                    email = email.replace("@student.num.edu.kh", "@gmail.com");
                    changed = true;
                }

                // Remove dots from prefix (local part)
                int atIndex = email.indexOf("@");
                if (atIndex > 0) {
                    String prefix = email.substring(0, atIndex);
                    if (prefix.contains(".")) {
                        prefix = prefix.replace(".", "");
                        email = prefix + email.substring(atIndex);
                        changed = true;
                    }
                }

                if (changed) {
                    s.setEmail(email);
                    studentRepository.save(s);
                }
            }
        }
    }

    private void seedPaginationData() {
        String className = "Eco - Year 2";
        ClassEntity testClass = classRepository.findAll().stream()
                .filter(c -> c.getName().equals(className))
                .findFirst()
                .orElse(null);

        // "Eco - Year 2" should be created by seedClasses(), but safe check
        if (testClass != null) {
            final ClassEntity finalTestClass = testClass;

            // 1. GLOBAL FIX: Find ALL placeholder students in the SYSTEM, rename them, and
            // move them to Eco - Year 2
            List<Student> allStudents = studentRepository.findAll();
            for (Student s : allStudents) {
                if (s.getName().startsWith("Eco Student") || s.getName().startsWith("Pagination Student")) {
                    boolean isMale = Math.random() > 0.5;
                    s.setGender(isMale ? "Male" : "Female");

                    String name = generateFullName(s.getGender());
                    s.setName(name);
                    s.setEmail(name.toLowerCase().replace(" ", "") + (int) (Math.random() * 1000) + "@gmail.com");
                    s.setClassEntity(finalTestClass); // FORCE move back to Eco - Year 2
                    studentRepository.save(s);
                }
            }

            // 2. Add more if needed to reach exactly 11 in this class
            long currentCount = studentRepository.findAll().stream()
                    .filter(s -> s.getClassEntity() != null
                            && s.getClassEntity().getId().equals(finalTestClass.getId()))
                    .count();

            if (currentCount < 11) {
                for (int i = 1; i <= 11 - currentCount; i++) {
                    Student s = new Student();
                    boolean isMale = Math.random() > 0.5;
                    s.setGender(isMale ? "Male" : "Female");

                    String name = generateFullName(s.getGender());
                    s.setName(name);

                    // Generate unique-ish email to avoid conflicts if same name picked twice
                    String emailPrefix = name.toLowerCase().replace(" ", "") + (currentCount + i);
                    s.setEmail(emailPrefix + "@gmail.com");

                    s.setClassEntity(testClass);
                    s.setPhoneNumber("0990000" + (currentCount + i));
                    studentRepository.save(s);
                }
                System.out.println("Seeded remaining students for Eco - Year 2 with Cambodian names (Total 11)");
            } else {
                System.out.println("Eco - Year 2 has " + currentCount + " students.");
            }
        } else {
            System.out.println("Warning: Class '" + className + "' not found for pagination seeding.");
        }
    }

    private void ensureTotalStudentCount(int targetCount) {
        long currentCount = studentRepository.count();

        // CASE 1: Need more students
        if (currentCount < targetCount) {
            List<ClassEntity> classes = classRepository.findAll();
            if (classes.isEmpty())
                return;

            int needed = (int) (targetCount - currentCount);
            for (int i = 0; i < needed; i++) {
                Student s = new Student();
                boolean isMale = Math.random() > 0.5;
                s.setGender(isMale ? "Male" : "Female");
                String name = generateFullName(s.getGender());
                s.setName(name);
                s.setEmail(name.toLowerCase().replace(" ", "") + (int) (Math.random() * 10000) + "@gmail.com");

                // Assign to random class
                s.setClassEntity(classes.get((int) (Math.random() * classes.size())));

                s.setPhoneNumber(String.format("0%d", (int) (Math.random() * 100000000)));
                studentRepository.save(s);
            }
            System.out.println("Added " + needed + " extra students to reach total of " + targetCount);
        }

        // CASE 2: Too many students (Trim down to target)
        else if (currentCount > targetCount) {
            long excess = currentCount - targetCount;
            // Only trim if significantly over (e.g. > 10 extra) to avoid minor fluctuation
            // issues
            if (excess > 10) {
                // Fetch all students page by page or just find and delete
                // Logic: Delete the most recently created ones (highest IDs usually)
                // Or simple approach: Delete random

                // Since we don't have simple ID sorting exposed here easily without a Sort
                // object,
                // we will fetch all and delete the last N.
                List<Student> allStudents = studentRepository.findAll();

                // Skip the first 'targetCount' students, delete the rest
                // We want to keep the "Pagination Test" data if possible.

                List<Student> toDelete = allStudents.stream()
                        .filter(s -> !s.getName().contains("Pagination")) // Safe-guard special test data
                        .skip(targetCount)
                        .collect(Collectors.toList());

                studentRepository.deleteAllInBatch(toDelete);

                System.out.println("Trimmed " + toDelete.size() + " excess students to reach target.");
            }
        }
    }

    private void ensureAllClassesHaveStudents() {
        List<ClassEntity> classes = classRepository.findAll();
        for (ClassEntity cls : classes) {
            long count = studentRepository.countByClassEntityId(cls.getId());
            if (count < 5) { // Ensure at least 5 students per class
                int needed = 5 - (int) count;
                for (int i = 0; i < needed; i++) {
                    Student s = new Student();
                    boolean isMale = Math.random() > 0.5;
                    s.setGender(isMale ? "Male" : "Female");
                    String name = generateFullName(s.getGender());
                    s.setName(name);
                    s.setEmail(name.toLowerCase().replace(" ", "") + (int) (Math.random() * 10000) + "@gmail.com");
                    s.setClassEntity(cls);
                    s.setPhoneNumber(String.format("0%d", (int) (Math.random() * 100000000)));
                    studentRepository.save(s);
                }
                System.out.println("Added " + needed + " students to empty/low class: " + cls.getName());
            }
        }
    }
}
