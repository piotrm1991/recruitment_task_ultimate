package it.piotrmachnik.recruitmentTaskUltimateSystems.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentModel extends RepresentationModel<StudentModel> {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
    private String course;
//    private List<TeacherModel> teachers;
    private Integer teachersCount;
}
