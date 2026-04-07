<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="sharedCss" value="/images/style.css" />
<c:url var="assignIcon" value="/UI3/icon0.svg" />
<c:url var="viewIcon" value="/UI3/icon1.svg" />
<c:url var="employeeStatIcon" value="/UI3/icon2.svg" />
<c:url var="shiftStatIcon" value="/UI3/icon3.svg" />
<c:url var="coverageStatIcon" value="/UI3/icon4.svg" />
<c:url var="avgStatIcon" value="/UI3/icon5.svg" />
<c:url var="distributionIcon" value="/UI3/icon6.svg" />
<c:url var="dailyIcon" value="/UI3/icon7.svg" />
<c:url var="listIcon" value="/UI3/icon8.svg" />
<c:url var="exportIcon" value="/UI3/icon9.svg" />
<c:url var="searchIcon" value="/UI3/icon12.svg" />
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${pageTitle}" default="Lịch phân ca" /></title>
  <link rel="stylesheet" href="${sharedCss}">
</head>
<body class="jsp-body">
  <div class="schedule-shell">
    <header class="hero-panel">
      <div class="hero-copy">
        <div class="eyebrow">
          <img src="${viewIcon}" alt="">
          WorkSchedule Overview
        </div>
        <h1 class="page-title"><c:out value="${pageTitle}" default="Xem lịch đã phân" /></h1>
        <p class="page-subtitle"><c:out value="${pageSubtitle}" default="Một giao diện duy nhất cho cả list view và grid view để admin xem, lọc, sửa hoặc hủy phân ca theo ngày hoặc theo tuần." /></p>
      </div>
      <div class="hero-actions">
        <nav class="tab-nav">
          <a class="tab-link" href="<c:out value='${assignShiftsUrl}' default='#' />">
            <img src="${assignIcon}" alt="">
            Phân ca
          </a>
          <a class="tab-link is-active" href="<c:out value='${viewSchedulesUrl}' default='#' />">
            <img src="${viewIcon}" alt="">
            Xem lịch đã phân
          </a>
        </nav>
      </div>
    </header>

    <section class="surface-card">
      <div class="summary-grid">
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-blue"><img src="${employeeStatIcon}" alt=""></div>
            <div>
              <div class="stat-label">Nhân viên có lịch</div>
              <div class="stat-value"><c:out value="${summary.totalEmployees}" default="0" /></div>
            </div>
          </div>
          <div class="stat-meta">Số nhân viên xuất hiện trong khoảng lọc hiện tại.</div>
        </article>
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-amber"><img src="${shiftStatIcon}" alt=""></div>
            <div>
              <div class="stat-label">Tổng phân ca</div>
              <div class="stat-value"><c:out value="${summary.totalSchedules}" default="0" /></div>
            </div>
          </div>
          <div class="stat-meta">Tổng số bản ghi `WorkSchedules` đang hiển thị.</div>
        </article>
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-green"><img src="${coverageStatIcon}" alt=""></div>
            <div>
              <div class="stat-label">Số ngày có ca</div>
              <div class="stat-value"><c:out value="${summary.coverageDays}" default="0" /></div>
            </div>
          </div>
          <div class="stat-meta">Ngày có ít nhất một nhân viên được phân công.</div>
        </article>
        <article class="stat-card">
          <div class="stat-head">
            <div class="stat-icon icon-slate"><img src="${avgStatIcon}" alt=""></div>
            <div>
              <div class="stat-label">Trung bình mỗi ngày</div>
              <div class="stat-value"><c:out value="${summary.averagePerDay}" default="0.0" /></div>
            </div>
          </div>
          <div class="stat-meta">Dùng để theo dõi tải phân ca trong tuần.</div>
        </article>
      </div>
    </section>

    <section class="surface-card">
      <div class="split-grid">
        <article class="chart-card">
          <h2 class="chart-title"><img class="icon-inline" src="${distributionIcon}" alt=""> Phân bố ca làm</h2>
          <div class="distribution-list">
            <c:forEach var="item" items="${shiftTypeDistribution}">
              <div class="distribution-item">
                <div class="distribution-row">
                  <span><c:out value="${item.label}" /></span>
                  <strong><c:out value="${item.percentage}" default="0" />%</strong>
                </div>
                <div class="progress"><span style="width:<c:out value='${item.percentage}' default='0' />%"></span></div>
              </div>
            </c:forEach>
          </div>
        </article>
        <article class="chart-card">
          <h2 class="chart-title"><img class="icon-inline" src="${dailyIcon}" alt=""> Tổng phân ca theo ngày</h2>
          <div class="daily-bars">
            <c:forEach var="item" items="${dailyAssignmentStats}">
              <div class="daily-bar__row">
                <div class="daily-bar__label"><c:out value="${item.label}" /></div>
                <div class="daily-bar__track"><span style="width:<c:out value='${item.percentOfPeak}' default='0' />%"></span></div>
                <div class="daily-bar__value"><c:out value="${item.count}" default="0" /></div>
              </div>
            </c:forEach>
          </div>
        </article>
      </div>
    </section>

    <section class="surface-card">
      <div class="section-heading">
        <div>
          <div class="badge badge--soft">
            <img class="icon-inline" src="${listIcon}" alt="">
            <c:out value="${viewModeLabel}" default="List / Grid View" />
          </div>
          <h2 class="section-title">Lịch phân ca theo bộ lọc</h2>
          <p class="section-note"><c:out value="${rangeLabel}" default="Chưa chọn khoảng thời gian" /></p>
        </div>
        <a class="button-ghost" href="<c:out value='${exportUrl}' default='#' />">
          <img src="${exportIcon}" alt="">
          Xuất dữ liệu
        </a>
      </div>

      <c:set var="listButtonClass" value="button-ghost" />
      <c:if test="${viewMode ne 'grid'}"><c:set var="listButtonClass" value="button-ghost is-active" /></c:if>
      <c:set var="gridButtonClass" value="button-ghost" />
      <c:if test="${viewMode eq 'grid'}"><c:set var="gridButtonClass" value="button-ghost is-active" /></c:if>

      <form action="<c:out value='${filterActionUrl}' default='#' />" method="get" class="toolbar">
        <div class="toolbar-left">
          <label class="field"><input type="date" name="fromDate" value="<c:out value='${filter.fromDate}' />"></label>
          <label class="field"><input type="date" name="toDate" value="<c:out value='${filter.toDate}' />"></label>
          <label class="field">
            <select name="shiftId">
              <option value="">Tất cả ca</option>
              <c:forEach var="option" items="${shiftOptions}">
                <option value="${option.value}" <c:if test="${option.selected}">selected</c:if>><c:out value="${option.label}" /></option>
              </c:forEach>
            </select>
          </label>
        </div>
        <div class="toolbar-right">
          <div class="search-field">
            <img class="icon-inline" src="${searchIcon}" alt="">
            <input type="search" name="keyword" value="<c:out value='${keyword}' />" placeholder="Tìm nhân viên theo tên hoặc email">
          </div>
          <div class="view-switch">
            <button class="${listButtonClass}" type="submit" name="viewMode" value="list">List</button>
            <button class="${gridButtonClass}" type="submit" name="viewMode" value="grid">Grid</button>
          </div>
          <button class="button" type="submit">Áp dụng</button>
        </div>
      </form>

      <c:choose>
        <c:when test="${viewMode eq 'grid'}">
          <div class="grid-board">
            <table class="grid-table">
              <thead>
                <tr>
                  <th>Nhân viên</th>
                  <c:forEach var="day" items="${weekDays}">
                    <th>
                      <div class="grid-head__day"><c:out value="${day.dayLabel}" /></div>
                      <div class="grid-head__date"><c:out value="${day.dateLabel}" /></div>
                    </th>
                  </c:forEach>
                </tr>
              </thead>
              <tbody>
                <c:choose>
                  <c:when test="${empty gridRows}">
                    <tr><td colspan="8"><div class="helper-text helper-text--center">Không có dữ liệu phân ca để hiển thị dạng lưới.</div></td></tr>
                  </c:when>
                  <c:otherwise>
                    <c:forEach var="row" items="${gridRows}">
                      <tr>
                        <td>
                          <div class="grid-employee">
                            <div class="avatar"><c:out value="${row.initials}" default="NV" /></div>
                            <div>
                              <div class="employee-name"><c:out value="${row.employeeName}" default="Nhân viên" /></div>
                              <div class="employee-meta"><c:out value="${row.roleName}" default="User" /></div>
                            </div>
                          </div>
                        </td>
                        <c:forEach var="cell" items="${row.cells}">
                          <td>
                            <c:choose>
                              <c:when test="${empty cell.items}">
                                <div class="cell-empty">Trống</div>
                              </c:when>
                              <c:otherwise>
                                <div class="cell-stack">
                                  <c:forEach var="item" items="${cell.items}">
                                    <div class="shift-pill">
                                      <div class="shift-pill__name"><c:out value="${item.name}" default="Ca làm" /></div>
                                      <div class="shift-pill__time"><c:out value="${item.timeLabel}" default="Đang cập nhật giờ" /></div>
                                    </div>
                                  </c:forEach>
                                </div>
                              </c:otherwise>
                            </c:choose>
                          </td>
                        </c:forEach>
                      </tr>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise>
          <div class="list-layout">
            <c:choose>
              <c:when test="${empty assignments}">
                <div class="mini-card mini-card--padded"><div class="helper-text">Không tìm thấy bản ghi phân ca theo bộ lọc hiện tại.</div></div>
              </c:when>
              <c:otherwise>
                <c:forEach var="assignment" items="${assignments}">
                  <article class="assignment-item">
                    <div class="assignment-main">
                      <div class="avatar"><c:out value="${assignment.initials}" default="NV" /></div>
                      <div class="assignment-copy">
                        <div class="assignment-name"><c:out value="${assignment.employeeName}" default="Nhân viên" /></div>
                        <div class="meta-stack">
                          <span><c:out value="${assignment.email}" default="Email đang cập nhật" /></span>
                          <span>• <c:out value="${assignment.workDate}" default="Chưa có ngày làm" /></span>
                          <span>• <c:out value="${assignment.timeLabel}" default="Chưa có giờ ca" /></span>
                        </div>
                      </div>
                    </div>
                    <div class="assignment-side">
                      <span class="badge badge--success"><c:out value="${assignment.shiftName}" default="Ca làm" /></span>
                      <a class="button-quiet" href="<c:out value='${assignment.editUrl}' default='#' />">Sửa</a>
                      <a class="button-quiet button-quiet--danger" href="<c:out value='${assignment.deleteUrl}' default='#' />">Xóa</a>
                    </div>
                  </article>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
</body>
</html>
