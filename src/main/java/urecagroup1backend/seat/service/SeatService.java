package urecagroup1backend.seat.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.seat.domain.EntityStatus;
import urecagroup1backend.seat.dto.SeatEntryResponse;
import urecagroup1backend.seat.dto.SeatResponse;
import urecagroup1backend.seat.domain.Seat;
import urecagroup1backend.seat.domain.SeatEntry;
import urecagroup1backend.seat.repository.SeatEntryRepository;
import urecagroup1backend.seat.repository.SeatRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    private final SeatRepository seatRepository;
    private final SeatEntryRepository seatEntryRepository;

    @Transactional
    public SeatEntryResponse entry(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        if (seatEntryRepository.findBySeatIdAndStatus(seatId, EntityStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("이미 입실된 좌석입니다.");
        }
        // todo: 좌석 예약 확인, 내가 한 예약인지 여부에 따라 진행(10분 내 입실 필요)

        SeatEntry seatEntry = SeatEntry.builder()
                .userId(userId)
                .seatId(seat.getId())
                .createdAt(LocalDateTime.now())
                .build();

        SeatEntry saved = seatEntryRepository.save(seatEntry);
        return SeatEntryResponse.from(saved);
    }

    @Transactional
    public SeatEntryResponse exitSeat(Long seatId, Long userId) {
        seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        SeatEntry seatEntry = seatEntryRepository.findBySeatIdAndStatus(seatId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("입실된 좌석을 찾을 수 없습니다."));

        if (!seatEntry.getUserId().equals(userId)) {
           throw new IllegalStateException("자신의 좌석만 퇴실할 수 있습니다.");
        }

        seatEntry.delete();
        return SeatEntryResponse.from(seatEntry);
    }

    @Transactional
    public SeatResponse createSeat(String seatNumber) {
        if (seatRepository.findBySeatNumber(seatNumber).isPresent()) {
            throw new IllegalStateException("이미 존재하는 좌석입니다.");
        }

        Seat seat = Seat.builder()
                .seatNumber(seatNumber)
                .build();

        Seat saved = seatRepository.save(seat);
        return SeatResponse.from(saved);
    }
}
