package uz.consortgroup.course_service.service.course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseStatus;
import uz.consortgroup.core.api.v1.dto.course.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePreviewResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePurchaseValidationResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.TeacherShortDto;
import uz.consortgroup.core.api.v1.dto.course.response.lesson.LessonPreviewDto;
import uz.consortgroup.core.api.v1.dto.course.response.module.ModulePreviewDto;
import uz.consortgroup.core.api.v1.dto.course.response.module.ModuleResponseDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.CourseTranslation;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.LessonTranslation;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.ModuleTranslation;
import uz.consortgroup.course_service.exception.CourseNotFoundException;
import uz.consortgroup.course_service.mapper.CourseMapper;
import uz.consortgroup.course_service.mapper.CourseTranslationMapper;
import uz.consortgroup.course_service.mapper.LessonMapper;
import uz.consortgroup.course_service.mapper.ModuleMapper;
import uz.consortgroup.course_service.mapper.ModuleTranslationMapper;
import uz.consortgroup.course_service.repository.CourseRepository;
import uz.consortgroup.course_service.service.course.translation.CourseTranslationService;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.lesson.translation.LessonTranslationService;
import uz.consortgroup.course_service.service.module.ModuleService;
import uz.consortgroup.course_service.service.module.translation.ModuleTranslationService;
import uz.consortgroup.course_service.service.teacher.TeacherInfoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseTranslationMapper courseTranslationMapper;
    private final ModuleTranslationMapper moduleTranslationMapper;
    private final CourseTranslationService courseTranslationService;
    private final ModuleTranslationService moduleTranslationService;
    private final LessonTranslationService lessonTranslationService;
    private final TeacherInfoService teacherInfoService;
    private final CourseMapper courseMapper;
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;
    private final ModuleService moduleService;
    private final ModuleMapper moduleMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public CourseResponseDto create(CourseCreateRequestDto dto) {
        log.info("Creating course...");
        Course course = courseMapper.toEntity(dto);
        Course savedCourse = courseRepository.save(course);
        savedCourse = entityManager.getReference(Course.class, savedCourse.getId());

        log.info("Saving translations and modules for course ID: {}", savedCourse.getId());
        courseTranslationService.saveTranslations(dto.getTranslations(), savedCourse);
        List<CourseTranslation> savedTranslations = courseTranslationService.findByCourseId(savedCourse.getId());

        List<Module> savedModules = moduleService.saveModules(dto.getModules(), savedCourse);
        moduleTranslationService.saveTranslations(dto.getModules(), savedModules);

        List<Lesson> savedLessons = lessonService.saveLessons(dto.getModules(), savedModules);
        lessonTranslationService.saveTranslations(dto.getModules(), savedLessons);

        CourseResponseDto response = courseMapper.toResponseDto(savedCourse);
        response.setTranslations(savedTranslations.stream()
                .map(courseTranslationMapper::toResponseDto)
                .toList());

        mapModulesToResponseDto(response, savedModules);
        log.info("Course creation complete for ID: {}", savedCourse.getId());
        return response;
    }

    @Override
    public CoursePurchaseValidationResponseDto validateCourseForPurchase(UUID courseId) {
        log.info("Validating course for purchase: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(String.format("Course with id %s not found", courseId)));

        return CoursePurchaseValidationResponseDto.builder()
                .id(course.getId())
                .courseStatus(course.getCourseStatus())
                .courseType(course.getCourseType())
                .priceType(course.getPriceType())
                .priceAmount(course.getPriceAmount())
                .startTime(course.getStartTime())
                .endTime(course.getEndTime())
                .purchasable(course.isPurchasable())
                .build();
    }

    @Override
    public void delete(UUID courseId) {
        log.info("Deleting course with ID: {}", courseId);
        courseRepository.findById(courseId).ifPresent(course -> {
            courseRepository.delete(course);
            log.info("Course deleted: {}", course.getId());
        });
    }

    @Override
    public CourseResponseDto getCourseById(UUID courseId) {
        log.info("Fetching course by ID: {}", courseId);
        return courseRepository.findById(courseId)
                .map(courseMapper::toResponseDto)
                .orElseThrow(() -> new CourseNotFoundException(String.format("Course with id %s not found", courseId)));
    }

    @Override
    public CoursePreviewResponseDto getCoursePreview(UUID courseId, Language language) {
        log.info("Fetching course preview for ID: {} in language: {}", courseId, language);
        Course course = courseRepository.findCourseWithTranslations(courseId, CourseStatus.ACTIVE)
                .orElseThrow(() -> new CourseNotFoundException("Course not found or not published"));

        List<Module> modules = moduleService.findByCourseId(courseId);
        List<UUID> moduleIds = modules.stream().map(Module::getId).toList();
        List<Lesson> lessons = lessonService.findByModuleIds(moduleIds);

        Map<UUID, List<Lesson>> lessonMap = lessons.stream()
                .collect(Collectors.groupingBy(lesson -> lesson.getModule().getId()));

        modules.forEach(module -> {
            List<Lesson> moduleLessons = lessonMap.getOrDefault(module.getId(), List.of());
            module.setLessons(moduleLessons);
        });

        course.setModules(modules);

        TeacherShortDto teacher = teacherInfoService.getTeacherShortInfo(course.getAuthorId());

        List<ModulePreviewDto> modulePreviews = modules.stream()
                .map(module -> mapToModulePreview(module, language))
                .filter(m -> !m.getPreviewLessons().isEmpty())
                .toList();

        log.info("Course preview prepared for course ID: {}", courseId);

        return CoursePreviewResponseDto.builder()
                .id(course.getId())
                .coverImageUrl(course.getCoverImageUrl())
                .priceType(course.getPriceType())
                .priceAmount(course.getPriceAmount())
                .translations(courseTranslationMapper.toResponseList(course.getTranslations()))
                .teacher(teacher)
                .modules(modulePreviews)
                .build();
    }

    @Override
    public boolean courseExistsById(UUID courseId) {
        log.debug("Checking existence of course with ID: {}", courseId);
        return courseRepository.existsById(courseId);
    }

    private ModulePreviewDto mapToModulePreview(Module module, Language lang) {
        String moduleTitle = module.getTranslations().stream()
                .filter(t -> t.getLanguage().equals(lang))
                .map(ModuleTranslation::getTitle)
                .findFirst()
                .orElse("Untitled");

        List<LessonPreviewDto> lessons = module.getLessons().stream()
                .filter(Lesson::getIsPreview)
                .map(lesson -> {
                    String lessonTitle = lesson.getTranslations().stream()
                            .filter(t -> t.getLanguage().equals(lang))
                            .map(LessonTranslation::getTitle)
                            .findFirst()
                            .orElse("Untitled");

                    return LessonPreviewDto.builder()
                            .id(lesson.getId())
                            .title(lessonTitle)
                            .durationMinutes(lesson.getDurationMinutes())
                            .build();
                })
                .toList();

        return ModulePreviewDto.builder()
                .id(module.getId())
                .title(moduleTitle)
                .previewLessons(lessons)
                .build();
    }

    private void mapModulesToResponseDto(CourseResponseDto response, List<Module> savedModules) {
        response.setModules(savedModules.stream()
                .map(module -> {
                    ModuleResponseDto moduleDto = moduleMapper.toResponseDto(module);
                    moduleDto.setTranslations(moduleTranslationService.findByModuleId(module.getId()).stream()
                            .map(moduleTranslationMapper::toResponseDto)
                            .toList());

                    moduleDto.setLessons(lessonService.findByModuleId(module.getId()).stream()
                            .map(lessonMapper::toResponseDto)
                            .toList());
                    return moduleDto;
                }).toList());
    }
}
