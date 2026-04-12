package CCPTMT.DoAn.QuanLyCaLam.dto;

import java.time.LocalDate;

import CCPTMT.DoAn.QuanLyCaLam.entity.enums.RequestType;
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
public class RequestCreateDto {

    @NotNull(message = "Loại yêu cầu không được để trống")
    private RequestType type;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu không được trong quá khứ")
    private LocalDate fromDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc không được trong quá khứ")
    private LocalDate toDate;

    @NotBlank(message = "Lý do không được để trống")
    private String reason;

    // Custom validation: fromDate <= toDate
    public boolean isValidDateRange() {
        return fromDate != null && toDate != null && !fromDate.isAfter(toDate);
    }
}