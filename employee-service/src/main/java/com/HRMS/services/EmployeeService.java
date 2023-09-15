package com.HRMS.services;

import com.HRMS.dto.request.*;
import com.HRMS.dto.request.UpdateEmployeeRequestDto;
import com.HRMS.dto.response.ListPermissionsResponseDto;
import com.HRMS.dto.response.ViewAllEmployeeInfoResponseDto;
import com.HRMS.exceptions.EmployeeException;
import com.HRMS.exceptions.ErrorType;
import com.HRMS.mapper.IEmployeeMapper;
import com.HRMS.rabbitmq.model.CreateEmployee;
import com.HRMS.rabbitmq.model.SendActivationEmail;
import com.HRMS.rabbitmq.producer.EmployeeProducer;
import com.HRMS.rabbitmq.producer.SendActivationEmailProducer;
import com.HRMS.repository.IEmployeeRepository;
import com.HRMS.repository.entity.Employee;

import com.HRMS.utils.RandomPasswordGenerator;
import com.HRMS.utils.ServiceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.HRMS.utils.RandomPasswordGenerator.*;

@Service
public class EmployeeService extends ServiceManager<Employee,String> {

    private final IEmployeeRepository repository;
    private final EmployeeProducer employeeProducer;
    private final SendActivationEmailProducer emailProducer;


    public EmployeeService(IEmployeeRepository repository, EmployeeProducer employeeProducer, SendActivationEmailProducer emailProducer) {
        super(repository);
        this.repository = repository;
        this.employeeProducer = employeeProducer;
        this.emailProducer = emailProducer;
    }


    public Boolean addEmployee(AddEmployeeRequestDto dto){

       Optional<Employee> empOpt= repository.findOptionalByEmail(dto.getEmail());
        if (empOpt.isPresent()){
            throw new EmployeeException(ErrorType.EMPLOYEE_ALREADY_EXIST);
        } else {
            Employee emp= IEmployeeMapper.INSTANCE.toEmployeeFromDto(dto);
            String mailGen= dto.getName().toLowerCase().charAt(0)+dto.getSurname().toLowerCase().trim()+"@"+dto.getCompanyName().toLowerCase().trim()+".com";
            emp.setCompanyEmail(mailGen);
            save(emp);

            String pass= generateRandomPassword();
            System.out.println("Auth a gider"+ mailGen);
            System.out.println("Maile a gider"+ pass);
            employeeProducer.createEmployeeAtAuth(CreateEmployee.builder()
                    .email(dto.getEmail())
                    .companyEmail(mailGen)
                    .password(pass)
                    .employeeId(emp.getId())
                    .build());

            emailProducer.sendMailActivationMessage(SendActivationEmail.builder()
                    .email(dto.getEmail())
                    .companyMail(mailGen)
                    .password(pass)
                    .build());
        }


    return true;
    }

    public ListPermissionsResponseDto listPermissionsByEmployeeId(ListPermissionsRequestDto requestDto) {
        Long employeeId = requestDto.getEmployeeId();
        Optional<Employee> optionalEmployee = repository.findOptionalByEmail(requestDto.getEmail());

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            ListPermissionsResponseDto responseDto = new ListPermissionsResponseDto();
            responseDto.setEmployeeId(employeeId);
            responseDto.setEmployeeName(employee.getName());

            return responseDto;

        } else {
            throw new EmployeeException(ErrorType.EMPLOYEE_NOT_FOUND);
        }
    }
    public Optional<List<Employee>> findOptionalByCompanyName(String companyName) {
        Optional<List<Employee>> optionalEmployee = repository.findOptionalByCompanyName(companyName);
        if (optionalEmployee.isEmpty()) {
            throw new EmployeeException(ErrorType.EMPLOYEE_NOT_FOUND);
        }
        return repository.findOptionalByCompanyName(companyName);

    }


    public Boolean updateEmployee(UpdateEmployeeRequestDto requestDto) {
        Optional<Employee> employeeExists = repository.findById(requestDto.getId());
        if (employeeExists.isEmpty()) {
            throw new EmployeeException(ErrorType.ID_NOT_FOUND);
        }
        Employee existingEmployee = employeeExists.get();
        existingEmployee.setName(requestDto.getName());
        existingEmployee.setSurname(requestDto.getSurname());
        existingEmployee.setEmail(requestDto.getEmail());
        existingEmployee.setPhone(requestDto.getPhone());
        existingEmployee.setUpdateDate(LocalDate.now());

        Employee updatedEmployee = update(existingEmployee);

        return true;
    }


    public Optional<ViewAllEmployeeInfoResponseDto>viewAllEmployeeInfo(ViewAllEmployeeInfoRequestDto requestDto) {

        Optional<Employee> employees = repository.findByCompanyIdAndDepartment(requestDto.getCompanyId(), requestDto.getDepartment());

        if (!employees.isPresent()) {
            throw new EmployeeException(ErrorType.EMPLOYEE_NOT_FOUND);
        }

        Employee employee = employees.get();
        ViewAllEmployeeInfoResponseDto responseDto = IEmployeeMapper.INSTANCE.toViewAllEmployeeInfoResponseDto(employee);

        return Optional.of(responseDto);
    }

    public Employee getEmployeeById(String id){
        Optional<Employee> optionalEmployee = findById(id);
        if(optionalEmployee.isEmpty())
            throw new EmployeeException(ErrorType.EMPLOYEE_NOT_FOUND);
        return optionalEmployee.get();
    }


}





