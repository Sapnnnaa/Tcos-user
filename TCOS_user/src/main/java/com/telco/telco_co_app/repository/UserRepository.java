package com.telco.telco_co_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telco.telco_co_app.model.AccountStatus;
import com.telco.telco_co_app.model.User;


public interface UserRepository extends JpaRepository<User, Integer> {
	
	List<User> findByEmail(String email);
	User findByMemberId(String memberId);
	User existsByEmail(String email);
	User findByUserId(String userId);
	@Query(value = "SELECT u.user_id FROM users u ORDER BY u.id DESC LIMIT 1", nativeQuery = true)
    String findLastUserId();
	List<User> findByAadharNumber(String aadharNum);
	List<User> findByPanCard(String panNum);
//	User save(User user);
	@Query(value = "select * from users u where u.user_id = :userId", nativeQuery = true)
    Optional<User> findByUserIDs(@Param("userId") String userId);
	@Query(value="SELECT IF(EXISTS (SELECT 1 FROM users WHERE user_id = :userId), 1, 0)",nativeQuery = true)
	int checkUserIdExist(String userId);
	User findByUserIdAndAccountStatus(String userId,AccountStatus accountStatus);

}
