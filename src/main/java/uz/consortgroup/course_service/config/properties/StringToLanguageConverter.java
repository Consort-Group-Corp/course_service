package uz.consortgroup.course_service.config.properties;

import org.springframework.stereotype.Component;
import uz.consortgroup.course_service.entity.enumeration.Language;
import org.springframework.core.convert.converter.Converter;

@Component
public class StringToLanguageConverter implements Converter<String, Language> {
    @Override
    public Language convert(String source) {
        return Language.fromValue(source);
    }
}
