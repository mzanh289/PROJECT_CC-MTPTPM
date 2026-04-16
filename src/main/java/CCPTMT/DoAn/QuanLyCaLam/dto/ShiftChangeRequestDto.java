package CCPTMT.DoAn.QuanLyCaLam.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftChangeRequestDto {

    @NotNull(message = "Ngày làm không được để trống")
    @FutureOrPresent(message = "Ngày làm không được trong quá khứ")
    private LocalDate workDate;

    @NotNull(message = "Ca hiện tại không được để trống")
    private Integer shiftId;

    @NotNull(message = "Ca muốn đổi sang không được để trống")
    private Integer targetShiftId;

    @NotBlank(message = "Lý do không được để trống")
    private String reason;
}
