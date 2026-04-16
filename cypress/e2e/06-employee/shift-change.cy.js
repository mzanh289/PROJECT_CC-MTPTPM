describe("Nhân viên - Yêu cầu đổi ca", () => {
  it("Mở được form yêu cầu đổi ca", () => {
    cy.loginAsEmployee();
    cy.visit("/requests/shift-change");

    cy.url().should("include", "/requests/shift-change");
    cy.contains("h1", "Đổi ca làm").should("be.visible");
    cy.get("#workScheduleSelect").should("be.visible");
    cy.get("#targetShiftSelect").should("be.disabled");
    cy.get("#submitBtn").should("be.disabled");
  });

  it("Chọn ca hiện tại sẽ mở khóa lựa chọn ca mới", () => {
    cy.loginAsEmployee();
    cy.visit("/requests/shift-change");

    cy.get("#workScheduleSelect option").its("length").should("be.greaterThan", 1);
    cy.get("#workScheduleSelect").select(1);

    cy.get("#targetShiftSelect").should("not.be.disabled");
    cy.get("#targetShiftSelect option").its("length").should("be.greaterThan", 1);

    cy.get("#targetShiftSelect").select(1);
    cy.get("textarea[name='reason']").type("Test tự động: cần đổi ca do bận việc cá nhân");
    cy.get("#submitBtn").should("not.be.disabled");
  });

  it("Admin không truy cập được form yêu cầu đổi ca", () => {
    cy.loginAsAdmin();
    cy.visit("/requests/shift-change");

    cy.url().should("include", "/admin/dashboard");
  });
});
