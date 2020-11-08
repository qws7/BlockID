package kr.or.hanium.lego.repository;

import kr.or.hanium.lego.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
