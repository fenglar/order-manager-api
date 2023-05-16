# Task for mentoring

## Junits + Tests

- Fix all junits present on the project;
- Add enough tests to cover at least 80%;
  - Use integration tests;
  - You can start the  test from "service", not from controller;
- Add testscontainer for the database
    - In case you need to use any "free" docker-desktop, you can use Rancher desktop
- For any API consumer use webmock for it
  - In the future we can switch it to Spring cloud contracts
- Create API contract using SpringCloud Contracts (Optional for now - but good to have)
- Create at least one saving verification using argument captor. Captor on repository.save
- Create at least one saving verification checking from the DB.
