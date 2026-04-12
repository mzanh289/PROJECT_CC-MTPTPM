package CCPTMT.DoAn.QuanLyCaLam.dto;

import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserViewDto {

    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Boolean status;
}
