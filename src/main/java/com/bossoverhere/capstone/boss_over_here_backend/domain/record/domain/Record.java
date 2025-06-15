package com.bossoverhere.capstone.boss_over_here_backend.domain.record.domain;

import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import com.bossoverhere.capstone.boss_over_here_backend.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "record")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record extends BaseEntity {
    @Id
    @Column(name = "record_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @NotNull(message = "매출은 필수값입니다.")
    @Column(name = "revenue")
    private Long revenue;

    @NotNull(message = "지출은 필수값입니다.")
    @Column(name = "expense")
    private Long expense;

    @Formula("revenue - expense")
    private Long profit;

    @Size(max = 300)
    @Column(length = 300)
    private String memo;

    public Long calculateProfit() {
        if (revenue == null || expense == null) {
            // 비정상적인 상태이므로 IllegalStateException 으로 대체
            throw new IllegalStateException("매출 또는 지출 정보가 없어서 손익을 계산할 수 없습니다.");
        }
        return revenue - expense;
    }

    public void changeSpot(Spot spot) {
        this.spot = spot;
    }

    public void changeStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void changeEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void changeRevenue(Long revenue) {
        this.revenue = revenue;
    }

    public void changeExpense(Long expense) {
        this.expense = expense;
    }

    public void changeMemo(String memo) {
        this.memo = memo;
    }
}
