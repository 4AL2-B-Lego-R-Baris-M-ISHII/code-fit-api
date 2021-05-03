package fr.esgi.pa.server.common.mapper;

public interface MapperDomainToEntity<D, E> {
    E domainToEntity(D domain);
}
