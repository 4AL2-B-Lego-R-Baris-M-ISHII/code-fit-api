package fr.esgi.pa.server.unit.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.usecase.CompileCodeById;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompileCodeByIdTest {

    private final long codeId = 61L;
    private CompileCodeById sut;

    @Mock
    private CodeDao mockCodeDao;

    @BeforeEach
    void setup() {
        sut = new CompileCodeById(mockCodeDao);
    }

    // TODO : find code by id
    @Test
    void should_find_code_by_id() throws NotFoundException {
        sut.execute(codeId);

        verify(mockCodeDao, times(1)).findById(codeId);
    }
    

    // TODO : find tests by exercise case id
    // TODO : compile all test with code content
}