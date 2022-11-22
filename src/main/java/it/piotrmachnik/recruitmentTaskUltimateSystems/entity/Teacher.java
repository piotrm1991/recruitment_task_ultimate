package it.piotrmachnik.recruitmentTaskUltimateSystems.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@Data
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Size(min = 3, max = 20, message = "First Name min=3, max=20")
    private String firstName;
    @Size(min = 3, max = 20, message = "Last Name min=3, max=20")
    private String lastName;
    @Min(value = 18, message = "Age min 18")
    private Integer age;
    @Email(message = "Incorrect Email")
    private String email;
    @Size(min = 3, max = 20, message = "Subject min=3, max=20")
    private String subject;
    @ManyToMany(mappedBy = "teachers")
    private List<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        this.students.add(student);
        student.getTeachers().add(this);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
        student.getTeachers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher)) return false;
        return id != null && id.equals(((Teacher) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
