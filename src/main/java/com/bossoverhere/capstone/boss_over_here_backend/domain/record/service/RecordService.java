package com.bossoverhere.capstone.boss_over_here_backend.domain.record.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dao.RecordRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.domain.Record;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.request.RecordCreateRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.record.dto.request.RecordUpdateRequest;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.domain.Spot;
import com.bossoverhere.capstone.boss_over_here_backend.domain.spot.service.SpotService;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.service.UserService;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.RecordErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {
    private final RecordRepository recordRepository;
    private final UserService userService;
    private final SpotService spotService;

    /** 1) 달력용: 해당 년·월에 기록이 있는 날짜만 뽑아오기 */
    public List<LocalDate> getCalendarDates(Long userId, int year, int month) {
        userService.getUserById(userId);
        return recordRepository.findCalendarDates(userId, year, month);
    }

    /** 2) 일별 리스트: 특정 날짜의 모든 Record 조회 */
    public List<Record> getRecordsByDate(Long userId, LocalDate date) {
        userService.getUserById(userId);
        return recordRepository.findAllByUser_IdAndRecordDate(userId, date);
    }
    public List<Record> getRecordsBySpot(Long userId, Long spotId) {
        userService.getUserById(userId);
        spotService.getSpotById(spotId);
        return recordRepository.findAllByUserIdAndSpotId(userId, spotId);
    }

    /** 3) 상세 조회 (권한 체크 포함) */
    public Record getRecord(Long userId, Long recordId) {
        return findOwnRecord(userId, recordId);
    }


    //기록 생성
    @Transactional
    public Record create(Long userId, RecordCreateRequest dto){
        User user = userService.getUserById(userId);
        Spot spot = spotService.getSpotById(dto.spotId());

        // 시간 검증
        if (dto.startTime().isAfter(dto.endTime())) {
            throw new ApplicationException(RecordErrorCode.INVALID_TIME);
        }

        // 금액 검증
        if (dto.revenue() < 0 || dto.expense() < 0) {
            throw new ApplicationException(RecordErrorCode.INVALID_AMOUNT);
        }

        // 날짜 검증
        if (dto.recordDate().isAfter(LocalDate.now())) {
            throw new ApplicationException(RecordErrorCode.INVALID_DATE);
        }

        Record record = dto.toEntity(user,spot);
        return recordRepository.save(record);

    }

    //원하는 필드만 업데이트
    @Transactional
    public Record update(Long userId, Long recordId, RecordUpdateRequest dto){
        Record record = findOwnRecord(userId, recordId);

        LocalTime newStartTime = dto.startTime().orElse(record.getStartTime());
        LocalTime newEndTime = dto.endTime().orElse(record.getEndTime());
        if (newStartTime.isAfter(newEndTime)) {
            throw new ApplicationException(RecordErrorCode.INVALID_TIME);
        }

        // 금액 검증
        if (dto.revenue().isPresent() && dto.revenue().get() < 0) {
            throw new ApplicationException(RecordErrorCode.INVALID_AMOUNT);
        }
        if (dto.expense().isPresent() && dto.expense().get() < 0) {
            throw new ApplicationException(RecordErrorCode.INVALID_AMOUNT);
        }

        dto.spotId().ifPresent(id -> {
            Spot spot = spotService.getSpotById(id);
            record.changeSpot(spot);
        });
        dto.startTime().ifPresent(record::changeStartTime);
        dto.endTime().ifPresent(record::changeEndTime);

        dto.revenue().ifPresent(record::changeRevenue);
        dto.expense().ifPresent(record::changeExpense);
        dto.memo().ifPresent(record::changeMemo);
        return record;

    }

    //기록 삭제
    @Transactional
    public void delete(Long userId, Long recordId){
        Record record = findOwnRecord(userId, recordId);
        recordRepository.delete(record);

    }


    //해당 사용자의 기록 존재 여부 확인
    private Record findOwnRecord(Long userId, Long recordId) {
        userService.getUserById(userId);
        Record record = getRecordById(recordId);
        if(!record.getUser().getId().equals(userId)){
            throw new ApplicationException(RecordErrorCode.ACCESS_DENIED);
        }
        return record;
    }


    //해당 기록이 존재하는지 확인(id로 조회)
    public Record getRecordById(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new ApplicationException(RecordErrorCode.NOTFOUND_RECORD));
    }

}
