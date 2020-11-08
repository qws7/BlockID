package kr.or.hanium.lego.repository;

import kr.or.hanium.lego.domain.Holder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolderRepository extends JpaRepository<Holder, Long> {
}
