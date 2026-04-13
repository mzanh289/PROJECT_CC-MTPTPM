Cypress.Commands.add("getByTestId", (testId) => {
  return cy.get(`[data-testid="${testId}"]`);
});

Cypress.Commands.add("login", (email, password) => {
  cy.visit("/login");
  cy.getByTestId("login-email").clear().type(email);
  cy.getByTestId("login-password").clear().type(password, { log: false });
  cy.getByTestId("login-submit").click();
});

Cypress.Commands.add("loginAsAdmin", () => {
  cy.login("admin@company.com", "123456");
  cy.url().should("include", "/admin/dashboard");
});

Cypress.Commands.add("loginAsEmployee", () => {
  cy.login("employee1@company.com", "123456");
  cy.url().should("include", "/employee/dashboard");
});
