describe("Xác thực đăng nhập", () => {
  it("Đăng nhập thành công với tài khoản admin", () => {
    cy.loginAsAdmin();
    cy.contains("h2", "Xin chao", { matchCase: false }).should("be.visible");
  });

  it("Hiển thị lỗi khi nhập sai thông tin đăng nhập", () => {
    cy.login("admin@company.com", "wrong-password");
    cy.url().should("include", "/login");
    cy.getByTestId("login-error").should("be.visible");
  });

  it("Đăng xuất thành công với vai trò nhân viên", () => {
    cy.loginAsEmployee();
    cy.getByTestId("logout-button").click();
    cy.url().should("include", "/login");
  });
});
