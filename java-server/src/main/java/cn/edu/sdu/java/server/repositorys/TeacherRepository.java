package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Teacher;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/*
 * Teacher 数据操作接口，实现 Person 数据的相关查询和操作
 * Optional<Teacher> findByPersonPersonId(Integer personId); 根据关联的 Person 的 personId 查询
 * Optional<Teacher> findByPersonNum(String num); 根据关联的 Person 的 num 查询
 * List<Teacher> findByPersonName(String name); 根据关联的 Person 的 name 查询，可能存在相同姓名的多个教师
 * List<Teacher> findTeacherListByNumName(String numName); 根据输入的学号或姓名模糊查询教师数据
 */

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Query("SELECT s FROM Teacher s WHERE s.teacherId = :teacherId")
    Teacher findByTeacherTeacherId(@Param("teacherId") Integer teacherId);

    Optional<Teacher> findByPersonPersonId(Integer personId);
    Teacher findByPersonNum(String num);
    List<Teacher> findByPersonName(String name);

    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
    List<Teacher> findTeacherListByNumName(String numName);


    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ",
            countQuery = "SELECT count(teacherId) from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
    Page<Teacher> findTeacherPageByNumName(String numName,  Pageable pageable);

    @Query("SELECT s FROM Teacher s JOIN FETCH s.person")
    List<Teacher> findAllWithPerson();


    @Query("SELECT s FROM Teacher s JOIN s.person p WHERE p.num = :teacherNum")
    Optional<Teacher> findByTeacherNum(@Param("teacherNum") String teacherNum);
    
}

