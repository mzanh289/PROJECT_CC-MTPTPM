package CCPTMT.DoAn.QuanLyCaLam.controller;

import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.service.ShiftService;
import CCPTMT.DoAn.QuanLyCaLam.service.WorkScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin/schedule")
public class WorkScheduleController {

    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private ShiftService shiftService;

    // ===== ASSIGN SHIFT =====
    @GetMapping("/assign")
    public String assignForm(HttpSession session, Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("users", workScheduleService.getAllEmployees());
        model.addAttribute("shifts", shiftService.getAllShifts());
        model.addAttribute("formAction", "/admin/schedule/assign");
        return "admin/assign-shift";
    }

    @PostMapping("/assign")
    public String assign(@RequestParam Integer userId,
            @RequestParam Integer shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            HttpSession session,
            RedirectAttributes ra) {

        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (userId == null || shiftId == null || workDate == null) {
            ra.addFlashAttribute("error", "Vui lòng chọn đầy đủ nhân viên, ca làm và ngày làm.");
            return "redirect:/admin/schedule/assign";
        }

        try {
            workScheduleService.assignShift(userId, shiftId, workDate);
            ra.addFlashAttribute("success", "Phân ca thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/schedule/assign";
    }

    // ===== MAIN VIEW =====
    @GetMapping
    public String view(
            @RequestParam(defaultValue = "daily") String type,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session,
            Model model) {

        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("sessionUser", sessionUser);

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        String normalizedType = normalizeViewType(type);
        if (!normalizedType.equalsIgnoreCase(type)) {
            model.addAttribute("warning", "Loại hiển thị không hợp lệ, hệ thống đã chuyển về chế độ theo ngày.");
        }

        switch (normalizedType) {
            case "employee":
                return viewByEmployee(userId, model);

            case "weekly":
                return viewByWeek(targetDate, model);

            default:
                return viewByDay(targetDate, model);
        }
    }

    // ===== VIEW BY EMPLOYEE =====
    private String viewByEmployee(Integer userId, Model model) {
        if (userId == null) {
            model.addAttribute("error", "Vui lòng chọn nhân viên để xem lịch làm.");
            model.addAttribute("schedules", Collections.emptyList());
            model.addAttribute("viewType", "employee");
            return "admin/schedule-view";
        }

        List<WorkSchedule> schedules = workScheduleService.getSchedulesByUserId(userId);

        model.addAttribute("schedules", schedules);
        model.addAttribute("viewType", "employee");

        return "admin/schedule-view";
    }

    // ===== VIEW BY DAY =====
    private String viewByDay(LocalDate date, Model model) {
        List<WorkSchedule> schedules = workScheduleService.getSchedulesByDate(date);

        model.addAttribute("targetDate", date);
        model.addAttribute("schedules", schedules);
        model.addAttribute("viewType", "daily");

        return "admin/schedule-view";
    }

    // ===== VIEW BY WEEK =====
    private String viewByWeek(LocalDate date, Model model) {
        LocalDate start = date.with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        List<WorkSchedule> schedules = workScheduleService.getSchedulesByWeek(date);

        // Tạo map ngày -> list schedule
        Map<LocalDate, List<WorkSchedule>> schedulesByDate = new LinkedHashMap<>();
        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            LocalDate day = start.plusDays(i);
            weekDays.add(day);
            List<WorkSchedule> daySchedules = new ArrayList<>();
            for (WorkSchedule ws : schedules) {
                if (ws.getWorkDate().equals(day)) {
                    daySchedules.add(ws);
                }
            }
            schedulesByDate.put(day, daySchedules);
        }

        model.addAttribute("targetDate", date);
        model.addAttribute("weekStart", start);
        model.addAttribute("weekEnd", end);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("schedules", schedules);
        model.addAttribute("schedulesByDate", schedulesByDate);
        model.addAttribute("viewType", "weekly");
        model.addAttribute("weekDays", weekDays);

        return "admin/schedule-view";
    }

    // ===== DELETE =====
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
            HttpSession session,
            RedirectAttributes ra) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        ra.addFlashAttribute("error", "Chức năng xóa phân ca đã bị khóa. Vui lòng dùng Sửa.");

        return "redirect:/admin/schedule";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes ra) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        Optional<WorkSchedule> scheduleOpt = workScheduleService.getScheduleById(id);
        if (scheduleOpt.isEmpty()) {
            ra.addFlashAttribute("error", "Không tìm thấy phân ca để sửa.");
            return "redirect:/admin/schedule";
        }

        WorkSchedule schedule = scheduleOpt.get();
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("users", workScheduleService.getAllEmployees());
        model.addAttribute("shifts", shiftService.getAllShifts());
        model.addAttribute("selectedUserId", schedule.getUser().getUserId());
        model.addAttribute("selectedShiftId", schedule.getShift().getShiftId());
        model.addAttribute("workDate", schedule.getWorkDate());
        model.addAttribute("formAction", "/admin/schedule/" + id + "/edit");
        return "admin/assign-shift";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id,
            @RequestParam Integer userId,
            @RequestParam Integer shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            HttpSession session,
            RedirectAttributes ra) {

        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (userId == null || shiftId == null || workDate == null) {
            ra.addFlashAttribute("error", "Vui lòng chọn đầy đủ nhân viên, ca làm và ngày làm.");
            return "redirect:/admin/schedule/" + id + "/edit";
        }

        try {
            workScheduleService.updateSchedule(id, userId, shiftId, workDate);
            ra.addFlashAttribute("success", "Cập nhật phân ca thành công!");
            return "redirect:/admin/schedule/daily?date=" + workDate;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/schedule/" + id + "/edit";
        }
    }

    @GetMapping("/daily")
    public String viewDaily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session,
            Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("sessionUser", sessionUser);
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return viewByDay(targetDate, model);
    }

    @GetMapping("/weekly")
    public String viewWeekly(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session,
            Model model) {
        SessionUserDto sessionUser = getAdminSessionUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("sessionUser", sessionUser);
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return viewByWeek(targetDate, model);
    }

    private SessionUserDto getAdminSessionUser(HttpSession session) {
        SessionUserDto sessionUser = (SessionUserDto) session.getAttribute(LoginController.SESSION_USER_KEY);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return null;
        }
        return sessionUser;
    }

    private String normalizeViewType(String type) {
        if ("employee".equalsIgnoreCase(type)) {
            return "employee";
        }
        if ("weekly".equalsIgnoreCase(type)) {
            return "weekly";
        }
        return "daily";
    }
}
