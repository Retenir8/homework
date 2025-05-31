package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Practice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PracticeRepository extends JpaRepository<Practice, Integer> {

    void deletePracticeById(Integer id);

}
