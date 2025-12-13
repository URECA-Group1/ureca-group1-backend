package urecagroup1backend.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import urecagroup1backend.member.domain.Member;

import java.util.Optional;

/*
@file MemberRepository.java
@author 신형서
@since 2025-11-28
@description 회원 레포지토리
*/

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialId(String socialId);
}
