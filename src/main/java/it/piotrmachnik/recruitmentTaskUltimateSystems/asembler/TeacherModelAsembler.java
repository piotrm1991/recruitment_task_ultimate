package it.piotrmachnik.recruitmentTaskUltimateSystems.asembler;

import it.piotrmachnik.recruitmentTaskUltimateSystems.controller.StudentController;
import it.piotrmachnik.recruitmentTaskUltimateSystems.controller.TeacherController;
import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Teacher;
import it.piotrmachnik.recruitmentTaskUltimateSystems.model.TeacherModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TeacherModelAsembler extends RepresentationModelAssemblerSupport<Teacher, TeacherModel> {

    public TeacherModelAsembler() {
        super(TeacherController.class, TeacherModel.class);
    }

    @Override
    public TeacherModel toModel(Teacher entity) {
        TeacherModel teacherModel = instantiateModel(entity);
        teacherModel.add(linkTo(methodOn(TeacherController.class).getTeacher(entity.getId())).withSelfRel());
        teacherModel.add(linkTo(methodOn(StudentController.class).getStudents(0, 3, "", "", entity.getId())).withRel("students"));

        teacherModel.setId(entity.getId());
        teacherModel.setAge(entity.getAge());
        teacherModel.setEmail(entity.getEmail());
        teacherModel.setSubject(entity.getSubject());
        teacherModel.setFirstName(entity.getFirstName());
        teacherModel.setLastName(entity.getLastName());
        teacherModel.setStudentsCount((entity.getStudents() != null) ? entity.getStudents().size() : 0);

        return teacherModel;
    }
}
