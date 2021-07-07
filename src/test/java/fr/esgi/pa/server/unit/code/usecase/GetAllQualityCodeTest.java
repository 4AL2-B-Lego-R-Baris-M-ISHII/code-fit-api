package fr.esgi.pa.server.unit.code.usecase;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.usecase.GetAllQualityCode;
import fr.esgi.pa.server.code.usecase.GetQualityCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetAllQualityCodeTest {
    private final long userId = 12L;
    private GetAllQualityCode sut;

    @Mock
    private CodeDao mockCodeDao;

    @Mock
    private GetQualityCode mockGetQualityCode;


    @BeforeEach
    void setup() {
        sut = new GetAllQualityCode(mockCodeDao, mockGetQualityCode);
    }

    @Test
    void should_get_all_code_by_userId() {
        sut.execute(userId);

        verify(mockCodeDao, times(1)).findAllByUserId(userId);
    }

}