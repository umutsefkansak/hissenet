package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.response.RiskOptionResponse;
import com.infina.hissenet.dto.response.RiskQuestionResponse;
import com.infina.hissenet.dto.response.RiskQuestionsResponse;
import com.infina.hissenet.entity.RiskOption;
import com.infina.hissenet.entity.RiskQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RiskAssessmentMapper {

    RiskQuestionResponse toResponse(RiskQuestion entity);

    @Mapping(target = "optionText", source = "optionText")
    RiskOptionResponse toResponse(RiskOption entity);

    List<RiskQuestionResponse> toResponse(List<RiskQuestion> entities);

    default RiskQuestionsResponse toQuestionsResponse(List<RiskQuestion> questions) {
        return new RiskQuestionsResponse(toResponse(questions));
    }
}