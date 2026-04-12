package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCPTMT.DoAn.QuanLyCaLam.entity.Attendance;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.AttendanceStatus;
import CCPTMT.DoAn.QuanLyCaLam.repository.AttendanceRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.WorkScheduleRepository;
import CCPTMT.DoAn.QuanLyCaLam.service.AttendanceService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;

    @Override
    public Attendance getTodayAttendance(Integer userId) {
        return attendanceRepository.findByUserUserIdAndWorkDate(userId, LocalDate.now()).orElse(null);
    }

    @Override
    public boolean hasAssignedShiftToday(Integer userId) {
        return workScheduleRepository.findByUserUserIdAndWorkDate(userId, LocalDate.now()).isPresent();
    }

    @Override
    public WorkSchedule getTodaySchedule(Integer userId) {
        return workScheduleRepository.findByUserUserIdAndWorkDate(userId, LocalDate.now()).orElse(null);
    }

    @Override
    public List<Attendance> getAttendanceHistory(Integer userId, int maxEntries) {
        List<Attendance> history = attendanceRepository.findByUserUserIdOrderByWorkDateDesc(userId);
        if (history.size() <= maxEntries) {
            return history;
        }
        return history.subList(0, maxEntries);
    }

    @Override
    @Transactional
    public Attendance checkIn(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        WorkSchedule schedule = requireAssignedShift(userId, today);

        Attendance attendance = attendanceRepository.findByUserUserIdAndWorkDate(userId, today)
                .orElseGet(() -> createAttendanceForToday(userId, today));

        if (attendance.getCheckIn() != null) {
            throw new IllegalStateException("Bạn đã check-in hôm nay.");
        }

        attendance.setCheckIn(now);
        attendance.setStatus(determineStatus(now.toLocalTime(), schedule.getShift().getStartTime()));
        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public Attendance checkOut(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        WorkSchedule schedule = requireAssignedShift(userId, today);

        Attendance attendance = attendanceRepository.findByUserUserIdAndWorkDate(userId, today)
                .orElseGet(() -> createAttendanceForToday(userId, today));

        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException("Bạn đã check-out hôm nay.");
        }

        if (attendance.getCheckIn() == null) {
            attendance.setCheckIn(now);
            attendance.setStatus(determineStatus(now.toLocalTime(), schedule.getShift().getStartTime()));
        }

        attendance.setCheckOut(now);
        return attendanceRepository.save(attendance);
    }

    private AttendanceStatus determineStatus(LocalTime checkInTime, LocalTime shiftStart) {
        if (checkInTime.isAfter(shiftStart)) {
            return AttendanceStatus.TRE;
        }
        return AttendanceStatus.DI_LAM;
    }

    private WorkSchedule requireAssignedShift(Integer userId, LocalDate today) {
        return workScheduleRepository.findByUserUserIdAndWorkDate(userId, today)
                .orElseThrow(() -> new IllegalStateException("Hôm nay bạn chưa được phân ca."));
    }

    private Attendance createAttendanceForToday(Integer userId, LocalDate today) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại."));
        Attendance attendance = Attendance.builder()
                .user(user)
                .workDate(today)
                .status(AttendanceStatus.DI_LAM)
                .build();
        return attendanceRepository.save(attendance);
    }
}
