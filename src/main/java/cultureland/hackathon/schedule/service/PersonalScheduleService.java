package cultureland.hackathon.schedule.service;

import cultureland.hackathon.global.exception.GeneralException;
import cultureland.hackathon.member.code.MemberErrorCode;
import cultureland.hackathon.member.entity.Member;
import cultureland.hackathon.member.repository.MemberRepository;
import cultureland.hackathon.schedule.code.PersonalScheduleErrorCode;
import cultureland.hackathon.schedule.dto.PersonalScheduleCreateRequestDto;
import cultureland.hackathon.schedule.dto.PersonalScheduleResponseDto;
import cultureland.hackathon.schedule.dto.PersonalScheduleUpdateRequestDto;
import cultureland.hackathon.schedule.entity.PersonalSchedule;
import cultureland.hackathon.schedule.repository.PersonalScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalScheduleService {

    private final PersonalScheduleRepository personalScheduleRepository;
    private final MemberRepository memberRepository;

    // 개인 일정 생성
    public PersonalScheduleResponseDto createPersonalSchedule(
            Long memberId,
            PersonalScheduleCreateRequestDto requestDto
    ) {
        Member member = getMember(memberId);

        PersonalSchedule personalSchedule = PersonalSchedule.create(
                member,
                requestDto.getTitle(),
                toUtcDateTime(requestDto.getStartDateTime()),
                toUtcDateTime(requestDto.getEndDateTime()),
                requestDto.getWeight()
        );

        PersonalSchedule saved = personalScheduleRepository.save(personalSchedule);

        return PersonalScheduleResponseDto.from(saved);
    }

    // 개인 일정 수정
    public PersonalScheduleResponseDto updatePersonalSchedule(
            Long memberId,
            Long personalScheduleId,
            PersonalScheduleUpdateRequestDto requestDto
    ) {
        validateUpdate(requestDto);

        Member member = getMember(memberId);
        PersonalSchedule personalSchedule = findPersonalSchedule(personalScheduleId, member);

        personalSchedule.update(
                requestDto.getTitle(),
                toUtcDateTime(requestDto.getStartDateTime()),
                toUtcDateTime(requestDto.getEndDateTime()),
                requestDto.getWeight()
        );

        return PersonalScheduleResponseDto.from(personalSchedule);
    }

    // 개인 일정 삭제
    public void deletePersonalSchedule(
            Long memberId,
            Long personalScheduleId
    ) {
        Member member = getMember(memberId);

        PersonalSchedule personalSchedule = findPersonalSchedule(personalScheduleId, member);

        personalScheduleRepository.delete(personalSchedule);
    }

    // 회원 조회
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                                new GeneralException(
                                        MemberErrorCode.MEMBER_NOT_FOUND
                                )
                );
    }

    // 개인일정 조회 및 소유권 검증
    private PersonalSchedule findPersonalSchedule(
            Long personalScheduleId,
            Member member
    ) {
        return personalScheduleRepository
                .findByPersonalScheduleIdAndMember(personalScheduleId, member)
                .orElseThrow(() -> new GeneralException(
                        PersonalScheduleErrorCode.PERSONAL_SCHEDULE_NOT_FOUND
                ));
    }

    // 수정 시 모든 필드 비워져 있는 경우 검증
    private void validateUpdate(PersonalScheduleUpdateRequestDto requestDto) {
        if (
                requestDto.getTitle() == null
                && requestDto.getStartDateTime() == null
                && requestDto.getEndDateTime() == null
                && requestDto.getWeight() == null
        ) {
            throw new GeneralException(
                    PersonalScheduleErrorCode.MISSING_FIELDS
            );
        }
    }

    // 요청 시각을 UTC 기준으로 변환
    private LocalDateTime toUtcDateTime(Instant instant) {
        return instant != null
                ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
                : null;
    }
}
