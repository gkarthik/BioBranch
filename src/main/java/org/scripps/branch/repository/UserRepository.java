package org.scripps.branch.repository;

import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findByEmail(String email);
	
	public User findById(long id);

}