package uz.consortgroup.course_service.service.course;

import uz.consortgroup.core.api.v1.dto.course.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePreviewResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePurchaseValidationResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.course_service.exception.CourseNotFoundException;

import java.util.UUID;

public interface CourseService {
    CourseResponseDto create(CourseCreateRequestDto dto);
    CoursePurchaseValidationResponseDto validateCourseForPurchase(UUID courseId);
    void delete(UUID courseId);
    CourseResponseDto getCourseById(UUID courseId);

    /**
     * Получает данные для предосмотор курса.
     * Возвращает основные данные курса, переводы, информацию о преподавателе
     * и список модулей с доступными уроками-превью.
     *
     * @param courseId идентификатор курса
     * @param language язык локализации (RU, UZ, EN)
     * @return объект {@link CoursePreviewResponseDto} с публичной информацией о курсе
     * @throws CourseNotFoundException если курс не существует или ещё не опубликован
     */
    CoursePreviewResponseDto getCoursePreview(UUID courseId, Language language);
}
