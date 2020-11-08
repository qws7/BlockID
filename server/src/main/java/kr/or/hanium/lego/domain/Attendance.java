package kr.or.hanium.lego.domain;

import kr.or.hanium.lego.domain.enumeration.AttendanceStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Attendance {
    @Id
    @GeneratedValue
    private Long id;

    private Long holder_id;

    private Long class_id;

    private Long verifier_id;

    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    @ManyToOne
    @JoinColumn(name = "class_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Class class_info;

    @ManyToOne
    @JoinColumn(name = "verifier_id", updatable = false, insertable = false)
    private Verifier verifier;
}
