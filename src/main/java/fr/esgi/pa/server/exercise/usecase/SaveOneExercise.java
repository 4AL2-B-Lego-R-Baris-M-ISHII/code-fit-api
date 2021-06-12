package fr.esgi.pa.server.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.utils.DefaultExercise;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.exception.IncorrectLanguageNameException;
import fr.esgi.pa.server.user.core.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveOneExercise {
    private final UserDao userDao;
    private final LanguageDao languageDao;
    private final DefaultExercise defaultExercise;

    public Long execute(
            String title,
            String description,
            String language,
            Long userId) throws NotFoundException, IncorrectLanguageNameException {
        if (!userDao.existsById(userId)) {
            var message = String.format("%s : User with id '%d' not exists", this.getClass(), userId);
            throw new NotFoundException(message);
        }
        Language foundLanguage = languageDao.findByStrLanguage(language);

        return defaultExercise.createDefaultExercise(title, description, foundLanguage, userId);
    }
}
