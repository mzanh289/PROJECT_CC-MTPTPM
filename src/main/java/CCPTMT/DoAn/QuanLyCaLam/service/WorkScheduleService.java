package CCPTMT.DoAn.QuanLyCaLam.service;

import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.repository.ShiftRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.WorkScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Shift> shiftOpt = shiftRepository.findById(shiftId);

        if (userOpt.isEmpty() || shiftOpt.isEmpty()) {
            throw new IllegalArgumentException("User or Shift not found");
        }

        Optional<WorkSchedule> existingSchedule = workScheduleRepository
                .findByUserUserIdAndWorkDate(userId, workDate);

        if (existingSchedule.isPresent()) {
            WorkSchedule schedule = existingSchedule.get();
            schedule.setShift(shiftOpt.get());
            return workScheduleRepository.save(schedule);
        } else {
            WorkSchedule schedule = WorkSchedule.builder()
                    .user(userOpt.get())
                    .shift(shiftOpt.get())
                    .workDate(workDate)
                    .build();
            return workScheduleRepository.save(schedule);
        }
    }

    @Transactional
    public void deleteSchedule(Integer id) {
        workScheduleRepository.deleteById(id);
    }

    @Transactional
    public void deleteScheduleByUserAndDate(Integer userId, LocalDate workDate) {
        Optional<WorkSchedule> schedule = workScheduleRepository.findByUserUserIdAndWorkDate(userId, workDate);
        schedule.ifPresent(workScheduleRepository::delete);
    }

    public List<User> getAllEmployees() {
        return userRepository.findAll();
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
}
