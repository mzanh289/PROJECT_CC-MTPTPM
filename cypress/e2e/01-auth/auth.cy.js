describe("Auth flow", () => {
  it("logs in with admin account", () => {
    cy.loginAsAdmin();
    cy.contains("h2", "Xin chao", { matchCase: false }).should("be.visible");
  });

  it("shows error for invalid credentials", () => {
    cy.login("admin@company.com", "wrong-password");
    cy.url().should("include", "/login");
    cy.getByTestId("login-error").should("be.visible");
  });

  it("logs out from employee role", () => {
    cy.loginAsEmployee();
    cy.getByTestId("logout-button").click();
    cy.url().should("include", "/login");
  });
});
