package fr.esgi.pa.server.code.infrastructure.dataprovider.mapper;

import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.infrastructure.dataprovider.entity.JpaCode;
import fr.esgi.pa.server.common.core.mapper.MapperDomainToEntity;
import fr.esgi.pa.server.common.core.mapper.MapperEntityToDomain;
import org.springframework.stereotype.Component;

@Component
public class CodeMapper implements
        MapperDomainToEntity<Code, JpaCode>,
        MapperEntityToDomain<JpaCode, Code> {
    @Override
    public JpaCode domainToEntity(Code domain) {
        return new JpaCode()
                .setId(domain.getId())
                .setUserId(domain.getUserId())
                .setContent(domain.getContent())
                .setExerciseCaseId(domain.getExerciseCaseId())
                .setIsResolved(domain.getIsResolved())
                .setResolvedDate(domain.getResolvedDate());
    }

    @Override
    public Code entityToDomain(JpaCode entity) {
        return new Code()
                .setId(entity.getId())
                .setUserId(entity.getUserId())
                .setContent(entity.getContent())
                .setExerciseCaseId(entity.getExerciseCaseId())
                .setIsResolved(entity.getIsResolved())
                .setResolvedDate(entity.getResolvedDate());
    }
}
