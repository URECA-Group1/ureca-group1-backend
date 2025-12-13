package urecagroup1backend.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import urecagroup1backend.auth.domain.Token;

/*
@file TokenRepository.java
@author 신형서
@since 2025-12-11
@description 액세스 토큰, 리프레시 토큰 리포지토리
*/
@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
}
