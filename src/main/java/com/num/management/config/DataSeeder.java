package com.num.management.config;

import com.num.management.dto.UserRegistrationDto;
import com.num.management.model.Attendance;
import com.num.management.model.ClassEntity;
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

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedSubjects(); // Seed subjects first
        seedTeachers();
        seedClasses();
        seedStudents();
        seedAttendance();
        fixExistingNames();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            UserRegistrationDto admin = new UserRegistrationDto();
            admin.setFirstName("Seang");
            admin.setLastName("Hai");
            admin.setEmail("admin@num.edu.kh");
            admin.setPassword("password");
            userService.save(admin);
            System.out.println("Seeded Admin: Seang Hai (admin@num.edu.kh / password)");
        }
    }

    private void seedSubjects() {
        if (subjectRepository.count() == 0) {
            subjectRepository.save(new Subject("Advanced Web Development", "WEB-001", "Spring Boot & React"));
            subjectRepository.save(new Subject("Data Structures", "CS-102", "Algorithms & Structures"));
            subjectRepository.save(new Subject("Database Systems", "DB-201", "SQL & NoSQL"));
            subjectRepository.save(new Subject("Network Security", "NET-301", "Cybersecurity Basics"));
            subjectRepository.save(new Subject("Mobile App Dev", "MOB-401", "Flutter & Swift"));
            System.out.println("Seeded Subjects");
        }
    }

    private final String[] TEACHER_NAMES = {
            "Sokha", "Vannak", "Bopha", "Chanthou", "Rithy",
            "Sophea", "Vanna", "Dara", "Sothea", "Chenda"
    };

    private final String[] MALE_STUDENT_NAMES = {
            "Dara", "Vuthy", "Sambo", "Visal", "Oudom", "Rith", "Sovann", "Pheakdei", "Chan", "Khemara",
            "Virak", "Sambath", "Borey", "Narin", "Panha", "Piseth", "Seyha", "Tola", "Veasna", "Vibol",
            "Vichea", "Vidya", "Vireak"
    };

    private final String[] FEMALE_STUDENT_NAMES = {
            "Bopha", "Thida", "Neary", "Chantrea", "Devi", "Sreyroth", "Kunthea", "Dalin", "Sreypov", "Kannitha",
            "Kolab", "Leakena", "Malis", "Phirun", "Pich", "Rachana", "Rathana", "Ravy", "Rom Chang", "Samnang",
            "Serey", "Sokea", "Sopheary", "Soriya", "Sotheary", "Sreymom", "Sreynich"
    };

    private void seedTeachers() {
        if (teacherRepository.count() == 0) {
            List<Subject> subjects = subjectRepository.findAll();
            if (subjects.isEmpty())
                return;

            for (int i = 0; i < TEACHER_NAMES.length; i++) {
                Teacher t = new Teacher();
                t.setName(TEACHER_NAMES[i]);
                t.setEmail(TEACHER_NAMES[i].toLowerCase().replace(" ", "") + "@num.edu.kh");
                t.setSubjectEntity(subjects.get(i % subjects.size()));
                t.setPhoneNumber("01234567" + i);
                teacherRepository.save(t);
            }
            System.out.println("Seeded 10 Teachers with Cambodian Names");
        }
    }

    private void seedClasses() {
        if (classRepository.count() == 0) {
            classRepository.save(new ClassEntity("Year 1 - IT", "Freshman IT Class"));
            classRepository.save(new ClassEntity("Year 2 - CS", "Sophomore CS Class"));
            classRepository.save(new ClassEntity("Year 3 - IT", "Junior IT Class"));
            classRepository.save(new ClassEntity("Year 4 - SE", "Senior Software Engineering Class"));
            System.out.println("Seeded 4 Classes");
        }
    }

    private void seedStudents() {
        if (studentRepository.count() == 0) {
            List<ClassEntity> classes = classRepository.findAll();
            if (classes.isEmpty())
                return;

            ClassEntity year3 = classes.stream()
                    .filter(c -> c.getName().contains("Year 3"))
                    .findFirst()
                    .orElse(classes.get(0));

            // Seed Boys
            for (int i = 0; i < MALE_STUDENT_NAMES.length; i++) {
                Student s = new Student();
                s.setName(MALE_STUDENT_NAMES[i]);
                s.setEmail(MALE_STUDENT_NAMES[i].toLowerCase().replace(" ", "") + "@student.num.edu.kh");
                s.setClassEntity(year3);
                s.setGender("Male");
                s.setPhoneNumber(String.format("012%06d", (int) (Math.random() * 1000000)));
                studentRepository.save(s);
            }

            // Seed Girls
            for (int i = 0; i < FEMALE_STUDENT_NAMES.length; i++) {
                Student s = new Student();
                s.setName(FEMALE_STUDENT_NAMES[i]);
                s.setEmail(FEMALE_STUDENT_NAMES[i].toLowerCase().replace(" ", "") + "@student.num.edu.kh");
                s.setClassEntity(year3);
                s.setGender("Female");
                s.setPhoneNumber(String.format("012%06d", (int) (Math.random() * 1000000)));
                studentRepository.save(s);
            }
            System.out.println("Seeded Students with Cambodian Names");
        }
    }

    private void seedAttendance() {
        // Find current week's Monday
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);

        // Check if we already have data for this week
        List<Attendance> allAttendance = attendanceRepository.findAll();
        boolean hasThisWeekData = allAttendance.stream()
                .anyMatch(att -> !att.getDate().isBefore(monday));

        if (!hasThisWeekData) {
            List<Student> students = studentRepository.findAll();
            if (students.isEmpty())
                return;

            System.out.println("Seeding Attendance... (No data found for current week starting " + monday + ")");

            for (int day = 0; day < 5; day++) {
                LocalDate currentDate = monday.plusDays(day);
                for (Student student : students) {
                    Attendance att = new Attendance();
                    att.setStudent(student);
                    att.setDate(currentDate);
                    att.setStatus(Math.random() > 0.1 ? "Present" : "Absent");
                    attendanceRepository.save(att);
                }
            }
            System.out.println("Seeded Attendance for the current week.");
        } else {
            System.out.println("Attendance data for current week already exists. Skipping seed.");
        }
    }

    // New method to fix existing data if it uses the old dummy names
    private void fixExistingNames() {
        // Fix Teachers
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
                    // t.setSubjectEntity(subjects.get(i % subjects.size())); // Logic moved below
                    // to be general
                    updated = true;
                }
            }

            // Fix missing subject for ANY teacher (including migrated ones)
            if (t.getSubjectEntity() == null && !subjects.isEmpty()) {
                t.setSubjectEntity(subjects.get(i % subjects.size()));
                updated = true;
            }

            if (updated) {
                teacherRepository.save(t);
            }
        }

        // Fix Students
        List<Student> students = studentRepository.findAll();
        List<ClassEntity> classes = classRepository.findAll(); // fetch classes
        ClassEntity defaultClass = classes.stream()
                .filter(c -> c.getName().contains("Year 3"))
                .findFirst()
                .orElse(classes.isEmpty() ? null : classes.get(0));

        int boyIndex = 0;
        int girlIndex = 0;

        for (Student s : students) {
            String name = s.getName();
            // Assign phone number if missing
            if (s.getPhoneNumber() == null || s.getPhoneNumber().isEmpty()) {
                s.setPhoneNumber(String.format("012%06d", (int) (Math.random() * 1000000)));
            }

            // Assign class if missing
            if (s.getClassEntity() == null && defaultClass != null) {
                s.setClassEntity(defaultClass);
            }

            if (name.startsWith("Boy Student")) {
                if (boyIndex < MALE_STUDENT_NAMES.length) {
                    s.setName(MALE_STUDENT_NAMES[boyIndex]);
                    s.setEmail(MALE_STUDENT_NAMES[boyIndex].toLowerCase().replace(" ", "") + "@student.num.edu.kh");
                    studentRepository.save(s);
                    boyIndex++;
                }
            } else if (name.startsWith("Girl Student")) {
                if (girlIndex < FEMALE_STUDENT_NAMES.length) {
                    s.setName(FEMALE_STUDENT_NAMES[girlIndex]);
                    s.setEmail(FEMALE_STUDENT_NAMES[girlIndex].toLowerCase().replace(" ", "") + "@student.num.edu.kh");
                    studentRepository.save(s);
                    girlIndex++;
                }
            } else {
                // Even if name is correct, save the phone number/class update
                studentRepository.save(s);
            }
        }
        System.out.println("Fixed existing dummy names to Cambodian names.");
    }
}
