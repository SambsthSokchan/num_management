package com.num.management.service;

import com.num.management.repository.StudentRepository;
import com.num.management.repository.TeacherRepository;
import com.num.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private com.num.management.repository.ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Count total admins/users
    public long getAdminCount() {
        return userRepository.countByRoles_Name("ROLE_ADMIN");
    }

    // Count total teachers
    public long getTeacherCount() {
        return teacherRepository.count();
    }

    // Count total students
    public long getStudentCount() {
        return studentRepository.count();
    }

    // Count total classes
    public long getClassCount() {
        return classRepository.count();
    }
}
