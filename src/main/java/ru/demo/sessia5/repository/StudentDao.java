package ru.demo.sessia5.repository;

import ru.demo.sessia5.model.Student;

public class StudentDao extends BaseDao<Student> {
    public StudentDao(){ super(Student.class);}
}
