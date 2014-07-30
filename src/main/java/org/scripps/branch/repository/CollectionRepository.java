package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CollectionRepository extends JpaRepository<Collection, Long> {

	@Query("select C from Collection C where C.id =?1")
	Collection findById(long i);

	@Query("select C from Collection C where C.user =?1")
	List<Collection> findByUser(User user);

	@Query("select C from Collection C, Dataset D  where C.user =?1 and C.id=D.collection and D.privateset='false'")
	List<Collection> getOnlyPublicCollections(User user);

	@Query("select D,C,U from Collection C, Dataset D, User U "
			+ "where D.collection = C.id and D.privateset='false'"
			+ "and C.user=U.id" + " order by C.name")
	List<Collection> getPublicCollection();
}
