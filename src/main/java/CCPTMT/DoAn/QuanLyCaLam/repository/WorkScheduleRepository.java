package CCPTMT.DoAn.QuanLyCaLam.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {

    List<WorkSchedule> findByUserUserId(Integer userId);

    List<WorkSchedule> findByUserUserIdOrderByWorkDateAsc(Integer userId);

    List<WorkSchedule> findByWorkDateBetween(LocalDate fromDate, LocalDate toDate);

    List<WorkSchedule> findByUserUserIdAndWorkDateBetween(Integer userId, LocalDate fromDate, LocalDate toDate);

    Optional<WorkSchedule> findByUserUserIdAndWorkDateAndShiftShiftId(Integer userId, LocalDate workDate,
            Integer shiftId);

    // Tất cả ca của 1 nhân viên trong 1 ngày — JOIN FETCH shift để tránh LazyInit
    @Query("SELECT ws FROM WorkSchedule ws JOIN FETCH ws.shift WHERE ws.user.userId = :userId AND ws.workDate = :workDate")
    List<WorkSchedule> findByUserUserIdAndWorkDate(@Param("userId") Integer userId,
            @Param("workDate") LocalDate workDate);

    @Query("SELECT ws FROM WorkSchedule ws "
            + "JOIN FETCH ws.user u "
            + "JOIN FETCH ws.shift s "
            + "WHERE ws.workDate BETWEEN :fromDate AND :toDate "
            + "AND u.role = :role "
            + "ORDER BY ws.workDate ASC, u.fullName ASC")
    List<WorkSchedule> findSchedulesWithUserAndShiftBetween(@Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("role") Role role);

    // Kiểm tra nhân viên đã có đúng ca này trong ngày này chưa
    boolean existsByUserUserIdAndWorkDateAndShiftShiftId(Integer userId, LocalDate workDate, Integer shiftId);
}
