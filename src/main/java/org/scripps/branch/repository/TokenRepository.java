package org.scripps.branch.repository;

import org.scripps.branch.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TokenRepository extends JpaRepository<Token, Long> {
	public Token findById(long id);
	public Token findByUid(String uid);
}