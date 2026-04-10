package CCPTMT.DoAn.QuanLyCaLam.entity;

import java.time.LocalDate;

import CCPTMT.DoAn.QuanLyCaLam.entity.enums.RequestStatus;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.RequestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private Integer requestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false, length = 20, columnDefinition = "nvarchar(20)")
    private RequestType type;

    @Column(name = "FromDate", nullable = false)
    private LocalDate fromDate;

    @Column(name = "ToDate", nullable = false)
    private LocalDate toDate;

    @Column(name = "Reason", nullable = false, columnDefinition = "nvarchar(500)")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private RequestStatus status;
}
