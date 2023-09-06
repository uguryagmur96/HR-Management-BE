package com.HRMS.repository;

import com.HRMS.repository.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAuthRepository extends JpaRepository<Auth,Long> {

<<<<<<< HEAD:auth-service/src/main/java/com/HRMS/repository/IAuthRepository.java
    Optional<Auth> findOptionalByEmail(String email);
=======
    Optional<Auth> findOptionalByUsername(String username);
    Optional<Auth> findOptionalByUsernameAndPassword(String username,String password);
>>>>>>> master:auth-service/src/main/java/com/hrms/repository/IAuthRepository.java

}