package fr.esgi.pa.server.common.mapper;

public interface MapperEntityToDomain<E, D> {
    D entityToDomain(E entity);
}
