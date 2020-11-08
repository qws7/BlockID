package kr.or.hanium.lego.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Holder {
    @Id @GeneratedValue
    private Long id;

    private String name;

    private String student_id;

    private String university;

    private String department;

    private String holder_did;

    @OneToMany
    @JoinColumn(name="holder_id")
    private List<Attendance> attendanceList;
}
