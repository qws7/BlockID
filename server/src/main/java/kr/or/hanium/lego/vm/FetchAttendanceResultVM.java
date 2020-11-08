package kr.or.hanium.lego.vm;


import kr.or.hanium.lego.domain.enumeration.AttendanceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FetchAttendanceResultVM {
    private String id;
    private String status;
    private LocalDateTime time;

    public FetchAttendanceResultVM(String id, String status, LocalDateTime time) {
        this.id = id;
        this.status = status;
        this.time = time;
    }
}
