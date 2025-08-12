package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(
        name = "risk_questions",
        indexes = {
                @Index(name = "idx_order_index", columnList = "orderIndex")
        }
)
@SQLRestriction("is_deleted = false")
public class RiskQuestion extends BaseEntity {

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false, unique = true)
    private Integer orderIndex;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<RiskOption> options;

    public RiskQuestion() {}

    public RiskQuestion(String questionText, Integer orderIndex) {
        this.questionText = questionText;
        this.orderIndex = orderIndex;
    }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public List<RiskOption> getOptions() { return options; }
    public void setOptions(List<RiskOption> options) { this.options = options; }
}