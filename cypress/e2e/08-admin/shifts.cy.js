describe("Admin - Quản lý ca làm", () => {
  it("Mở được danh sách ca làm", () => {
    cy.loginAsAdmin();
    cy.visit("/admin/shifts");

    cy.url().should("include", "/admin/shifts");
    cy.contains("h1", "Danh sách ca làm").should("be.visible");
    cy.get("table.shifts-table").should("be.visible");
  });

  it("Tạo mới ca làm thành công", () => {
    const unique = Date.now();
    const shiftName = `Ca test Cypress ${unique}`;

    cy.loginAsAdmin();
    cy.visit("/admin/shifts/new");

    cy.get("#shiftName").type(shiftName);
    cy.get("#startTime").type("09:00");
    cy.get("#endTime").type("11:00");
    cy.get("form#shiftForm").submit();

    cy.url().should("include", "/admin/shifts");
    cy.contains("td", shiftName).should("be.visible");
  });
});
