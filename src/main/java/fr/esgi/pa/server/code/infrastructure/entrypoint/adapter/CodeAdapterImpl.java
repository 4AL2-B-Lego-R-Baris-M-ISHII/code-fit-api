package fr.esgi.pa.server.code.infrastructure.entrypoint.adapter;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.entity.Code;
import org.springframework.stereotype.Component;

@Component
public class CodeAdapterImpl implements CodeAdapter {
    @Override
    public DtoCode domainToDto(Code code) {
        return new DtoCode()
                .setCodeId(code.getId())
                .setIsResolved(code.getIsResolved())
                .setContent(code.getContent());
    }
}
