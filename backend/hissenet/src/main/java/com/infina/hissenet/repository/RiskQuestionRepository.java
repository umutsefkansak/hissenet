package com.infina.hissenet.repository;

import com.infina.hissenet.entity.RiskQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RiskQuestionRepository extends JpaRepository<RiskQuestion, Long> {

    List<RiskQuestion> findAllByOrderByOrderIndexAsc();
}