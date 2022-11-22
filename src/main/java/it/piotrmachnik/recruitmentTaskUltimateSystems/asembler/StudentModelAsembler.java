package it.piotrmachnik.recruitmentTaskUltimateSystems.asembler;

import it.piotrmachnik.recruitmentTaskUltimateSystems.controller.StudentController;
import it.piotrmachnik.recruitmentTaskUltimateSystems.controller.TeacherController;
import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Student;
import it.piotrmachnik.recruitmentTaskUltimateSystems.model.StudentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StudentModelAsembler extends RepresentationModelAssemblerSupport<Student, StudentModel> {

    @Autowired
    private TeacherModelAsembler teacherModelAsembler;

    public StudentModelAsembler() {
        super(StudentController.class, StudentModel.class);
    }

    @Override
    public StudentModel toModel(Student entity) {
        StudentModel studentModel = instantiateModel(entity);
        studentModel.add(linkTo(methodOn(StudentController.class).getStudent(entity.getId())).withSelfRel());
        studentModel.add(linkTo(methodOn(TeacherController.class).getTeachers(0, 3, "", "", entity.getId())).withRel("teachers"));

        studentModel.setId(entity.getId());
        studentModel.setAge(entity.getAge());
        studentModel.setEmail(entity.getEmail());
        studentModel.setCourse(entity.getCourse());
        studentModel.setFirstName(entity.getFirstName());
        studentModel.setLastName(entity.getLastName());
        studentModel.setTeachersCount((entity.getTeachers() != null) ? entity.getTeachers().size() : 0);

        return studentModel;
    }
}
