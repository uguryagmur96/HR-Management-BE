package com.HRMS.mapper;

import com.HRMS.dto.request.AddUserRequestDto;
import com.HRMS.dto.request.DoRegisterRequestDto;
import com.HRMS.rabbitmq.model.CreateEmployee;
import com.HRMS.repository.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper( unmappedTargetPolicy = ReportingPolicy.IGNORE,componentModel = "spring")
public interface IAuthMapper {

    IAuthMapper INSTANCE = Mappers.getMapper(IAuthMapper.class);

    Auth authFromDto(final DoRegisterRequestDto dto);


}
