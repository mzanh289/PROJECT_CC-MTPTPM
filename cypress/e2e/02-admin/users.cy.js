describe("Admin user management", () => {
  it("creates a new user and marks it inactive", () => {
    const unique = Date.now();
    const email = `cypress.user.${unique}@example.com`;

    cy.loginAsAdmin();
    cy.visit("/admin/users");

    cy.getByTestId("create-user-link").click();
    cy.url().should("include", "/admin/users/new");

    cy.getByTestId("user-fullname").type("Cypress User");
    cy.getByTestId("user-email").type(email);
    cy.getByTestId("user-phone").type("0901000200");
    cy.getByTestId("user-password").type("123456", { log: false });
    cy.getByTestId("user-save").click();

    cy.url().should("include", "/admin/users");
    cy.contains("td", email).should("be.visible");

    cy.on("window:confirm", () => true);
    cy.contains("tr", email).within(() => {
      cy.contains("button", "Xóa").click();
    });

    cy.contains("tr", email).within(() => {
      cy.contains("Inactive").should("be.visible");
    });
  });
});
