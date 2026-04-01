package CCPTMT.DoAn.QuanLyCaLam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Requests {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private Integer requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private Users user;
    
    @Column(name = "Type", columnDefinition = "nvarchar(max)", nullable = false)
    private String type; // Nghỉ / Đổi ca
    
    @Column(name = "FromDate", nullable = false)
    private LocalDate fromDate;
    
    @Column(name = "ToDate", nullable = false)
    private LocalDate toDate;
    
    @Column(name = "Reason", columnDefinition = "nvarchar(max)")
    private String reason;
    
    @Column(name = "Status")
    private String requestStatus; // Pending / Approved / Rejected
}
