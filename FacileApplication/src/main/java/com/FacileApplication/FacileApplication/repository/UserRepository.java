package com.FacileApplication.FacileApplication.repository;

import com.FacileApplication.FacileApplication.model.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UserRequest entity.
 *
 * JpaRepository provides built-in methods like:
 *   - save()      -> INSERT or UPDATE
 *   - findById()  -> SELECT by primary key
 *   - findAll()   -> SELECT all
 *   - deleteById() -> DELETE by primary key
 *
 * We add one custom method to check for duplicate mobile numbers.
 */
@Repository
public interface UserRepository extends JpaRepository<UserRequest, Long> {

    /**
     * Spring Data JPA auto-generates the query:
     *   SELECT COUNT(*) > 0 FROM user_requests WHERE mobile_number = ?
     *
     * Returns true if the mobile number already exists in the database.
     */
    boolean existsByMobileNumber(String mobileNumber);
}