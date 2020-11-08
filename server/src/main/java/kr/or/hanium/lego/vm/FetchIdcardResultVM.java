package kr.or.hanium.lego.vm;


import kr.or.hanium.lego.domain.enumeration.CardStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FetchIdcardResultVM {
    private String studentId;
    private CardStatus status;
    private String university;
    private LocalDateTime expireDate;
    private String department;
    private Long holder_id;
    private String name;
    private Boolean isInBlockchainLedger;
}
