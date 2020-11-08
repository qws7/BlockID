package kr.or.hanium.lego.service.dto;

import kr.or.hanium.lego.domain.enumeration.CardStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StduentIdCardDto {
    private LocalDateTime expireDate;
    private Long holder_id;
    private CardStatus status;
}
