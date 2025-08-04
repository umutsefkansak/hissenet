package com.infina.hissenet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infina.hissenet.entity.base.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "risk_options")
@SQLRestriction("is_deleted = false")
public class RiskOption extends BaseEntity {

    @Column(nullable = false)
    private String optionText;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private RiskQuestion question;

    public RiskOption() {}

    public RiskOption(String optionText, Integer score, Integer orderIndex) {
        this.optionText = optionText;
        this.score = score;
        this.orderIndex = orderIndex;
    }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public RiskQuestion getQuestion() { return question; }
    public void setQuestion(RiskQuestion question) { this.question = question; }
}