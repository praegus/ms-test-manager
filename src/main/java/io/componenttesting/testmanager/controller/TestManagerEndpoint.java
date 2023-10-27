package io.componenttesting.testmanager.controller;

import io.componenttesting.api.ApiApi;
import io.componenttesting.model.ProjectCreate;
import io.componenttesting.model.ProjectResponse;
import io.componenttesting.testmanager.exceptions.NotFoundException;
import io.componenttesting.testmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestManagerEndpoint implements ApiApi {

    @Autowired
    ProjectService projectService;

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<String> handleException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @Override
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String projectName) {
        return ResponseEntity.ok().body(projectService.getProject(projectName));
    }

    @Override
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        return ResponseEntity.ok().body(projectService.getAllProjects());
    }

    @Override
    @PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<Void> createProjects(@RequestBody @Valid ProjectCreate project) {
        projectService.createNewProject(project);
        return null;
    }

    @Override
    @PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<Void> deleteProject(String projectName) {
        projectService.deleteProject(projectName);
        return null;
    }
}
