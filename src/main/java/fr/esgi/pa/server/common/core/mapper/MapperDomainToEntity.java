package fr.esgi.pa.server.common.core.mapper;

public interface MapperDomainToEntity<D, E> {
    E domainToEntity(D domain);
}
