package kr.or.hanium.lego.domain.enumeration;

public enum AttendanceStatus {
    PRESENT("출석"), 
    ABSENT("결석"),
    LATE("지각");

    private String krName;

    AttendanceStatus(String krName) {
        this.krName = krName;
    }

    public String getKrName() { return krName; }
}


