describe("Admin - Quản lý yêu cầu", () => {
  it("Hiển thị danh sách yêu cầu", () => {
    cy.loginAsAdmin();
    cy.visit("/admin/requests");

    cy.url().should("include", "/admin/requests");
    cy.contains("h2", "Danh sách yêu cầu").should("be.visible");
    cy.get("table.table").should("be.visible");
  });

  it("Xem được modal chi tiết khi bấm vào một dòng yêu cầu", () => {
    cy.loginAsAdmin();
    cy.visit("/admin/requests");

    cy.get("body").then(($body) => {
      const rowCount = $body.find("tr.request-row").length;
      if (rowCount > 0) {
        cy.get("tr.request-row").first().click();
        cy.get("#requestDetailModal").should("have.class", "active");
        cy.get("#modalRequestId").should("contain.text", "#");
        cy.get("#modalCloseBtn").click();
        cy.get("#requestDetailModal").should("not.have.class", "active");
      }
    });
  });

  it("Nhân viên bị chặn khi vào trang quản lý yêu cầu", () => {
    cy.loginAsEmployee();
    cy.visit("/admin/requests");

    cy.url().should("include", "/employee/dashboard");
  });
});
