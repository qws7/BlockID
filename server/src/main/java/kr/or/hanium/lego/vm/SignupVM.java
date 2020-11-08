package kr.or.hanium.lego.vm;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SignupVM {
    private String name;
    private String student_id;
    private String university;
    private String department;
}
