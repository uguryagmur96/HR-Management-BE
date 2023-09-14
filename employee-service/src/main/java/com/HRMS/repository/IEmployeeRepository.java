package com.HRMS.repository;

import com.HRMS.repository.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEmployeeRepository extends MongoRepository<Employee,String> {
    Optional<Employee> findOptionalByEmail(String email);
    Optional<List<Employee>> findOptionalByCompanyName(String companyName);
    Optional<Employee> findByCompanyIdAndDepartment(String companyId, String department);
    Optional<Employee> findById(String id);

}
