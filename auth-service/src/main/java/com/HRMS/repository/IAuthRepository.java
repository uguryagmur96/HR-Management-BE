package com.HRMS.repository;

import com.HRMS.repository.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAuthRepository extends JpaRepository<Auth,Long> {


    Optional<Auth> findOptionalByEmail(String email);
    Optional<Auth> findOptionalByEmailAndPassword(String email, String password);
    Optional<Auth> findOptionalByCompanyEmailAndPassword(String companyEmail, String password);




}
