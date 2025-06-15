package com.bossoverhere.capstone.boss_over_here_backend.domain.record.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    /**
     * 1) 달력용: 해당 년·월에 기록이 있는 날짜만 뽑아오기
     */
    @Query("""
        select r.recordDate 
          from Record r 
         where r.user.id = :userId
           and function('year', r.recordDate)  = :year
           and function('month', r.recordDate) = :month
        """)
    List<LocalDate> findCalendarDates(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    /**
     * 2) 일별 리스트: 주어진 날짜의 모든 Record 조회
     */
    List<Record> findAllByUser_IdAndRecordDate( Long userId, LocalDate recordDate);

    List<Record> findAllByUserIdAndSpotId(Long userId, Long spotId);

}
