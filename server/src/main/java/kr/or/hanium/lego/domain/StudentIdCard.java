package kr.or.hanium.lego.domain;

import kr.or.hanium.lego.domain.enumeration.CardStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_id_card")
@Data
public class StudentIdCard{
    @Id
    @GeneratedValue
    private Long id;

    private String card_did;

    private LocalDateTime verified_date;

    private LocalDateTime expire_date;

    private Long holder_id;

    private Long issuer_id;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @OneToOne
    @JoinColumn(name = "holder_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Holder holder;

    @ManyToOne
    @JoinColumn(name = "issuer_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Issuer issuer;
}
