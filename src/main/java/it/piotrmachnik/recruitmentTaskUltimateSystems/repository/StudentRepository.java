package it.piotrmachnik.recruitmentTaskUltimateSystems.repository;

import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Page<Student> findByFirstName(String firstName, Pageable pageable);
    Page<Student> findByLastName(String lastName, Pageable pageable);
    Page<Student> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);

    Page<Student> findByTeachersId(Integer teacherId, Pageable pageable);
    Page<Student> findByFirstNameAndLastNameAndTeachersId(String firstName, String lastName, Integer teacherId, Pageable pageable);
    Page<Student> findByFirstNameAndTeachersId(String firstName, Integer teacherId, Pageable pageable);
    Page<Student> findByLastNameAndTeachersId(String lastName, Integer teacherId, Pageable pageable);
}
