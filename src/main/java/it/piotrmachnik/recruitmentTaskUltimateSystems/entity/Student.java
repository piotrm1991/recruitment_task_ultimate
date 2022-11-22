package it.piotrmachnik.recruitmentTaskUltimateSystems.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @NotBlank
    @Size(min = 3, max = 20, message = "First Name min=3, max=20")
    private String firstName;
    @NotBlank
    @Size(min = 3, max = 20, message = "Last Name min=3, max=20")
    private String lastName;
    @Min(value = 18, message = "Age min 18")
    private Integer age;
    @Email(message = "Incorrect Email")
    private String email;
    @Size(min = 3, max = 20, message = "Course min=3, max=20")
    private String course;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "teachers_students", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private List<Teacher> teachers = new ArrayList<>();

    public void addTeacher(Teacher teacher) {
        this.teachers.add(teacher);
        teacher.getStudents().add(this);
    }

    public void removeTeacher(Teacher teacher) {
        this.teachers.remove(teacher);
        teacher.getStudents().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return id != null && id.equals(((Student) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
