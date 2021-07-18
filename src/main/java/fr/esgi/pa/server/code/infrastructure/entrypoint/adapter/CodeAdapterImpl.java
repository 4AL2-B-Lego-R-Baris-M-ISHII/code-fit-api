package fr.esgi.pa.server.code.infrastructure.entrypoint.adapter;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.entity.Code;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CodeAdapterImpl implements CodeAdapter {
    @Override
    public DtoCode domainToDto(Code code) {
        var resolvedDateTimestampSec = Optional.ofNullable(code.getResolvedDate())
                .map(resolvedDate -> resolvedDate.getTime() / 1000)
                .orElse(null);
        return new DtoCode()
                .setCodeId(code.getId())
                .setIsResolved(code.getIsResolved())
                .setContent(code.getContent())
                .setResolvedDateTimestampSec(resolvedDateTimestampSec);
    }
}
