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

    @Column(name = "faculty", length = 20)
    private String faculty;

    @Column(name = "specialty_number")
    private int specialtyNumber;

    @Column(name = "last_name", length = 20)
    private String lastName;

    @Column(name = "first_name", length = 20)
    private String firstName;

    @Column(name = "middle_name", length = 20)
    private String middleName;

    public Student() {}

    public Student(String faculty, int specialtyNumber, String lastName, String firstName, String middleName) {
        this.faculty = faculty;
        this.specialtyNumber = specialtyNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
    }

    public String getId() { return String.valueOf(numberpp); }
    public void setId(int id) { this.numberpp = id; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public int getSpecialtyNumber() { return specialtyNumber; }
    public void setSpecialtyNumber(int specialtyNumber) { this.specialtyNumber = specialtyNumber; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(lastName).append(" ").append(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        return fullName.toString();
    }
}