package io.componenttesting.testmanager.service;

import io.componenttesting.model.ProjectResponse;
import io.componenttesting.model.TestData;
import io.componenttesting.testmanager.dao.ProjectEntity;
import io.componenttesting.testmanager.dao.TestDataEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ProjectMapper {

    ProjectResponse map(ProjectEntity entity);
    TestData map(TestDataEntity entity);
}
