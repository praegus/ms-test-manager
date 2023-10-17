Feature: Test data

  Scenario: When test data is received of an existing project it will be added
    Given project "TestProject1" exists
    When project "TestProject1" has received the following testdata:
    """
    {
      "project": "TestProject1",
      "testName": "test01",
      "testRunId": "12",
      "result": "PASSED"
    }
    """
    Then path "/api/projects/TestProject1" should exist and give me:
    """
    {
      "name": "TestProject1",
      "rating": "${json-unit.ignore}",
      "testdata": [{"result":"PASSED","testname":"test01","testrunId":12}]
    }
    """
