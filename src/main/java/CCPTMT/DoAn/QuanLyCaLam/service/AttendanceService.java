package CCPTMT.DoAn.QuanLyCaLam.service;

import java.util.List;

import CCPTMT.DoAn.QuanLyCaLam.entity.Attendance;

public interface AttendanceService {

    Attendance getTodayAttendance(Integer userId);

    boolean hasAssignedShiftToday(Integer userId);

    List<Attendance> getAttendanceHistory(Integer userId, int maxEntries);

    Attendance checkIn(Integer userId);

    Attendance checkOut(Integer userId);
}
