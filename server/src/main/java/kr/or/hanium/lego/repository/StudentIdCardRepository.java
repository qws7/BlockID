package kr.or.hanium.lego.repository;

import kr.or.hanium.lego.domain.StudentIdCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentIdCardRepository extends JpaRepository<StudentIdCard, Long> {
    StudentIdCard findByHolder_id(Long holder_id);
}
