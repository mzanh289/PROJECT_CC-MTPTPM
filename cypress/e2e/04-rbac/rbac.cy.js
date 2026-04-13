describe("RBAC", () => {
  it("blocks employee from admin pages", () => {
    cy.loginAsEmployee();
    cy.visit("/admin/users");
    cy.url().should("not.include", "/admin/users");
    cy.url().should("include", "/employee/dashboard");
  });
});
