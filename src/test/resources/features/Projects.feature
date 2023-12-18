Feature: Projecten beheren

  Scenario: I should be able to create a new project
    When I use "/api/projects" to send:
    """
    {
      "name": "ProjectX"
    }
    """
    Then path "/api/projects/ProjectX" should exist and give me:
    """
    {
      "name": "ProjectX",
      "rating": null,
      "testdata": []
    }
    """

  Scenario Outline: A team should always have a unique name - <name>
    Given project "<existing>" exists
    When I use "/api/projects" to send:
    """
    {
      "name": "<name>"
    }
    """
    Then I should receive a <status> status code

    Examples:
      | existing   | name       | status |
      | Project301 | Project301 | 422    |
      | PROJECT302 | project302 | 422    |
      | Project303 | Project313 | 200    |

  Scenario: I should be able to delete an existing project
    Given project "AAA" exists
    When I delete "/api/projects/AAA"
    Then path "/api/projects/AAA" should receive a 404 status code