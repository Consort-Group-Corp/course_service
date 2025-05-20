package uz.consortgroup.course_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseStatus;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.course.enumeration.PriceType;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.course_service.service.course.CourseService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @Test
    void courseCreate_Success() throws Exception {
        CourseCreateRequestDto request = CourseCreateRequestDto.builder()
                .authorId(UUID.randomUUID())
                .courseType(CourseType.PREMIUM)
                .priceType(PriceType.PAID)
                .priceAmount(BigDecimal.valueOf(100))
                .courseStatus(CourseStatus.DRAFT)
                .translations(List.of(
                        CourseTranslationRequestDto.builder()
                                .language(Language.ENGLISH)
                                .title("Test Course")
                                .slug("test-course")
                                .build()
                ))
                .build();

        when(courseService.create(any())).thenReturn(new CourseResponseDto());

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void courseCreate_InvalidRequest_MissingRequiredFields() throws Exception {
        CourseCreateRequestDto request = CourseCreateRequestDto.builder().build();

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void courseCreate_InvalidRequest_EmptyTranslations() throws Exception {
        CourseCreateRequestDto request = CourseCreateRequestDto.builder()
                .authorId(UUID.randomUUID())
                .courseType(CourseType.SPECIAL)
                .priceType(PriceType.FREE)
                .courseStatus(CourseStatus.HIDDEN)
                .translations(Collections.emptyList())
                .build();

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
