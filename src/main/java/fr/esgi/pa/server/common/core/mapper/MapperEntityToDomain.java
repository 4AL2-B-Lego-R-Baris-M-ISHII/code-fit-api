package fr.esgi.pa.server.common.core.mapper;

public interface MapperEntityToDomain<E, D> {
    D entityToDomain(E entity);
}
