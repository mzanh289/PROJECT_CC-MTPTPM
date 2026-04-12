package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CCPTMT.DoAn.QuanLyCaLam.dto.AdminAttendanceReportDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.AdminAttendanceReportRowDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Attendance;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.AttendanceStatus;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.repository.AttendanceRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.WorkScheduleRepository;
import CCPTMT.DoAn.QuanLyCaLam.service.AdminReportService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final WorkScheduleRepository workScheduleRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public AdminAttendanceReportDto buildAttendanceReport(String period, LocalDate referenceDate) {
        LocalDate anchorDate = referenceDate != null ? referenceDate : LocalDate.now();
        String normalizedPeriod = normalizePeriod(period);

        LocalDate fromDate;
        LocalDate toDate;
        String periodLabel;

        switch (normalizedPeriod) {
            case "week":
                fromDate = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                toDate = fromDate.plusDays(6);
                periodLabel = "Theo tuần";
                break;
            case "month":
                fromDate = anchorDate.withDayOfMonth(1);
                toDate = anchorDate.withDayOfMonth(anchorDate.lengthOfMonth());
                periodLabel = "Theo tháng";
                break;
            case "day":
            default:
                fromDate = anchorDate;
                toDate = anchorDate;
                periodLabel = "Theo ngày";
                break;
        }

        List<WorkSchedule> schedules = workScheduleRepository.findSchedulesWithUserAndShiftBetween(fromDate, toDate, Role.USER);
        List<Attendance> attendances = attendanceRepository.findByWorkDateBetween(fromDate, toDate);

        Map<String, Attendance> attendanceByUserAndDate = attendances.stream()
                .collect(Collectors.toMap(
                        att -> makeKey(att.getUser().getUserId(), att.getWorkDate()),
                        att -> att,
                        (first, second) -> first,
                        HashMap::new));

        Map<String, RowAccumulator> assignedByUserAndDate = new LinkedHashMap<>();
        for (WorkSchedule schedule : schedules) {
            String key = makeKey(schedule.getUser().getUserId(), schedule.getWorkDate());
            assignedByUserAndDate.computeIfAbsent(key, k -> new RowAccumulator(
                    schedule.getWorkDate(),
                    schedule.getUser().getUserId(),
                    schedule.getUser().getFullName()))
                    .addShift(schedule.getShift().getShiftName());
        }

        Map<LocalDate, DayCounter> dayCounters = initDayCounters(fromDate, toDate);

        List<AdminAttendanceReportRowDto> rows = new ArrayList<>();
        int onTimeCount = 0;
        int lateCount = 0;
        int absentCount = 0;

        for (RowAccumulator accumulator : assignedByUserAndDate.values()) {
            Attendance attendance = attendanceByUserAndDate.get(makeKey(accumulator.userId, accumulator.workDate));
            AttendanceStatus finalStatus = resolveStatus(attendance);
            String statusLabel = toStatusLabel(finalStatus);

            if (finalStatus == AttendanceStatus.TRE) {
                lateCount++;
                dayCounters.get(accumulator.workDate).late++;
            } else if (finalStatus == AttendanceStatus.DI_LAM) {
                onTimeCount++;
                dayCounters.get(accumulator.workDate).onTime++;
            } else {
                absentCount++;
                dayCounters.get(accumulator.workDate).absent++;
            }

            rows.add(AdminAttendanceReportRowDto.builder()
                    .workDate(accumulator.workDate)
                    .userId(accumulator.userId)
                    .fullName(accumulator.fullName)
                    .shiftNames(String.join(", ", accumulator.shiftNames))
                    .checkIn(attendance != null ? attendance.getCheckIn() : null)
                    .checkOut(attendance != null ? attendance.getCheckOut() : null)
                    .status(finalStatus)
                    .statusLabel(statusLabel)
                    .build());
        }

        rows.sort(Comparator.comparing(AdminAttendanceReportRowDto::getWorkDate)
                .thenComparing(AdminAttendanceReportRowDto::getFullName)
                .reversed());

        List<String> chartLabels = new ArrayList<>();
        List<Integer> onTimeSeries = new ArrayList<>();
        List<Integer> lateSeries = new ArrayList<>();
        List<Integer> absentSeries = new ArrayList<>();

        for (Map.Entry<LocalDate, DayCounter> entry : dayCounters.entrySet()) {
            chartLabels.add(entry.getKey().format(DATE_FORMAT));
            onTimeSeries.add(entry.getValue().onTime);
            lateSeries.add(entry.getValue().late);
            absentSeries.add(entry.getValue().absent);
        }

        return AdminAttendanceReportDto.builder()
                .period(normalizedPeriod)
                .referenceDate(anchorDate)
                .fromDate(fromDate)
                .toDate(toDate)
                .periodLabel(periodLabel)
                .totalAssigned(rows.size())
                .onTimeCount(onTimeCount)
                .lateCount(lateCount)
                .absentCount(absentCount)
                .chartLabels(chartLabels)
                .onTimeSeries(onTimeSeries)
                .lateSeries(lateSeries)
                .absentSeries(absentSeries)
                .rows(rows)
                .build();
    }

    @Override
    public byte[] exportAttendanceReportPdf(AdminAttendanceReportDto reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = loadFont(16, true);
            Font bodyFont = loadFont(10, false);
            Font boldBodyFont = loadFont(10, true);

                Paragraph title = new Paragraph("Báo cáo chấm công", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            Paragraph periodInfo = new Paragraph(
                    "Kỳ báo cáo: " + reportData.getPeriodLabel() + " | Từ "
                        + reportData.getFromDate().format(DATE_FORMAT) + " đến "
                            + reportData.getToDate().format(DATE_FORMAT),
                    bodyFont);
            periodInfo.setSpacingAfter(8f);
            document.add(periodInfo);

            Paragraph summary = new Paragraph(
                    "Tổng phân ca: " + reportData.getTotalAssigned()
                        + " | Đi đúng giờ: " + reportData.getOnTimeCount()
                        + " | Đi trễ: " + reportData.getLateCount()
                        + " | Nghỉ bỏ ca: " + reportData.getAbsentCount(),
                    boldBodyFont);
            summary.setSpacingAfter(10f);
            document.add(summary);

            PdfPTable table = new PdfPTable(new float[] { 1.2f, 1.2f, 2.2f, 2.2f, 1.4f, 1.4f, 1.2f });
            table.setWidthPercentage(100f);

            addHeaderCell(table, "Ngày", boldBodyFont);
            addHeaderCell(table, "Mã NV", boldBodyFont);
            addHeaderCell(table, "Nhân viên", boldBodyFont);
            addHeaderCell(table, "Ca làm", boldBodyFont);
            addHeaderCell(table, "Check-in", boldBodyFont);
            addHeaderCell(table, "Check-out", boldBodyFont);
            addHeaderCell(table, "Trạng thái", boldBodyFont);

            for (AdminAttendanceReportRowDto row : reportData.getRows()) {
                addBodyCell(table, row.getWorkDate().format(DATE_FORMAT), bodyFont);
                addBodyCell(table, String.valueOf(row.getUserId()), bodyFont);
                addBodyCell(table, row.getFullName(), bodyFont);
                addBodyCell(table, row.getShiftNames(), bodyFont);
                addBodyCell(table, row.getCheckIn() != null ? row.getCheckIn().toLocalTime().format(TIME_FORMAT) : "-", bodyFont);
                addBodyCell(table, row.getCheckOut() != null ? row.getCheckOut().toLocalTime().format(TIME_FORMAT) : "-", bodyFont);
                addBodyCell(table, row.getStatusLabel(), bodyFont);
            }

            if (reportData.getRows().isEmpty()) {
                PdfPCell noDataCell = new PdfPCell(new Phrase("Không có dữ liệu phân ca trong kỳ báo cáo", bodyFont));
                noDataCell.setColspan(7);
                noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                noDataCell.setPadding(8f);
                table.addCell(noDataCell);
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể xuất PDF báo cáo", ex);
        }
    }

    private Map<LocalDate, DayCounter> initDayCounters(LocalDate fromDate, LocalDate toDate) {
        Map<LocalDate, DayCounter> dayCounters = new LinkedHashMap<>();
        LocalDate cursor = fromDate;
        while (!cursor.isAfter(toDate)) {
            dayCounters.put(cursor, new DayCounter());
            cursor = cursor.plusDays(1);
        }
        return dayCounters;
    }

    private AttendanceStatus resolveStatus(Attendance attendance) {
        if (attendance == null || attendance.getStatus() == null || attendance.getStatus() == AttendanceStatus.NGHI) {
            return AttendanceStatus.NGHI;
        }
        if (attendance.getStatus() == AttendanceStatus.TRE) {
            return AttendanceStatus.TRE;
        }
        return AttendanceStatus.DI_LAM;
    }

    private String toStatusLabel(AttendanceStatus status) {
        if (status == AttendanceStatus.DI_LAM) {
            return "Đi đúng giờ";
        }
        if (status == AttendanceStatus.TRE) {
            return "Đi trễ";
        }
        return "Nghỉ bỏ ca";
    }

    private String normalizePeriod(String period) {
        if (period == null) {
            return "day";
        }

        String candidate = period.trim().toLowerCase(Locale.ROOT);
        if ("week".equals(candidate) || "month".equals(candidate) || "day".equals(candidate)) {
            return candidate;
        }
        return "day";
    }

    private String makeKey(Integer userId, LocalDate date) {
        return userId + "_" + date;
    }

    private Font loadFont(float size, boolean bold) {
        try {
            String fontPath = bold ? "C:/Windows/Fonts/arialbd.ttf" : "C:/Windows/Fonts/arial.ttf";
            if (!Files.exists(Path.of(fontPath))) {
                throw new IllegalStateException("Font not found");
            }
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(baseFont, size, Font.NORMAL);
        } catch (Exception ex) {
            return new Font(bold ? Font.HELVETICA : Font.HELVETICA, size, bold ? Font.BOLD : Font.NORMAL);
        }
    }

    private void addHeaderCell(PdfPTable table, String value, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String value, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private static class RowAccumulator {

        private final LocalDate workDate;
        private final Integer userId;
        private final String fullName;
        private final Set<String> shiftNames = new LinkedHashSet<>();

        private RowAccumulator(LocalDate workDate, Integer userId, String fullName) {
            this.workDate = workDate;
            this.userId = userId;
            this.fullName = fullName;
        }

        private void addShift(String shiftName) {
            shiftNames.add(shiftName);
        }
    }

    private static class DayCounter {

        private int onTime;
        private int late;
        private int absent;
    }
}
