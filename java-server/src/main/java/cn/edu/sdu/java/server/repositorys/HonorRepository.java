package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HonorRepository extends JpaRepository<Honor,Integer>{
    //@Query("SELECT h FROM Honor h WHERE h.students.name = :studentId")
    //List<Honor> findByPersonPersonId(@Param("personId") Integer personId);

    @Query("SELECT p FROM Person p")
    List<Person> findAvailablePerson();

    Optional<Honor> findByHonorName(String honorName);
}