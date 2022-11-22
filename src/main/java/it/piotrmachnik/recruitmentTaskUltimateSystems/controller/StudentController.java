package it.piotrmachnik.recruitmentTaskUltimateSystems.controller;

import it.piotrmachnik.recruitmentTaskUltimateSystems.asembler.StudentModelAsembler;
import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Student;
import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Teacher;
import it.piotrmachnik.recruitmentTaskUltimateSystems.exceptions.EntityAlreadyExistsException;
import it.piotrmachnik.recruitmentTaskUltimateSystems.exceptions.IncompleteDataException;
import it.piotrmachnik.recruitmentTaskUltimateSystems.model.StudentModel;
import it.piotrmachnik.recruitmentTaskUltimateSystems.repository.StudentRepository;
import it.piotrmachnik.recruitmentTaskUltimateSystems.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.Delimiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/students")
public class StudentController {

    private static final String REL_SELF = "self";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentModelAsembler studentModelAsembler;

    @Autowired
    private PagedResourcesAssembler<Student> pagedResourcesAssembler;

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping
    public ResponseEntity<PagedModel<StudentModel>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(defaultValue = "id,desc") String[] sort) {
        try {
            List<Order> orders = new ArrayList<Order>();

            if (sort[0].contains(",")) {
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order((_sort[1].equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC), _sort[0]));
                }
            } else {
                orders.add(new Order((sort[1].equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC), sort[0]));
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Student> pageStudents = null;
            if (teacherId != null) {
                if (firstName != null && firstName != "" && lastName != null && lastName != "") {
                    pageStudents = studentRepository.findByFirstNameAndLastNameAndTeachersId(firstName, lastName, teacherId, pagingSort);
                } else if (firstName != "" && firstName != null) {
                    pageStudents = studentRepository.findByFirstNameAndTeachersId(firstName, teacherId, pagingSort);
                } else if (lastName != "" && lastName != null) {
                    pageStudents = studentRepository.findByLastNameAndTeachersId(lastName, teacherId, pagingSort);
                } else {
                    pageStudents = studentRepository.findByTeachersId(teacherId, pagingSort);
                }
            } else {
                if (firstName != null && lastName != null) {
                    pageStudents = studentRepository.findByFirstNameAndLastName(firstName, lastName, pagingSort);
                } else if (firstName != null) {
                    pageStudents = studentRepository.findByFirstName(firstName, pagingSort);
                } else if (lastName != null) {
                    pageStudents = studentRepository.findByLastName(lastName, pagingSort);
                } else {
                    pageStudents = studentRepository.findAll(pagingSort);
                }
            }

            PagedModel<StudentModel> response = pagedResourcesAssembler.toModel(pageStudents, studentModelAsembler);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<StudentModel> getStudent(@PathVariable("id") Integer id) {
        if (this.studentRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Student By Id: " + id + " not found");
        }
        return Optional.of(this.studentModelAsembler.toModel(this.studentRepository.findById(id).get()))
//                .map(this::resource)
                .map(this::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/delete/{id}")
    public void deleteStudent(@PathVariable Integer id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            throw new EntityNotFoundException("Student with Id: " + id + " not found");
        }
        studentRepository.deleteById(id);
    }

    @PutMapping("/{id}/addTeacher")
    public ResponseEntity<?> addTeacherToStudent(@PathVariable("id") Integer id,
                                                 @Valid @RequestBody(required = false) Teacher teacher,
                                                 @RequestParam(required = false) Integer[] teacherId) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            throw new EntityNotFoundException("Student with Id: " + id + " not found");
        }
        Student updatedStudent = student.get();
        if (teacherId != null && teacherId.length > 0) {
            for (Integer idT : teacherId) {
                Optional<Teacher> teach = teacherRepository.findById(idT);
                if (teach.isEmpty()) {
                    throw new EntityNotFoundException("Teacher with Id: " + idT + " not found");
                }
            }
            for (Integer idT : teacherId) {
                Optional<Teacher> teach = teacherRepository.findById(idT);
                updatedStudent.addTeacher(teach.get());
            }
            studentRepository.save(updatedStudent);
        } else {
            if (teacher == null) {
                throw new IncompleteDataException("Teacher data not complete");
            }
            teacherRepository.save(teacher);
            updatedStudent.addTeacher(teacher);
            studentRepository.save(updatedStudent);
        }

//        return ResponseEntity.created(URI.create(
//                        resource(updatedStudent).getLink(REL_SELF).get().getHref()))
//                .build();
        return new ResponseEntity<>(studentModelAsembler.toModel(updatedStudent), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/removeTeacher")
    public ResponseEntity<?> removeTeacherFromStudent(@PathVariable("id") Integer id,
                                                      @RequestParam Integer[] teacherId) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            throw new EntityNotFoundException("Student with Id: " + id + " not found");
        }
        Student updatedStudent = student.get();
        if (teacherId != null && teacherId.length > 0) {
            for (Integer idT : teacherId) {
                Optional<Teacher> teach = teacherRepository.findById(idT);
                if (teach.isEmpty()) {
                    throw new EntityNotFoundException("Teacher with Id: " + idT + " not found");
                }
            }
            for (Integer idT : teacherId) {
                Optional<Teacher> teach = teacherRepository.findById(idT);
                updatedStudent.removeTeacher(teach.get());
            }
            studentRepository.save(updatedStudent);
        }
        return new ResponseEntity<>(studentModelAsembler.toModel(updatedStudent), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<?> addStudent(@Valid @RequestBody Student student) {
        if (student.getId() != null) {
            Optional<Student> s = studentRepository.findById(student.getId());
            if (s.isPresent()) {
                throw new EntityAlreadyExistsException("Student with Id: " + student.getId() + " already exists");
            }
        }
        Student addedStudent = this.studentRepository.save(student);
//        return ResponseEntity.created(URI.create(
//                        resource(addedStudent).getLink(REL_SELF).get().getHref()))
//                .build();
        return new ResponseEntity<>(studentModelAsembler.toModel(addedStudent), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editStudent(@PathVariable(name = "id") Integer id, @Valid @RequestBody Student student) {
        Optional<Student> s = studentRepository.findById(id);
        if (s.isEmpty()) {
            throw new EntityNotFoundException("Student with Id: " + id + " not found");
        }
        Student editedStudent = s.get();
        editedStudent.setFirstName(student.getFirstName());
        editedStudent.setLastName(student.getLastName());
        editedStudent.setEmail(student.getEmail());
        editedStudent.setAge(student.getAge());
        editedStudent.setCourse(student.getCourse());
        studentRepository.save(editedStudent);

        return new ResponseEntity<>(studentModelAsembler.toModel(editedStudent), HttpStatus.CREATED);
    }

//    private EntityModel<Student> resource(Student student) {
//        EntityModel<Student> studentResource = EntityModel.of(student);
//        studentResource.add(linkTo(
//                methodOn(StudentController.class)
//                        .getStudent(student.getId()))
//                .withSelfRel());
//        return studentResource;
//    }

    private <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok().body(body);
    }
}
