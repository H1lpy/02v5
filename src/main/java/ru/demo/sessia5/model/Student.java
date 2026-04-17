package ru.demo.sessia5.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "students")
public class Student implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int numberpp;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "faculty", length = 20)
    private String faculty;

    @Column(name = "specialty_number")
    private int specialtyNumber;

    public Student() {}

    public Student(String fullName, String faculty, int specialtyNumber) {
        this.fullName = fullName;
        this.faculty = faculty;
        this.specialtyNumber = specialtyNumber;
    }

    public String getId() { return String.valueOf(numberpp); }
    public void setId(int id) { this.numberpp = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public int getSpecialtyNumber() { return specialtyNumber; }
    public void setSpecialtyNumber(int specialtyNumber) { this.specialtyNumber = specialtyNumber; }
}