describe("Nhân viên - Quản lý yêu cầu", () => {
  it("Tạo yêu cầu nghỉ phép thành công", () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    const dayAfter = new Date();
    dayAfter.setDate(dayAfter.getDate() + 2);

    const toDateInput = (d) => d.toISOString().slice(0, 10);

    cy.loginAsEmployee();
    cy.visit("/requests/create");

    cy.getByTestId("request-from-date").type(toDateInput(tomorrow));
    cy.getByTestId("request-to-date").type(toDateInput(dayAfter));
    cy.getByTestId("request-reason").type("Cypress E2E leave request");
    cy.getByTestId("request-submit").click();

    cy.url().should("include", "/requests/my");
    cy.getByTestId("request-success").should("be.visible");
    cy.getByTestId("request-table").contains("Cypress E2E leave request").should("be.visible");
  });
});
