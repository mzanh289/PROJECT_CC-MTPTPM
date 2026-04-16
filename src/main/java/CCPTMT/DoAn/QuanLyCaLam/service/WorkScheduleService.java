package CCPTMT.DoAn.QuanLyCaLam.service;

import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import CCPTMT.DoAn.QuanLyCaLam.repository.ShiftRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.WorkScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkScheduleService {

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    public List<WorkSchedule> getAllSchedules() {
        return workScheduleRepository.findAll();
    }

    public Optional<WorkSchedule> getScheduleById(Integer id) {
        return workScheduleRepository.findById(id);
    }

    public List<WorkSchedule> getSchedulesByUserId(Integer userId) {
        return workScheduleRepository.findByUserUserId(userId);
    }

    public List<WorkSchedule> getSchedulesByDateRange(LocalDate fromDate, LocalDate toDate) {
        return workScheduleRepository.findByWorkDateBetween(fromDate, toDate);
    }

    public List<WorkSchedule> getSchedulesByDate(LocalDate date) {
        return workScheduleRepository.findByWorkDateBetween(date, date);
    }

    public List<WorkSchedule> getSchedulesByWeek(LocalDate date) {
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        return workScheduleRepository.findByWorkDateBetween(weekStart, weekEnd);
    }

    @Transactional
    public WorkSchedule assignShift(Integer userId, Integer shiftId, LocalDate workDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
        if (user.getRole() != Role.USER) {
            throw new IllegalArgumentException("Chỉ được phân ca cho nhân viên (USER)");
        }
        if (!Boolean.TRUE.equals(user.getStatus())) {
            throw new IllegalArgumentException("Nhân viên đã ngừng hoạt động, không thể phân ca.");
        }
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm"));

        validateScheduleDateTimeNotPast(workDate, shift);

        // Kiểm tra trùng ca: cùng nhân viên + cùng ngày + cùng ca
        if (workScheduleRepository.existsByUserUserIdAndWorkDateAndShiftShiftId(userId, workDate, shiftId)) {
            throw new IllegalArgumentException(
                    "Nhân viên đã được phân ca '" + shift.getShiftName() + "' vào ngày này rồi.");
        }

        // Kiểm tra trùng giờ: ca mới có bị chồng giờ với ca hiện tại không
        List<WorkSchedule> existingOnDay = workScheduleRepository
                .findByUserUserIdAndWorkDate(userId, workDate);

        LocalTime newStart = shift.getStartTime();
        LocalTime newEnd = shift.getEndTime();

        for (WorkSchedule existing : existingOnDay) {
            LocalTime exStart = existing.getShift().getStartTime();
            LocalTime exEnd = existing.getShift().getEndTime();

            // Chồng giờ nếu: newStart < exEnd && newEnd > exStart
            boolean overlaps = newStart.isBefore(exEnd) && newEnd.isAfter(exStart);
            if (overlaps) {
                throw new IllegalArgumentException(
                        "Ca '" + shift.getShiftName() + "' (" + newStart + "-" + newEnd + ")" +
                                " bị trùng giờ với ca '" + existing.getShift().getShiftName() +
                                "' (" + exStart + "-" + exEnd + ").");
            }
        }

        WorkSchedule schedule = WorkSchedule.builder()
                .user(user)
                .shift(shift)
                .workDate(workDate)
                .build();
        try {
            return workScheduleRepository.save(schedule);
        } catch (DataIntegrityViolationException e) {
            // Fallback nếu DB constraint cũ vẫn tồn tại hoặc race condition
            throw new IllegalArgumentException(
                    "Nhân viên đã được phân ca '" + shift.getShiftName() +
                            "' vào ngày " + workDate + " rồi.");
        }
    }

    @Transactional
    public void deleteSchedule(Integer id) {
        workScheduleRepository.deleteById(id);
    }

    @Transactional
    public WorkSchedule updateSchedule(Integer scheduleId, Integer userId, Integer shiftId, LocalDate workDate) {
        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phân ca"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
        if (user.getRole() != Role.USER) {
            throw new IllegalArgumentException("Chỉ được phân ca cho nhân viên (USER)");
        }
        if (!Boolean.TRUE.equals(user.getStatus())) {
            throw new IllegalArgumentException("Nhân viên đã ngừng hoạt động, không thể phân ca.");
        }
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca làm"));

        validateScheduleDateTimeNotPast(workDate, shift);

        Optional<WorkSchedule> duplicateSchedule = workScheduleRepository
                .findByUserUserIdAndWorkDateAndShiftShiftId(userId, workDate, shiftId);
        if (duplicateSchedule.isPresent() && !duplicateSchedule.get().getScheduleId().equals(scheduleId)) {
            throw new IllegalArgumentException(
                    "Nhân viên đã được phân ca '" + shift.getShiftName() + "' vào ngày này rồi.");
        }

        List<WorkSchedule> existingOnDay = workScheduleRepository.findByUserUserIdAndWorkDate(userId, workDate);
        LocalTime newStart = shift.getStartTime();
        LocalTime newEnd = shift.getEndTime();

        for (WorkSchedule existing : existingOnDay) {
            if (existing.getScheduleId().equals(scheduleId)) {
                continue;
            }
            LocalTime exStart = existing.getShift().getStartTime();
            LocalTime exEnd = existing.getShift().getEndTime();
            boolean overlaps = newStart.isBefore(exEnd) && newEnd.isAfter(exStart);
            if (overlaps) {
                throw new IllegalArgumentException(
                        "Ca '" + shift.getShiftName() + "' (" + newStart + "-" + newEnd + ")"
                                + " bị trùng giờ với ca '" + existing.getShift().getShiftName()
                                + "' (" + exStart + "-" + exEnd + ").");
            }
        }

        schedule.setUser(user);
        schedule.setShift(shift);
        schedule.setWorkDate(workDate);
        return workScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteScheduleByUserAndDate(Integer userId, LocalDate workDate) {
        List<WorkSchedule> schedules = workScheduleRepository.findByUserUserIdAndWorkDate(userId, workDate);
        workScheduleRepository.deleteAll(schedules);
    }

    public List<User> getAllEmployees() {
        return userRepository.findAllByRoleAndStatusOrderByUserIdDesc(Role.USER, Boolean.TRUE);
    }

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public Map<LocalDate, List<WorkSchedule>> groupSchedulesByDate(List<WorkSchedule> schedules) {
        return schedules.stream()
                .collect(Collectors.groupingBy(WorkSchedule::getWorkDate));
    }

    public Map<Integer, List<WorkSchedule>> groupSchedulesByUser(List<WorkSchedule> schedules) {
        return schedules.stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getUserId()));
    }

    private void validateScheduleDateTimeNotPast(LocalDate workDate, Shift shift) {
        LocalDateTime scheduleStartDateTime = LocalDateTime.of(workDate, shift.getStartTime());
        if (scheduleStartDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày đã qua rồi, không thể phân ca trong quá khứ.");
        }
    }
}
