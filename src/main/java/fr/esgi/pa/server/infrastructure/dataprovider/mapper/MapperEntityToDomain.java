package fr.esgi.pa.server.infrastructure.dataprovider.mapper;

public interface MapperEntityToDomain<E, D> {
    D entityToDomain(E entity);
}
