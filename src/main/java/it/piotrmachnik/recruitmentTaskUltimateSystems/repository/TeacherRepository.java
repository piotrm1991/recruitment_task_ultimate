package it.piotrmachnik.recruitmentTaskUltimateSystems.repository;

import it.piotrmachnik.recruitmentTaskUltimateSystems.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Page<Teacher> findByFirstName(String firstName, Pageable pageable);
    Page<Teacher> findByLastName(String lastName, Pageable pageable);
    Page<Teacher> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);

    Page<Teacher> findByFirstNameAndLastNameAndStudentsId(String firstName, String lastName, Integer studentId, Pageable pageable);
    Page<Teacher> findByFirstNameAndStudentsId(String firstName, Integer studentId, Pageable pageable);
    Page<Teacher> findByLastNameAndStudentsId(String lastName, Integer studentId, Pageable pageable);
    Page<Teacher> findByStudentsId(Integer studentId, Pageable pageable);
}
