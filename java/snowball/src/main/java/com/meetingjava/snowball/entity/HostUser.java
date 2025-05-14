package com.smu.snowball.entity;

// JPA 엔티티로 지정 (DB 테이블과 매핑됨)
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// Lombok 어노테이션 (Getter, Setter, 기본 생성자 자동 생성)
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HostUser 엔티티 클래스
 * 모임장(HostUser)에 대한 정보를 관리하는 엔티티
 * - ID (PK)
 * - 이름
 * - 권한 레벨
 */
@Entity // JPA에서 이 클래스가 엔티티임을 명시
@Getter // Lombok: 모든 필드의 Getter 메소드 생성
@Setter // Lombok: 모든 필드의 Setter 메소드 생성
@NoArgsConstructor // Lombok: 기본 생성자 생성

public class HostUser {

    @Id // 엔티티의 Primary Key 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; // 모임장 고유 ID
    private String name; // 모임장 이름
    private String authorityLevel;  // 권한 수준 (예: '일반', '부회장' 등)
}
