package kr.or.hanium.lego.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Verifier {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String verifier_did;

    private String end_point;
}
