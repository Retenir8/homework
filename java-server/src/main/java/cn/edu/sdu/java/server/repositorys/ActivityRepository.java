package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    // 按活动开始时间排序查询
    List<Activity> findByOrderByStartTimeAsc();

    // 根据组织者查询活动
    List<Activity> findByOrganizer(Person organizer);

    List<Activity> findByNameContaining(String filter);
}
