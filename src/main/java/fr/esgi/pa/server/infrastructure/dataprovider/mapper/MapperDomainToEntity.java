package fr.esgi.pa.server.infrastructure.dataprovider.mapper;

public interface MapperDomainToEntity<D, E> {
    E domainToEntity(D domain);
}
