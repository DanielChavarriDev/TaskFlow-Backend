
package com.danielchavarri.taskflow.common.interfaces.rest;

import com.danielchavarri.taskflow.project.application.ProjectNotFoundException;
import com.danielchavarri.taskflow.task.application.TaskNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.danielchavarri.taskflow.user.application.EmailAlreadyExistsException;
import com.danielchavarri.taskflow.user.application.InvalidCredentialsException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ProblemDetail handleValidation(
                        MethodArgumentNotValidException exception) {

                ProblemDetail problem = createProblem(
                                HttpStatus.BAD_REQUEST,
                                "Validation failed",
                                "One or more request fields contain invalid values.");

                Map<String, String> errors = exception.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .collect(Collectors.toMap(
                                                error -> error.getField(),
                                                error -> error.getDefaultMessage() != null
                                                                ? error.getDefaultMessage()
                                                                : "Invalid value",
                                                (first, second) -> first));

                problem.setProperty("errors", errors);
                return problem;
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ProblemDetail handleUnreadableMessage(
                        HttpMessageNotReadableException exception) {

                return createProblem(
                                HttpStatus.BAD_REQUEST,
                                "Malformed request",
                                "The request body contains invalid JSON or invalid field values.");
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ProblemDetail handleTypeMismatch(
                        MethodArgumentTypeMismatchException exception) {

                return createProblem(
                                HttpStatus.BAD_REQUEST,
                                "Invalid parameter",
                                "The parameter '%s' has an invalid value."
                                                .formatted(exception.getName()));
        }

        @ExceptionHandler(ProjectNotFoundException.class)
        public ProblemDetail handleProjectNotFound(
                        ProjectNotFoundException exception) {

                return createProblem(
                                HttpStatus.NOT_FOUND,
                                "Project not found",
                                exception.getMessage());
        }

        @ExceptionHandler(TaskNotFoundException.class)
        public ProblemDetail handleTaskNotFound(
                        TaskNotFoundException exception) {

                return createProblem(
                                HttpStatus.NOT_FOUND,
                                "Task not found",
                                exception.getMessage());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ProblemDetail handleIllegalArgument(
                        IllegalArgumentException exception) {

                return createProblem(
                                HttpStatus.BAD_REQUEST,
                                "Invalid request",
                                exception.getMessage());
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ProblemDetail handleDataIntegrityViolation(
                        DataIntegrityViolationException exception) {

                return createProblem(
                                HttpStatus.CONFLICT,
                                "Data conflict",
                                "The operation conflicts with existing database constraints.");
        }

        private ProblemDetail createProblem(
                        HttpStatus status,
                        String title,
                        String detail) {

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);

                problem.setTitle(title);
                problem.setType(URI.create(
                                "https://api.taskflow.local/problems/"
                                                + status.value()));

                return problem;
        }

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ProblemDetail handleEmailAlreadyExists(
                        EmailAlreadyExistsException exception) {

                return createProblem(
                                HttpStatus.CONFLICT,
                                "Email already registered",
                                exception.getMessage());
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ProblemDetail handleInvalidCredentials(
                        InvalidCredentialsException exception) {
                return createProblem(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid credentials",
                                exception.getMessage());
        }
}