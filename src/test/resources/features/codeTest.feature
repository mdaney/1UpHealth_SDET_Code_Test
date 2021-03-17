@Pagination
Feature: 1UpHealth Pagination of FHIR API for a single Observation resource

  Scenario Outline: Verify GET operation to view the Observation resource with newly generated token
    Given I am an Authorized User with body "<app_user_id>" and "<client_id>" and "<client_secret>"
    And I have the access token with "<client_id>" "<client_secret>" "<grant_type>"
    When I view Observation resource page for "<patient_id>"
    Then I should view A total of 10 non-duplicate records and next URL in each page for "<patient_id>"
    Then I should NOT view the next URL in the last page
    Examples:
      | app_user_id | client_id                        | client_secret                    | grant_type         | patient_id   |
      | saney       | bad630a0843b99063a81209e894ee167 | 798c8a8573d3d0fe46f2d9cabf29dc21 | authorization_code | e467f71f186f |


  Scenario Outline: Verify GET operation to view the Observation resource with Refresh Token
    Given I am an Authorized User with body "<app_user_id>" and "<client_id>" and "<client_secret>"
    And I have the access token with "<client_id>" "<client_secret>" "<grant_type>"
    When I Generate a New Access Token for "<client_id>" "<client_secret>" "<refresh_grant_type>"
    Then I view Observation resource page for "<patient_id>"
    Then I should view A total of 10 non-duplicate records and next URL in each page for "<patient_id>"
    Then I should NOT view the next URL in the last page
    Examples:
      | app_user_id | client_id                        | client_secret                    | grant_type         | patient_id   | refresh_grant_type |
      | saney       | bad630a0843b99063a81209e894ee167 | 798c8a8573d3d0fe46f2d9cabf29dc21 | authorization_code | e467f71f186f | refresh_token      |


