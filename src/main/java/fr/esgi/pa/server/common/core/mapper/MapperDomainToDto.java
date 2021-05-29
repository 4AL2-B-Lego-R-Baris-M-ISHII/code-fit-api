package fr.esgi.pa.server.common.core.mapper;

public interface MapperDomainToDto<Domain, Dto> {
    Dto domainToDto(Domain domain);
}
