package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Query(value = "from Activity where ?1='' or title like %?1%  ")
    List<Activity> findActivityListBytitle(String title);

    Optional<Activity> findByTitle(String title);

    @Query("SELECT a FROM Activity a JOIN FETCH a.students WHERE a.activity_id = :activityId")
    Optional<Activity> findActivityByIdWithStudents(@Param("activityId") int activityId);


}