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

    public long getAdminCount() {
        return userRepository.count();
    }

    public long getTeacherCount() {
        return teacherRepository.count();
    }

    public long getStudentCount() {
        return studentRepository.count();
    }

    public long getClassCount() {
        return classRepository.count();
    }
}
