package it.piotrmachnik.recruitmentTaskUltimateSystems.controller;

import it.piotrmachnik.recruitmentTaskUltimateSystems.asembler.TeacherModelAsembler;
import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Student;
import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Teacher;
import it.piotrmachnik.recruitmentTaskUltimateSystems.exceptions.EntityAlreadyExistsException;
import it.piotrmachnik.recruitmentTaskUltimateSystems.exceptions.IncompleteDataException;
import it.piotrmachnik.recruitmentTaskUltimateSystems.model.TeacherModel;
import it.piotrmachnik.recruitmentTaskUltimateSystems.repository.StudentRepository;
import it.piotrmachnik.recruitmentTaskUltimateSystems.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private static final String REL_SELF = "self";

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherModelAsembler teacherModelAsembler;

    @Autowired
    private PagedResourcesAssembler<Teacher> pagedResourcesAssembler;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<PagedModel<TeacherModel>> getTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer studentId) {
        try {

            Page<Teacher> pageTeachers = null;
            if (studentId != null) {
                if (firstName != "" && firstName != null && lastName != null && lastName != "") {
                    pageTeachers = teacherRepository.findByFirstNameAndLastNameAndStudentsId(firstName, lastName, studentId, PageRequest.of(page, size));
                } else if (firstName != "" && firstName != null) {
                    pageTeachers = teacherRepository.findByFirstNameAndStudentsId(firstName, studentId, PageRequest.of(page, size));
                } else if (lastName != "" && lastName != null) {
                    pageTeachers = teacherRepository.findByLastNameAndStudentsId(lastName, studentId, PageRequest.of(page, size));
                } else {
                    pageTeachers = teacherRepository.findByStudentsId(studentId, PageRequest.of(page, size));
                }
            } else {
                if (firstName != null && lastName != null) {
                    pageTeachers = teacherRepository.findByFirstNameAndLastName(firstName, lastName, PageRequest.of(page, size));
                } else if (firstName != null) {
                    pageTeachers = teacherRepository.findByFirstName(firstName, PageRequest.of(page, size));
                } else if (lastName != null) {
                    pageTeachers = teacherRepository.findByLastName(lastName, PageRequest.of(page, size));
                } else {
                    pageTeachers = teacherRepository.findAll(PageRequest.of(page, size));
                }
            }

            PagedModel<TeacherModel> response = pagedResourcesAssembler.toModel(pageTeachers, teacherModelAsembler);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
//            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> addTeacher(@Valid @RequestBody Teacher teacher) {
        if (teacher.getId() != null) {
            Optional<Teacher> t = teacherRepository.findById(teacher.getId());
            if (t.isPresent()) {
                throw new EntityAlreadyExistsException("Teacher with Id: " + teacher.getId() + " already exists");
            }
        }
        Teacher addedTeacher = this.teacherRepository.save(teacher);
//        return ResponseEntity.created(URI.create(
//                        resource(this.teacherModelAsembler.toModel(addedTeacher)).getLink(REL_SELF).get().getHref()))
//                .build();
        return new ResponseEntity<>(teacherModelAsembler.toModel(addedTeacher), HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TeacherModel> getTeacher(@PathVariable("id") Integer id) {
        if (this.teacherRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Teacher By Id: " + id + " not found");
        }
        return Optional.of(this.teacherModelAsembler.toModel(this.teacherRepository.findById(id).get()))
//                .map(this::resource)
                .map(this::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/delete/{id}")
    public void deleteTeacher(@PathVariable Integer id) {
        Optional<Teacher> teacher = teacherRepository.findById(id);
        if (teacher.isEmpty()) {
            throw new EntityNotFoundException("Teacher with Id: " + id + " not found");
        }
        teacherRepository.deleteById(id);
    }

    @PutMapping("/{id}/addStudent")
    public ResponseEntity<?> addStudentToTeacher(@PathVariable("id") Integer id,
                                                 @Valid @RequestBody(required = false) Student student,
                                                 @RequestParam(required = false) Integer... studentId) {
        Optional<Teacher> teacher = teacherRepository.findById(id);
        if (teacher.isEmpty()) {
            throw new EntityNotFoundException("Teacher with Id: " + id + " not found");
        }
        Teacher updatedTeacher = teacher.get();
        if (studentId != null && studentId.length > 0) {
            for (Integer idS : studentId) {
                Optional<Student> s = studentRepository.findById(idS);
                if (s.isEmpty()) {
                    throw new EntityNotFoundException("Student with Id: " + idS + " not found");
                }
            }
            for (Integer idS : studentId) {
                Optional<Student> s = studentRepository.findById(idS);
                updatedTeacher.addStudent(s.get());
            }
        } else {
            if (student == null) {
                throw new IncompleteDataException("Student data not complete");
            }
            studentRepository.save(student);
            updatedTeacher.addStudent(student);
        }
        teacherRepository.save(updatedTeacher);

//        return ResponseEntity.created(URI.create(
//                        resource(updatedStudent).getLink(REL_SELF).get().getHref()))
//                .build();
        return new ResponseEntity<>(teacherModelAsembler.toModel(updatedTeacher), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/removeStudent")
    public ResponseEntity<?> removeStudentFromTeacher(@PathVariable("id") Integer id, @RequestParam Integer... studentId) {
        Optional<Teacher> teacher = teacherRepository.findById(id);
        if (teacher.isEmpty()) {
            throw new EntityNotFoundException("Teacher with Id: " + id + " not found");
        }
        Teacher updatedTeacher = teacher.get();
        if (studentId != null && studentId.length > 0) {
            for (Integer idS : studentId) {
                Optional<Student> s = studentRepository.findById(idS);
                if (s.isEmpty()) {
                    throw new EntityNotFoundException("Student with Id: " + idS + " not found");
                }
            }
            for (Integer idS : studentId) {
                Optional<Student> s = studentRepository.findById(idS);
                updatedTeacher.removeStudent(s.get());
            }
            teacherRepository.save(updatedTeacher);
        }
        return new ResponseEntity<>(teacherModelAsembler.toModel(updatedTeacher), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editTeacher(@PathVariable(name = "id") Integer id, @Valid @RequestBody Teacher teacher) {
        Optional<Teacher> t = teacherRepository.findById(id);
        if (t.isEmpty()) {
            throw new EntityNotFoundException("Teacher with Id: " + id + " not found");
        }
        Teacher editedTeacher = t.get();
        editedTeacher.setFirstName(teacher.getFirstName());
        editedTeacher.setLastName(teacher.getLastName());
        editedTeacher.setEmail(teacher.getEmail());
        editedTeacher.setAge(teacher.getAge());
        editedTeacher.setSubject(teacher.getSubject());
        teacherRepository.save(editedTeacher);

        return new ResponseEntity<>(teacherModelAsembler.toModel(editedTeacher), HttpStatus.CREATED);
    }

//    private EntityModel<TeacherModel> resource(TeacherModel teacher) {
//        EntityModel<TeacherModel> teacherResource = EntityModel.of(teacher);
//        teacherResource.add(linkTo(
//                methodOn(TeacherController.class)
//                        .getTeacher(teacher.getId()))
//                .withSelfRel());
//        return teacherResource;
//    }

    private <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok().body(body);
    }
}
