package kr.or.hanium.lego.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Data
public class Class {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String department;

    private String days;

    private LocalTime start_time;

    private LocalTime end_time;

}
