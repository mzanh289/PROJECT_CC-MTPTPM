describe("Nhân viên - Chấm công", () => {
  it("Hiển thị trang chấm công với các nút thao tác", () => {
    cy.loginAsEmployee();
    cy.visit("/employee/attendance");

    cy.url().should("include", "/employee/attendance");
    cy.contains("h1", "Chấm công").should("be.visible");
    cy.getByTestId("checkin-btn").should("be.visible");
    cy.getByTestId("checkout-btn").should("be.visible");
  });

  it("Đi tới trang lịch sử chấm công", () => {
    cy.loginAsEmployee();
    cy.visit("/employee/attendance/history");

    cy.url().should("include", "/employee/attendance/history");
    cy.contains("h1", "Lịch sử chấm công").should("be.visible");

    cy.get("body").then(($body) => {
      const hasTable = $body.find("table.history-table").length > 0;
      const hasEmpty = $body.text().includes("Hiện tại bạn chưa có bản ghi chấm công");
      expect(hasTable || hasEmpty).to.eq(true);
    });
  });

  it("Admin bị chuyển hướng khi vào trang chấm công nhân viên", () => {
    cy.loginAsAdmin();
    cy.visit("/employee/attendance");

    cy.url().should("include", "/admin/dashboard");
  });
});
