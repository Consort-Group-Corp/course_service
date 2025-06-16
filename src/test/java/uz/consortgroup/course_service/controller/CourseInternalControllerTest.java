package uz.consortgroup.course_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseStatus;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.PriceType;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePurchaseValidationResponseDto;
import uz.consortgroup.course_service.service.course.CourseService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseInternalController.class)
class CourseInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @Test
    void validateCourseForPurchase_ShouldReturnValidResponse() throws Exception {
        UUID courseId = UUID.randomUUID();
        CoursePurchaseValidationResponseDto response = CoursePurchaseValidationResponseDto.builder()
                .id(courseId)
                .courseStatus(CourseStatus.ACTIVE)
                .courseType(CourseType.PREMIUM)
                .priceType(PriceType.PAID)
                .priceAmount(BigDecimal.valueOf(100))
                .startTime(Instant.now().plusSeconds(3600))
                .endTime(Instant.now().plusSeconds(7200))
                .purchasable(true)
                .build();

        when(courseService.validateCourseForPurchase(courseId)).thenReturn(response);

        mockMvc.perform(get("/internal/courses/{courseId}/purchase-validation", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId.toString()))
                .andExpect(jsonPath("$.courseStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.courseType").value("PREMIUM"))
                .andExpect(jsonPath("$.priceType").value("PAID"))
                .andExpect(jsonPath("$.priceAmount").value(100))
                .andExpect(jsonPath("$.purchasable").value(true));
    }

    @Test
    void validateCourseForPurchase_ShouldReturnNotPurchasable() throws Exception {
        UUID courseId = UUID.randomUUID();
        CoursePurchaseValidationResponseDto response = CoursePurchaseValidationResponseDto.builder()
                .purchasable(false)
                .build();

        when(courseService.validateCourseForPurchase(courseId)).thenReturn(response);

        mockMvc.perform(get("/internal/courses/{courseId}/purchase-validation", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchasable").value(false));
    }

    @Test
    void validateCourseForPurchase_ShouldHandleServiceException() throws Exception {
        UUID courseId = UUID.randomUUID();

        when(courseService.validateCourseForPurchase(courseId))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/internal/courses/{courseId}/purchase-validation", courseId))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void validateCourseForPurchase_ShouldReturnFreeCourseDetails() throws Exception {
        UUID courseId = UUID.randomUUID();
        CoursePurchaseValidationResponseDto response = CoursePurchaseValidationResponseDto.builder()
                .priceType(PriceType.FREE)
                .purchasable(true)
                .build();

        when(courseService.validateCourseForPurchase(courseId)).thenReturn(response);

        mockMvc.perform(get("/internal/courses/{courseId}/purchase-validation", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceType").value("FREE"))
                .andExpect(jsonPath("$.purchasable").value(true));
    }
}