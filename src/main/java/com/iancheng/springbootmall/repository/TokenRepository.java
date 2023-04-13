package com.iancheng.springbootmall.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iancheng.springbootmall.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer>{
	
	@Query(value = """
			       SELECT * \s
			       FROM `token` t \s
			       INNER JOIN `user` u ON t.FK_user_id = u.user_id \s
			       WHERE u.user_id = :id AND (t.expired = false OR t.revoked = false) \s
			       """, nativeQuery = true)
	List<Token> findAllValidTokenByUser(Integer id);

	Optional<Token> findByToken(String token);
}
