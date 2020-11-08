package kr.or.hanium.lego.vm;

import lombok.Data;

@Data
public class FetchClassesResultVM {
    private String name;
    private Long id;

    public FetchClassesResultVM(String name, Long id) {
        this.name= name;
        this.id = id;
    }
}
