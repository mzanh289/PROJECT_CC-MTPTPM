<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="sharedCss" value="/images/style.css" />
<c:url var="assignIcon" value="/UI1/icon0.svg" />
<c:url var="viewIcon" value="/UI1/icon1.svg" />
<c:url var="emptyIcon" value="/UI1/icon2.svg" />
<c:url var="selectIcon" value="/UI1/icon3.svg" />
<c:url var="saveIcon" value="/UI1/icon4.svg" />
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${pageTitle}" default="Quản lý ca và phân ca" /></title>
  <link rel="stylesheet" href="${sharedCss}">
</head>
<body class="jsp-body">
  <div class="schedule-shell">
    <header class="hero-panel">
      <div class="hero-copy">
        <div class="eyebrow">
          <img src="${assignIcon}" alt="">
          Admin Shift Management
        </div>
        <h1 class="page-title"><c:out value="${pageTitle}" default="Quản lý ca làm và phân ca theo ngày" /></h1>
        <p class="page-subtitle"><c:out value="${pageSubtitle}" default="Gom phần CRUD Shifts và WorkSchedules vào cùng một workspace để admin tạo ca, chọn nhân viên, rồi gán ca theo ngày hoặc theo tuần." /></p>
      </div>
      <div class="hero-actions">
        <nav class="tab-nav" aria-label="Schedule tabs">
          <a class="tab-link is-active" href="<c:out value='${assignShiftsUrl}' default='#' />">
            <img src="${assignIcon}" alt="">
            Phân ca
          </a>
          <a class="tab-link" href="<c:out value='${viewSchedulesUrl}' default='#' />">
            <img src="${viewIcon}" alt="">
            Xem lịch đã phân
          </a>
        </nav>
        <a class="button" href="<c:out value='${viewShiftsUrl}' default='#' />">
          <img src="${saveIcon}" alt="">
          Danh sách ca làm
        </a>
      </div>
    </header>

    <section class="surface-card">
      <div class="summary-grid">
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-blue"><img src="${assignIcon}" alt=""></div>
            <div>
              <div class="stat-label">Tổng ca làm</div>
              <div class="stat-value"><c:out value="${dashboard.totalShifts}" default="0" /></div>
            </div>
          </div>
          <div class="stat-meta">Số bản ghi trong bảng `Shifts`.</div>
        </article>
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-green"><img src="${selectIcon}" alt=""></div>
            <div>
              <div class="stat-label">Nhân viên đã chọn</div>
              <div class="stat-value"><c:out value="${selectedEmployeeCount}" default="0" /></div>
            </div>
          </div>
          <div class="stat-meta">Nhân viên đang chuẩn bị gán vào `WorkSchedules`.</div>
        </article>
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-amber"><img src="${viewIcon}" alt=""></div>
            <div>
              <div class="stat-label">Ca hôm nay</div>
              <div class="stat-value"><c:out value="${dashboard.todayAssignments}" default="0" /></div>
            </div>
          </div>
          <div class="stat-meta">Tổng số phân ca trong ngày đang chọn.</div>
        </article>
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-slate"><img src="${saveIcon}" alt=""></div>
            <div>
              <div class="stat-label">Lịch tuần này</div>
              <div class="stat-value"><c:out value="${dashboard.weekAssignments}" default="0" /></div>
            </div>
          </div>
          <div class="stat-meta">Dùng để kiểm tra nhanh khối lượng phân ca.</div>
        </article>
      </div>
    </section>

    <section class="surface-card">
      <div class="section-heading">
        <div>
          <h2 class="section-title">Quản lý ca làm</h2>
          <p class="section-note">CRUD chuẩn cho bảng `Shifts`: tạo ca, cập nhật giờ bắt đầu hoặc kết thúc, và xóa ca khi không còn sử dụng.</p>
        </div>
        <span class="badge badge--soft"><c:out value="${shiftCount}" default="0" /> ca đang có</span>
      </div>

      <div class="content-split">
        <section class="surface-card surface-card--nested">
          <div class="section-heading">
            <div>
              <h3 class="section-title">Danh sách ca</h3>
              <p class="section-note">Map trực tiếp từ `List&lt;Shift&gt;` gồm `shiftId`, `shiftName`, `startTime`, `endTime`.</p>
            </div>
          </div>

          <div class="table-wrap">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Mã ca</th>
                  <th>Tên ca</th>
                  <th>Bắt đầu</th>
                  <th>Kết thúc</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <c:choose>
                  <c:when test="${empty shifts}">
                    <tr>
                      <td colspan="5"><div class="helper-text helper-text--center">Chưa có ca làm nào. Hãy tạo ca sáng, chiều hoặc tối để bắt đầu.</div></td>
                    </tr>
                  </c:when>
                  <c:otherwise>
                    <c:forEach var="shift" items="${shifts}">
                      <tr>
                        <td>#<c:out value="${shift.shiftId}" default="0" /></td>
                        <td><strong><c:out value="${shift.shiftName}" default="Chưa đặt tên" /></strong></td>
                        <td><c:out value="${shift.startTime}" default="--:--" /></td>
                        <td><c:out value="${shift.endTime}" default="--:--" /></td>
                        <td>
                          <div class="table-actions">
                            <a class="button-quiet" href="<c:out value='${shift.editUrl}' default='#' />">Sửa</a>
                            <a class="button-quiet button-quiet--danger" href="<c:out value='${shift.deleteUrl}' default='#' />">Xóa</a>
                          </div>
                        </td>
                      </tr>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
              </tbody>
            </table>
          </div>
        </section>

        <section class="surface-card surface-card--nested">
          <div class="section-heading">
            <div>
              <h3 class="section-title">Thêm hoặc cập nhật ca</h3>
              <p class="section-note">Form dùng chung cho create/update Shift, chỉ cần bind `shiftId`, `shiftName`, `startTime`, `endTime`.</p>
            </div>
          </div>

          <form action="<c:out value='${saveShiftUrl}' default='#' />" method="post" class="stack-list">
            <input type="hidden" name="shiftId" value="<c:out value='${shiftForm.shiftId}' />">
            <label class="field-block">
              <span class="field-label">Tên ca</span>
              <span class="field">
                <input type="text" name="shiftName" value="<c:out value='${shiftForm.shiftName}' />" placeholder="Ví dụ: Ca sáng">
              </span>
            </label>
            <div class="form-grid">
              <label class="field-block">
                <span class="field-label">Giờ bắt đầu</span>
                <span class="field"><input type="time" name="startTime" value="<c:out value='${shiftForm.startTime}' />"></span>
              </label>
              <label class="field-block">
                <span class="field-label">Giờ kết thúc</span>
                <span class="field"><input type="time" name="endTime" value="<c:out value='${shiftForm.endTime}' />"></span>
              </label>
            </div>
            <div class="footer-actions">
              <div class="helper-text">Nên validate `EndTime` lớn hơn `StartTime` trước khi lưu.</div>
              <div class="toolbar-right">
                <a class="button-ghost" href="<c:out value='${resetShiftFormUrl}' default='#' />">Làm mới</a>
                <button class="button" type="submit">
                  <img src="${saveIcon}" alt="">
                  Lưu ca làm
                </button>
              </div>
            </div>
          </form>
        </section>
      </div>
    </section>

    <section class="surface-card">
      <div class="section-heading">
        <div>
          <h2 class="section-title">Workspace phân ca</h2>
          <p class="section-note">Phục vụ bảng `WorkSchedules`: chọn nhân viên, chọn ngày làm, chọn ca và lưu phân công.</p>
        </div>
        <span class="badge badge--soft"><c:out value="${selectedEmployeeCount}" default="0" /> nhân viên đã chọn</span>
      </div>
      <c:choose>
        <c:when test="${empty selectedEmployees}">
          <div class="empty-state">
            <div>
              <div class="empty-state__icon">
                <img src="${emptyIcon}" alt="">
              </div>
              <h2>Chưa chọn nhân viên để phân ca</h2>
              <p>Admin cần chọn nhân viên đang hoạt động trước, sau đó mới gán ca theo đúng `UserID`, `ShiftID` và `WorkDate`.</p>
              <a class="button" href="<c:out value='${openSelectorUrl}' default='#' />">
                <img src="${selectIcon}" alt="">
                Chọn nhân viên
              </a>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="section-heading">
            <div>
              <h3 class="section-title">Nhân viên đã chọn</h3>
              <p class="section-note">Nên render từ bảng `Users`, ưu tiên role `User` và status đang hoạt động.</p>
            </div>
            <a class="button-ghost" href="<c:out value='${openSelectorUrl}' default='#' />">
              <img src="${selectIcon}" alt="">
              Chỉnh danh sách
            </a>
          </div>

          <div class="employee-chip-grid">
            <c:forEach var="employee" items="${selectedEmployees}">
              <article class="person-card">
                <div class="person-main">
                  <div class="avatar"><c:out value="${employee.initials}" default="NV" /></div>
                  <div class="person-text">
                    <div class="person-name"><c:out value="${employee.fullName}" default="Chưa có tên" /></div>
                    <div class="person-meta">
                      <c:out value="${employee.email}" default="Email đang cập nhật" />
                      <c:if test="${not empty employee.phone}"> • <c:out value="${employee.phone}" /></c:if>
                    </div>
                  </div>
                </div>
                <span class="badge badge--success"><c:out value="${employee.roleName}" default="User" /></span>
              </article>
            </c:forEach>
          </div>

          <div class="content-split content-split--top">
            <section class="surface-card surface-card--nested">
              <div class="section-heading">
                <div>
                  <h3 class="section-title">Tạo bản ghi phân ca</h3>
                  <p class="section-note">Mỗi lần lưu sẽ tạo một hoặc nhiều bản ghi trong `WorkSchedules` theo danh sách nhân viên đã chọn.</p>
                </div>
              </div>

              <form action="<c:out value='${saveScheduleUrl}' default='#' />" method="post" class="stack-list">
                <div class="form-grid form-grid--three">
                  <label class="field-block">
                    <span class="field-label">Ngày làm việc</span>
                    <span class="field"><input type="date" name="workDate" value="<c:out value='${scheduleForm.workDate}' />"></span>
                  </label>
                  <label class="field-block">
                    <span class="field-label">Ca làm</span>
                    <span class="field">
                      <select name="shiftId">
                        <option value="">Chọn ca làm</option>
                        <c:forEach var="shift" items="${shiftOptions}">
                          <option value="${shift.value}" <c:if test="${shift.selected}">selected</c:if>><c:out value="${shift.label}" /></option>
                        </c:forEach>
                      </select>
                    </span>
                  </label>
                  <label class="field-block">
                    <span class="field-label">Kiểu hiển thị lịch</span>
                    <span class="field">
                      <select name="viewMode">
                        <option value="week">Theo tuần</option>
                        <option value="day" <c:if test="${scheduleForm.viewMode eq 'day'}">selected</c:if>>Theo ngày</option>
                      </select>
                    </span>
                  </label>
                </div>

                <div class="selected-user-list">
                  <c:forEach var="employee" items="${selectedEmployees}">
                    <label class="selected-user-pill">
                      <input type="checkbox" name="userIds" value="${employee.id}" checked>
                      <span><c:out value="${employee.fullName}" /></span>
                    </label>
                  </c:forEach>
                </div>

                <label class="field-block">
                  <span class="field-label">Ghi chú cho admin</span>
                  <span class="field">
                    <textarea name="note" placeholder="Ví dụ: ưu tiên nhân viên A vì đã đổi ca với nhân viên B"><c:out value="${scheduleForm.note}" /></textarea>
                  </span>
                </label>

                <div class="footer-actions">
                  <div class="helper-text">Nên chặn lưu khi một nhân viên đã có ca khác trong cùng `WorkDate`.</div>
                  <button class="button" type="submit">
                    <img src="${saveIcon}" alt="">
                    Lưu phân ca
                  </button>
                </div>
              </form>
            </section>

            <section class="surface-card surface-card--nested">
              <div class="section-heading">
                <div>
                  <h3 class="section-title">Lịch dự kiến</h3>
                  <p class="section-note">Preview nhanh lịch đã phân để admin soát trùng ca, thiếu người hoặc sai ngày.</p>
                </div>
              </div>

              <form action="<c:out value='${filterSchedulePreviewUrl}' default='#' />" method="get" class="toolbar">
                <div class="toolbar-left">
                  <label class="field"><input type="date" name="fromDate" value="<c:out value='${filter.fromDate}' />"></label>
                  <label class="field"><input type="date" name="toDate" value="<c:out value='${filter.toDate}' />"></label>
                </div>
                <div class="toolbar-right">
                  <button class="button-ghost" type="submit">Xem lịch</button>
                </div>
              </form>

              <div class="list-layout">
                <c:choose>
                  <c:when test="${empty scheduledAssignments}">
                    <div class="mini-card mini-card--padded"><div class="helper-text">Chưa có lịch nào trong khoảng đang chọn.</div></div>
                  </c:when>
                  <c:otherwise>
                    <c:forEach var="assignment" items="${scheduledAssignments}">
                      <article class="assignment-item">
                        <div class="assignment-main">
                          <div class="avatar"><c:out value="${assignment.initials}" default="NV" /></div>
                          <div class="assignment-copy">
                            <div class="assignment-name"><c:out value="${assignment.employeeName}" default="Nhân viên" /></div>
                            <div class="meta-stack">
                              <span><c:out value="${assignment.workDate}" default="Chưa có ngày" /></span>
                              <span>• <c:out value="${assignment.shiftName}" default="Chưa có ca" /></span>
                              <span>• <c:out value="${assignment.timeLabel}" default="Chưa có giờ" /></span>
                            </div>
                          </div>
                        </div>
                        <div class="assignment-side">
                          <a class="button-quiet" href="<c:out value='${assignment.editUrl}' default='#' />">Sửa</a>
                          <a class="button-quiet button-quiet--danger" href="<c:out value='${assignment.deleteUrl}' default='#' />">Hủy ca</a>
                        </div>
                      </article>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
              </div>
            </section>
          </div>

          <section class="surface-card surface-card--nested">
            <div class="section-heading">
              <div>
                <h3 class="section-title">Lưu ý nghiệp vụ</h3>
                <p class="section-note">Các rule dưới đây bám sát yêu cầu demo hệ thống chấm công và phân quyền.</p>
              </div>
            </div>
            <div class="rule-grid">
              <article class="mini-card mini-card--padded">
                <h4 class="rule-title">Admin</h4>
                <p class="helper-text">Có quyền CRUD `Shifts`, CRUD `WorkSchedules`, xem lịch theo ngày hoặc tuần, và mở danh sách chấm công để đối chiếu.</p>
              </article>
              <article class="mini-card mini-card--padded">
                <h4 class="rule-title">Nhân viên</h4>
                <p class="helper-text">Chỉ xem ca làm của mình, check-in hoặc check-out theo ca được gán, không tự sửa `WorkSchedules`.</p>
              </article>
              <article class="mini-card mini-card--padded">
                <h4 class="rule-title">Ràng buộc dữ liệu</h4>
                <p class="helper-text">Một nhân viên chỉ nên có một ca mỗi ngày, đúng với unique key `UserID + WorkDate` trong entity `WorkSchedule` hiện tại.</p>
              </article>
            </div>
          </section>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
  <jsp:include page="select-employees.jsp" />
</body>
</html>
