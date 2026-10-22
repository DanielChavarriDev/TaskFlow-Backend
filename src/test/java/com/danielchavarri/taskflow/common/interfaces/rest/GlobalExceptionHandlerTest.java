
package com.danielchavarri.taskflow.common.interfaces.rest;

import com.danielchavarri.taskflow.project.application.ProjectNotFoundException;
import com.danielchavarri.taskflow.task.application.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturn404WhenProjectDoesNotExist() {
        ProjectNotFoundException exception = mock(ProjectNotFoundException.class);

        when(exception.getMessage())
                .thenReturn("No se encontró el proyecto solicitado");

        ProblemDetail response = handler.handleProjectNotFound(exception);

        assertEquals(404, response.getStatus());
        assertEquals("Project not found", response.getTitle());
        assertEquals(
                "No se encontró el proyecto solicitado",
                response.getDetail());
    }

    @Test
    void shouldReturn404WhenTaskDoesNotExist() {
        TaskNotFoundException exception = mock(TaskNotFoundException.class);

        when(exception.getMessage())
                .thenReturn("No se encontró la tarea solicitada");

        ProblemDetail response = handler.handleTaskNotFound(exception);

        assertEquals(404, response.getStatus());
        assertEquals("Task not found", response.getTitle());
        assertEquals(
                "No se encontró la tarea solicitada",
                response.getDetail());
    }

    @Test
    void shouldReturn400ForIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");

        ProblemDetail response = handler.handleIllegalArgument(exception);

        assertEquals(400, response.getStatus());
        assertEquals("Invalid request", response.getTitle());
        assertEquals("Argumento inválido", response.getDetail());
    }

    @Test
    void shouldReturn400ForMalformedRequest() {
        org.springframework.http.converter.HttpMessageNotReadableException exception = mock(
                org.springframework.http.converter.HttpMessageNotReadableException.class);

        ProblemDetail response = handler.handleUnreadableMessage(exception);

        assertEquals(400, response.getStatus());
        assertEquals("Malformed request", response.getTitle());
        assertNotNull(response.getDetail());
    }

    @Test
    void shouldReturn409ForDataIntegrityViolation() {
        org.springframework.dao.DataIntegrityViolationException exception = new org.springframework.dao.DataIntegrityViolationException(
                "Database constraint violation");

        ProblemDetail response = handler.handleDataIntegrityViolation(exception);

        assertEquals(409, response.getStatus());
        assertEquals("Data conflict", response.getTitle());
        assertNotNull(response.getDetail());
    }

    @Test
    void shouldIncludeProblemTypeForNotFoundResponse() {
        ProjectNotFoundException exception = mock(ProjectNotFoundException.class);

        when(exception.getMessage()).thenReturn("Proyecto inexistente");

        ProblemDetail response = handler.handleProjectNotFound(exception);

        assertNotNull(response.getType());
        assertEquals(
                "https://api.taskflow.local/problems/404",
                response.getType().toString());
    }

    @Test
    void shouldReturn400ForInvalidParameterType() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);

        when(exception.getName()).thenReturn("id");

        ProblemDetail response = handler.handleTypeMismatch(exception);

        assertEquals(400, response.getStatus());
        assertEquals("Invalid parameter", response.getTitle());
        assertEquals(
                "The parameter 'id' has an invalid value.",
                response.getDetail());
        assertEquals(
                "https://api.taskflow.local/problems/400",
                response.getType().toString());
    }
}