package uz.consortgroup.course_service.config.properties;

import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;
import uz.consortgroup.core.api.v1.dto.course.enumeration.Language;

@Component
public class StringToLanguageConverter implements Converter<String, Language> {
    @Override
    public Language convert(String source) {
        return Language.fromValue(source);
    }
}
