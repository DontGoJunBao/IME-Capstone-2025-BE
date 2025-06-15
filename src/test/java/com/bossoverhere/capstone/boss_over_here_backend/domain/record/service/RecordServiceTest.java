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
import com.bossoverhere.capstone.boss_over_here_backend.global.error.SpotErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;
    @Mock private UserService userService;
    @Mock private SpotService spotService;

    @InjectMocks
    private RecordService sut;

    private final Long USER_ID = 42L;
    private User user;
    private Spot spot;
    private Record existingRecord;

    @BeforeEach
    void setUp() {
        user = User.builder().id(USER_ID).build();
        spot = Spot.builder().id(10L).name("기본 스팟").build();

        existingRecord = Record.builder()
                .id(100L)
                .user(user)
                .spot(spot)
                .recordDate(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(18, 0))
                .revenue(1000L)
                .expense(200L)
                .memo("기본 메모")
                .build();
    }

    // --- getCalendarDates ---

    @Test
    void getCalendarDates_성공() {
        List<LocalDate> dates = List.of(
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 2)
        );

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(recordRepository.findCalendarDates(USER_ID, 2025, 5))
                .thenReturn(dates);

        List<LocalDate> result = sut.getCalendarDates(USER_ID, 2025, 5);

        assertThat(result).containsExactlyElementsOf(dates);
        verify(recordRepository).findCalendarDates(USER_ID, 2025, 5);
    }

    // --- getRecordsByDate ---

    @Test
    void getRecordsByDate_성공() {
        LocalDate date = LocalDate.of(2025, 5, 5);
        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(recordRepository.findAllByUser_IdAndRecordDate(USER_ID, date))
                .thenReturn(List.of(existingRecord));

        var list = sut.getRecordsByDate(USER_ID, date);

        assertThat(list).hasSize(1).first().isSameAs(existingRecord);
        verify(recordRepository).findAllByUser_IdAndRecordDate(USER_ID, date);
    }

    // --- getRecord ---

    @Test
    void getRecord_존재함_권한O() {
        when(recordRepository.findById(100L))
                .thenReturn(Optional.of(existingRecord));

        // userService 호출도 stub
        when(userService.getUserById(USER_ID)).thenReturn(user);

        Record r = sut.getRecord(USER_ID, 100L);

        assertThat(r).isSameAs(existingRecord);
    }

    @Test
    void getRecord_존재하지않음() {
        when(recordRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.getRecord(USER_ID, 999L))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.NOTFOUND_RECORD);
    }

    @Test
    void getRecord_권한없음() {
        User other = User.builder().id(99L).build();
        Record rec = Record.builder()
                .id(200L)
                .user(other)
                .spot(spot)
                .recordDate(LocalDate.now())
                .startTime(LocalTime.NOON)
                .endTime(LocalTime.NOON.plusHours(1))
                .revenue(500L)
                .expense(100L)
                .memo("")
                .build();

        when(recordRepository.findById(200L))
                .thenReturn(Optional.of(rec));
        when(userService.getUserById(USER_ID)).thenReturn(user);

        assertThatThrownBy(() -> sut.getRecord(USER_ID, 200L))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.ACCESS_DENIED);
    }

    // --- create ---

    @Test
    void create_성공() {
        RecordCreateRequest dto = new RecordCreateRequest(
                LocalDate.now().minusDays(1),
                spot.getId(),
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                1000L,
                500L,
                "메모"
        );

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(spotService.getSpotById(spot.getId())).thenReturn(spot);

        ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
        when(recordRepository.save(captor.capture()))
                .thenAnswer(inv -> {
                    Record r = inv.getArgument(0);
                    return Record.builder()
                            .id(123L)
                            .user(r.getUser())
                            .spot(r.getSpot())
                            .recordDate(r.getRecordDate())
                            .startTime(r.getStartTime())
                            .endTime(r.getEndTime())
                            .revenue(r.getRevenue())
                            .expense(r.getExpense())
                            .memo(r.getMemo())
                            .build();
                });

        Record saved = sut.create(USER_ID, dto);

        // 저장된 ID 검증
        assertThat(saved.getId()).isEqualTo(123L);
        // 캡처된 엔티티 검증
        Record arg = captor.getValue();
        assertThat(arg.getSpot()).isEqualTo(spot);
        assertThat(arg.getRevenue()).isEqualTo(1000L);
        // 수익 계산 검증
        assertThat(saved.calculateProfit()).isEqualTo(1000L - 500L);

        verify(recordRepository).save(any());
    }

    @Test
    void create_시간검증실패() {
        RecordCreateRequest dto = new RecordCreateRequest(
                LocalDate.now().minusDays(1),
                spot.getId(),
                LocalTime.of(18, 0),
                LocalTime.of(8, 0),
                1000L,
                500L,
                ""
        );

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(spotService.getSpotById(spot.getId())).thenReturn(spot);

        assertThatThrownBy(() -> sut.create(USER_ID, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.INVALID_TIME);
    }

    @Test
    void create_금액검증실패() {
        RecordCreateRequest dto = new RecordCreateRequest(
                LocalDate.now().minusDays(1),
                spot.getId(),
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                -1L,
                100L,
                ""
        );

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(spotService.getSpotById(spot.getId())).thenReturn(spot);

        assertThatThrownBy(() -> sut.create(USER_ID, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.INVALID_AMOUNT);
    }

    @Test
    void create_날짜검증실패() {
        RecordCreateRequest dto = new RecordCreateRequest(
                LocalDate.now().plusDays(1),
                spot.getId(),
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                100L,
                50L,
                ""
        );

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(spotService.getSpotById(spot.getId())).thenReturn(spot);

        assertThatThrownBy(() -> sut.create(USER_ID, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.INVALID_DATE);
    }

    // --- update ---

    @Test
    void update_성공_전체필드변경() {
        Spot newSpot = Spot.builder().id(20L).name("새 스팟").build();
        when(recordRepository.findById(100L)).thenReturn(Optional.of(existingRecord));
        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(spotService.getSpotById(newSpot.getId())).thenReturn(newSpot);

        RecordUpdateRequest dto = new RecordUpdateRequest(
                Optional.of(newSpot.getId()),
                Optional.of(LocalTime.of(7, 0)),
                Optional.of(LocalTime.of(12, 0)),
                Optional.of(2000L),
                Optional.of(300L),
                Optional.of("새메모")
        );

        Record updated = sut.update(USER_ID, 100L, dto);

        assertThat(updated.getSpot()).isEqualTo(newSpot);
        assertThat(updated.getStartTime()).isEqualTo(LocalTime.of(7, 0));
        assertThat(updated.getEndTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(updated.getRevenue()).isEqualTo(2000L);
        assertThat(updated.getExpense()).isEqualTo(300L);
        assertThat(updated.calculateProfit()).isEqualTo(2000L - 300L);
        assertThat(updated.getMemo()).isEqualTo("새메모");
    }

    @Test
    void update_시간검증실패() {
        when(recordRepository.findById(100L)).thenReturn(Optional.of(existingRecord));
        when(userService.getUserById(USER_ID)).thenReturn(user);

        RecordUpdateRequest dto = new RecordUpdateRequest(
                Optional.empty(),
                Optional.of(LocalTime.of(20, 0)),
                Optional.of(LocalTime.of(10, 0)),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        assertThatThrownBy(() -> sut.update(USER_ID, 100L, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.INVALID_TIME);
    }

    @Test
    void update_금액검증실패() {
        when(recordRepository.findById(100L)).thenReturn(Optional.of(existingRecord));
        when(userService.getUserById(USER_ID)).thenReturn(user);

        RecordUpdateRequest dto = new RecordUpdateRequest(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(-100L),
                Optional.of(-50L),
                Optional.empty()
        );

        assertThatThrownBy(() -> sut.update(USER_ID, 100L, dto))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.INVALID_AMOUNT);
    }

    // --- delete ---

    @Test
    void delete_성공() {
        when(recordRepository.findById(100L)).thenReturn(Optional.of(existingRecord));
        when(userService.getUserById(USER_ID)).thenReturn(user);

        sut.delete(USER_ID, 100L);

        verify(recordRepository).delete(existingRecord);
    }

    @Test
    void delete_권한없음() {
        Record other = Record.builder()
                .id(existingRecord.getId())
                .user(User.builder().id(999L).build())
                .spot(spot)
                .recordDate(existingRecord.getRecordDate())
                .startTime(existingRecord.getStartTime())
                .endTime(existingRecord.getEndTime())
                .revenue(existingRecord.getRevenue())
                .expense(existingRecord.getExpense())
                .memo(existingRecord.getMemo())
                .build();

        when(recordRepository.findById(200L)).thenReturn(Optional.of(other));
        when(userService.getUserById(USER_ID)).thenReturn(user);

        assertThatThrownBy(() -> sut.delete(USER_ID, 200L))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(RecordErrorCode.ACCESS_DENIED);
    }

    @Test
    void getRecordsBySpot_성공() {
        // 준비
        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(spotService.getSpotById(spot.getId())).thenReturn(spot);

        List<Record> recs = List.of(existingRecord);
        when(recordRepository.findAllByUserIdAndSpotId(USER_ID, spot.getId()))
                .thenReturn(recs);

        // 실행
        List<Record> result = sut.getRecordsBySpot(USER_ID, spot.getId());

        // 검증
        assertThat(result).hasSize(1)
                .first().isSameAs(existingRecord);

        verify(userService).getUserById(USER_ID);
        verify(spotService).getSpotById(spot.getId());
        verify(recordRepository).findAllByUserIdAndSpotId(USER_ID, spot.getId());
    }

    @Test
    void getRecordsBySpot_스팟없음() {
        // 준비: spotService에서 NOTFOUND_SPOT 예외 발생
        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(spotService.getSpotById(999L))
                .thenThrow(new ApplicationException(SpotErrorCode.NOTFOUND_SPOT));

        // 실행 + 검증
        assertThatThrownBy(() -> sut.getRecordsBySpot(USER_ID, 999L))
                .isInstanceOf(ApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(SpotErrorCode.NOTFOUND_SPOT);

        verify(userService).getUserById(USER_ID);
        verify(spotService).getSpotById(999L);
        verifyNoInteractions(recordRepository);
    }
}