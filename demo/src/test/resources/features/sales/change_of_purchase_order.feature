Feature: Change of Purchase Order Period
  As a Rentit's customer
  So that I could change my mind
  I want to change PO rental period

  Background: Plant catalog
    Given the following plant catalog
      | id | name           | description                      | price  |
      |  1 | Mini Excavator | 1.5 Tonne Mini excavator         | 150.00 |
    And the following inventory
      | id | plantInfo | serialNumber | equipmentCondition |
      |  1 |     1     | exc-mn1.5-01 | SERVICEABLE        |
      |  2 |     1     | exc-mn1.5-01 | SERVICEABLE        |

  Scenario: Change PO period if same item is available
    Given the existing PO
      | id | plant  | item_id | startDate | endDate     | status    |
      | 1  |  1     |   1     | 2019-05-28 | 2019-05-30 | OPEN  |
    When the customer wants to change PO period from "2019-05-29" to "2019-06-01"
    Then PO period is changed
