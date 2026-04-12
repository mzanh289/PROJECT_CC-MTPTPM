package CCPTMT.DoAn.QuanLyCaLam.dto;

import CCPTMT.DoAn.QuanLyCaLam.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionUserDto {

    private Integer userId;
    private String fullName;
    private String email;
    private Role role;
}
