package uz.consortgroup.course_service.service.course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePurchaseValidationResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.module.ModuleResponseDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.CourseTranslation;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;
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

import java.util.List;
import java.util.UUID;

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
    private final CourseMapper courseMapper;
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;
    private final ModuleService moduleService;
    private final ModuleMapper moduleMapper;


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    @AllAspect
    public CourseResponseDto create(CourseCreateRequestDto dto) {
        Course course = courseMapper.toEntity(dto);
        Course savedCourse = courseRepository.save(course);
        savedCourse = entityManager.getReference(Course.class, savedCourse.getId());

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

        return response;
    }

    @Override
    @AllAspect
    public CoursePurchaseValidationResponseDto validateCourseForPurchase(UUID courseId) {
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
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void delete(UUID courseId) {
        courseRepository.findById(courseId).ifPresent(courseRepository::delete);
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