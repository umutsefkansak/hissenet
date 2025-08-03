package com.infina.hissenet.config;

import java.util.List;

public class RiskAssessmentConfig {
    public static final List<RiskQuestion> QUESTIONS = List.of(
            new RiskQuestion("AGE", "Yaş aralığınızı seçiniz:",
                    List.of(
                            new RiskOption("25 yaş altı", 4),
                            new RiskOption("25-35 arası", 3),
                            new RiskOption("35-50 arası", 2),
                            new RiskOption("50 yaş üstü", 1)
                    )
            ),

            new RiskQuestion("EDUCATION", "Eğitim durumunuzu seçiniz:",
                    List.of(
                            new RiskOption("İlköğretim", 1),
                            new RiskOption("Lise", 2),
                            new RiskOption("Üniversite", 3),
                            new RiskOption("Lisansüstü", 4)
                    )
            ),

            new RiskQuestion("INVESTMENT_PERIOD", "Ne kadar süreli yatırım yapmayı düşünüyorsunuz?",
                    List.of(
                            new RiskOption("Kısa vadeli (1 yıldan az)", 1),
                            new RiskOption("Orta vadeli (1-3 yıl)", 2),
                            new RiskOption("Uzun vadeli (3-10 yıl)", 3),
                            new RiskOption("Çok uzun vadeli (10+ yıl)", 4)
                    )
            ),

            new RiskQuestion("RISK_APPETITE", "Yatırım yaparken hangisi sizi daha iyi tanımlar?",
                    List.of(
                            new RiskOption("Güvenli, düşük getirili yatırımları tercih ederim", 1),
                            new RiskOption("Orta düzeyde risk alabilirim", 2),
                            new RiskOption("Yüksek getiri için risk alabilirim", 3),
                            new RiskOption("Çok yüksek risk alarak maksimum getiri isterim", 4)
                    )
            ),

            new RiskQuestion("FINANCIAL_KNOWLEDGE", "Finansal bilginiz ne durumda?",
                    List.of(
                            new RiskOption("Yeni başlıyorum", 1),
                            new RiskOption("Temel bilgilerim var", 2),
                            new RiskOption("Orta düzeyde bilgiliyim", 3),
                            new RiskOption("İleri düzeyde bilgiliyim", 4)
                    )
            )
    );

    public record RiskQuestion(
            String code,
            String questionText,
            List<RiskOption> options
    ) {}

    public record RiskOption(
            String optionText,
            Integer score
    ) {}
}
