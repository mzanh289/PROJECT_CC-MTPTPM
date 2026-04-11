package CCPTMT.DoAn.QuanLyCaLam.controller;

import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.service.ShiftService;
import CCPTMT.DoAn.QuanLyCaLam.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
    public String assignForm(Model model) {
        model.addAttribute("users", workScheduleService.getAllEmployees());
        model.addAttribute("shifts", shiftService.getAllShifts());
        return "admin/assign-shift";
    }

    @PostMapping("/assign")
    public String assign(@RequestParam Integer userId,
            @RequestParam Integer shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            RedirectAttributes ra) {

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
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        switch (type) {
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
        for (int i = 0; i <= 6; i++) {
            LocalDate day = start.plusDays(i);
            List<WorkSchedule> daySchedules = new ArrayList<>();
            for (WorkSchedule ws : schedules) {
                if (ws.getWorkDate().equals(day)) {
                    daySchedules.add(ws);
                }
            }
            schedulesByDate.put(day, daySchedules);
        }

        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDays.add(start.plusDays(i));
        }
        model.addAttribute("weekDays", weekDays);

        model.addAttribute("targetDate", date);
        model.addAttribute("weekStart", start);
        model.addAttribute("weekEnd", end);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("schedules", schedules);
        model.addAttribute("schedulesByDate", schedulesByDate);
        model.addAttribute("viewType", "weekly");

        return "admin/schedule-view";
    }

    // ===== DELETE =====
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
            RedirectAttributes ra) {

        try {
            workScheduleService.deleteSchedule(id);
            ra.addFlashAttribute("success", "Xóa thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Xóa thất bại!");
        }

        return "redirect:/admin/schedule";
    }

    @GetMapping("/daily")
    public String viewDaily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return viewByDay(targetDate, model);
    }

    @GetMapping("/weekly")
    public String viewWeekly(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return viewByWeek(targetDate, model);
    }
}
