Feature: Project authorization

  Scenario Outline: I need WRITE access to create a project
    When I use "/api/projects" with a <type> token to send:
    """
    { "name": "<name>" }
    """
    Then I should receive a <status> status code

    Examples:
      | name            | type    | status |
      | CanCreate       | write   | 200    |
      | NotEnoughRights | read    | 403    |
      | InvalidToken    | invalid | 401    |