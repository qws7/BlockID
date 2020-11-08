package kr.or.hanium.lego.vm;

import lombok.Data;

@Data
public class BlockAttendanceVM {
    private String attendance_id;
    private String class_id;
    private String holder_id;
    private String status;
    private String time;
    private String verifier_id;
}
