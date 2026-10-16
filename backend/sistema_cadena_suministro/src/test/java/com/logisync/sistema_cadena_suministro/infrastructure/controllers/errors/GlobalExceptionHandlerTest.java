package com.logisync.sistema_cadena_suministro.infrastructure.controllers.errors;

import com.logisync.sistema_cadena_suministro.domain.exceptions.BusinessRuleException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ConflictException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.InvalidStateTransitionException;
import com.logisync.sistema_cadena_suministro.domain.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new FailingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void notFoundMapsTo404() throws Exception {
        mockMvc.perform(get("/fail/{kind}", "not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("urn:logisync:problem:not-found"));
    }

    @Test
    void conflictAndInvalidStateMapTo409() throws Exception {
        mockMvc.perform(get("/fail/{kind}", "conflict")).andExpect(status().isConflict());
        mockMvc.perform(get("/fail/{kind}", "state"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("urn:logisync:problem:invalid-state"));
        mockMvc.perform(get("/fail/{kind}", "lock")).andExpect(status().isConflict());
    }

    @Test
    void businessRuleMapsTo422() throws Exception {
        mockMvc.perform(get("/fail/{kind}", "rule")).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void accessDeniedMapsTo403() throws Exception {
        mockMvc.perform(get("/fail/{kind}", "denied")).andExpect(status().isForbidden());
    }

    @Test
    void unexpectedErrorDoesNotLeakDetails() throws Exception {
        mockMvc.perform(get("/fail/{kind}", "boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void validationErrorsAreListedPerField() throws Exception {
        mockMvc.perform(post("/validate").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void malformedJsonMapsTo400() throws Exception {
        mockMvc.perform(post("/validate").contentType(MediaType.APPLICATION_JSON).content("{bad"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("urn:logisync:problem:malformed-request"));
    }

    record Payload(@NotBlank String name) {
    }

    @RestController
    static class FailingController {

        @GetMapping("/fail/{kind}")
        String fail(@PathVariable("kind") String kind) {
            switch (kind) {
                case "not-found" -> throw new ResourceNotFoundException("Thing", 1);
                case "conflict" -> throw new ConflictException("conflict");
                case "state" -> throw new InvalidStateTransitionException("Order", "ENTREGADO", "be cancelled");
                case "lock" -> throw new OptimisticLockingFailureException("stale");
                case "rule" -> throw new BusinessRuleException("rule");
                case "denied" -> throw new AccessDeniedException("denied");
                default -> throw new IllegalStateException("secret internals");
            }
        }

        @PostMapping("/validate")
        String validate(@Valid @RequestBody Payload payload) {
            return payload.name();
        }
    }
}
