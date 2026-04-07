<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="dialogIcon" value="/UI2/icon0.svg" />
<c:url var="searchIcon" value="/UI2/icon1.svg" />
<c:url var="closeIcon" value="/UI2/icon2.svg" />
<c:if test="${showEmployeeSelector}">
  <div class="modal-overlay">
    <div class="modal-wrap">
      <section class="modal-card">
        <header class="section-heading section-heading--topless">
          <div>
            <div class="eyebrow">
              <img src="${dialogIcon}" alt="">
              Chọn nhân viên
            </div>
            <h2 class="page-title page-title--modal">Danh sách nhân viên hoạt động</h2>
            <p class="page-subtitle">
              Chỉ hiển thị tài khoản có thể được phân ca trong bảng <strong>Users</strong>.
              Đã chọn <strong><c:out value="${selectedEmployeeCount}" default="0" /></strong> nhân viên.
            </p>
          </div>
          <a class="button-ghost" href="<c:out value='${closeDialogUrl}' default='#' />">
            <img src="${closeIcon}" alt="">
            Đóng
          </a>
        </header>

        <form action="<c:out value='${filterEmployeesUrl}' default='#' />" method="get" class="modal-body">
          <aside class="filter-panel">
            <div class="toolbar">
              <h3 class="section-title section-title--small">Bộ lọc</h3>
              <button class="button-quiet" type="submit" name="selectAll" value="true">Chọn tất cả</button>
            </div>

            <div class="filter-group">
              <h4>Vai trò</h4>
              <div class="checkbox-list">
                <c:forEach var="option" items="${roleOptions}">
                  <label class="check-row">
                    <input type="checkbox" name="roles" value="${option.value}" <c:if test="${option.selected}">checked</c:if>>
                    <span><c:out value="${option.label}" /></span>
                    <c:if test="${not empty option.count}"><span class="badge"><c:out value="${option.count}" /></span></c:if>
                  </label>
                </c:forEach>
              </div>
            </div>

            <div class="filter-group">
              <h4>Trạng thái</h4>
              <div class="checkbox-list">
                <c:forEach var="option" items="${statusOptions}">
                  <label class="check-row">
                    <input type="checkbox" name="statuses" value="${option.value}" <c:if test="${option.selected}">checked</c:if>>
                    <span><c:out value="${option.label}" /></span>
                  </label>
                </c:forEach>
              </div>
            </div>

            <div class="filter-group">
              <h4>Điều kiện phân ca</h4>
              <div class="checkbox-list">
                <label class="check-row">
                  <input type="checkbox" name="onlyAvailable" value="true" <c:if test="${onlyAvailable}">checked</c:if>>
                  <span>Chỉ hiện nhân viên chưa có ca trong ngày đã chọn</span>
                </label>
                <label class="check-row">
                  <input type="checkbox" name="onlyUsers" value="true" <c:if test="${onlyUsers}">checked</c:if>>
                  <span>Chỉ hiện tài khoản role User</span>
                </label>
              </div>
            </div>
          </aside>

          <section class="employee-column">
            <div class="toolbar">
              <div class="search-field">
                <img class="icon-inline" src="${searchIcon}" alt="">
                <input type="search" name="keyword" value="<c:out value='${keyword}' />" placeholder="Tìm theo tên, email hoặc số điện thoại">
              </div>
              <div class="toolbar-right">
                <span class="badge"><c:out value="${totalEmployeeCount}" default="0" /> kết quả</span>
                <button class="button-ghost" type="submit">Lọc</button>
              </div>
            </div>

            <div class="employee-list">
              <c:choose>
                <c:when test="${empty employees}">
                  <div class="mini-card mini-card--padded"><div class="helper-text">Không có nhân viên phù hợp với bộ lọc hiện tại.</div></div>
                </c:when>
                <c:otherwise>
                  <c:forEach var="employee" items="${employees}">
                    <label class="employee-item">
                      <div class="employee-item__left">
                        <input class="employee-item__check" type="checkbox" name="employeeIds" value="${employee.id}" <c:if test="${employee.selected}">checked</c:if>>
                        <div class="avatar"><c:out value="${employee.initials}" default="NV" /></div>
                        <div>
                          <div class="employee-name"><c:out value="${employee.fullName}" default="Chưa có tên" /></div>
                          <div class="employee-meta">
                            <span><c:out value="${employee.email}" default="Email đang cập nhật" /></span>
                            <span>• <c:out value="${employee.phone}" default="Chưa có SĐT" /></span>
                            <span>• <c:out value="${employee.roleName}" default="User" /></span>
                          </div>
                        </div>
                      </div>
                      <div class="assignment-side">
                        <span class="badge<c:if test='${employee.active}'> badge--success</c:if>"><c:out value="${employee.statusLabel}" default="Đang hoạt động" /></span>
                        <c:if test="${employee.selected}"><span class="badge badge--soft">Đã chọn</span></c:if>
                      </div>
                    </label>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </div>

            <div class="footer-actions">
              <div class="helper-text">Model gợi ý: `employees`, `roleOptions`, `statusOptions`, `selectedEmployeeCount`, `keyword`, `onlyAvailable`.</div>
              <div class="toolbar-right">
                <a class="button-ghost" href="<c:out value='${cancelUrl}' default='#' />">Hủy</a>
                <button class="button" type="submit" formaction="<c:out value='${applySelectionUrl}' default='#' />" formmethod="post">Áp dụng lựa chọn (<c:out value="${selectedEmployeeCount}" default="0" />)</button>
              </div>
            </div>
          </section>
        </form>
      </section>
    </div>
  </div>
</c:if>
