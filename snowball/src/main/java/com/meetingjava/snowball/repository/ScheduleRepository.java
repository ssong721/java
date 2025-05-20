// ScheduleRepository.java
package com.meetingjava.snowball.repository;

import com.meetingjava.snowball.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 필요 시 커스텀 쿼리 추가 가능
}
