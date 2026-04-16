describe("Phân quyền truy cập", () => {
  it("Chặn nhân viên truy cập trang quản trị", () => {
    cy.loginAsEmployee();
    cy.visit("/admin/users");
    cy.url().should("not.include", "/admin/users");
    cy.url().should("include", "/employee/dashboard");
  });
});
