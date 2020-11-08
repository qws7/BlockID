package kr.or.hanium.lego.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Issuer {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String issuer_did;
}
