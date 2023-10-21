package io.componenttesting.testmanager.model;

import lombok.Data;
import lombok.Getter;

@Data
public class AverageTestResults {

    @Getter
    private int averagePassingPercentage;
}
