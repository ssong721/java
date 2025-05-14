package com.smu.snowball.repository;

// HostUser 엔티티 클래스를 위한 JPA Repository 인터페이스
// DB 접근 (CRUD) 기능을 Spring Data JPA가 자동으로 제공

import com.smu.snowball.entity.HostUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// HostUser에 대한 DB 접근을 담당하는 Repository 인터페이스
/**
 * HostUserRepository
 * - HostUser 엔티티에 대한 DB 접근 인터페이스
 * - JpaRepository<HostUser, Long> 상속을 통해
 *   기본 CRUD (Create, Read, Update, Delete) 메소드 제공
 * - @Repository 어노테이션을 통해 Spring Bean으로 등록
 */
@Repository
public interface HostUserRepository extends JpaRepository<HostUser, Long> {
    // 기본적인 CRUD 메소드 외에 커스텀 메소드 정의 가능 (예: findByName(String name))
}
