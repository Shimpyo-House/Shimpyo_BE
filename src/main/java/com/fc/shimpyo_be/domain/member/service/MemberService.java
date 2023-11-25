package com.fc.shimpyo_be.domain.member.service;

import com.fc.shimpyo_be.domain.member.dto.request.CheckPasswordRequestDto;
import com.fc.shimpyo_be.domain.member.dto.request.UpdateMemberRequestDto;
import com.fc.shimpyo_be.domain.member.dto.response.CheckPasswordResponseDto;
import com.fc.shimpyo_be.domain.member.dto.response.MemberResponseDto;
import com.fc.shimpyo_be.domain.member.entity.Member;
import com.fc.shimpyo_be.domain.member.exception.InvalidPasswordException;
import com.fc.shimpyo_be.domain.member.exception.MemberNotFoundException;
import com.fc.shimpyo_be.domain.member.exception.UnmatchedPasswordException;
import com.fc.shimpyo_be.domain.member.exception.WrongPasswordException;
import com.fc.shimpyo_be.domain.member.repository.MemberRepository;
import com.fc.shimpyo_be.global.util.SecurityUtil;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$#^()!%*?&])[A-Za-z\\d@$!#^()%*?&]{8,30}$";

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    public boolean isExistsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member getMember(long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    public MemberResponseDto updateMember(UpdateMemberRequestDto updateMemberRequestDto) {
        Member member = getMember(securityUtil.getCurrentMemberId());
        member.update(updateMemberRequestDto);
        if (updateMemberRequestDto.getPassword() != null) {
            updatePassword(member, updateMemberRequestDto);
        }
        return MemberResponseDto.of(member);
    }

    public CheckPasswordResponseDto checkPassword(CheckPasswordRequestDto checkPasswordRequestDto) {
        Member member = getMember(securityUtil.getCurrentMemberId());
        checkCorrectPassword(member, checkPasswordRequestDto.getPassword());
        return CheckPasswordResponseDto.builder()
            .isCorrectPassword(true)
            .build();
    }

    private void updatePassword(Member member, UpdateMemberRequestDto updateMemberRequestDto) {
        checkValidPassword(updateMemberRequestDto.getPassword());
        checkMatchedPassword(updateMemberRequestDto.getPassword(),
            updateMemberRequestDto.getPasswordConfirm());
        member.changePassword(passwordEncoder.encode(updateMemberRequestDto.getPassword()));
    }

    public void checkCorrectPassword(Member member, String password) {
        if (!member.getPassword().equals(password)) {
            throw new WrongPasswordException();
        }
    }

    public void checkMatchedPassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new UnmatchedPasswordException();
        }
    }

    public void checkValidPassword(String password) {
        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new InvalidPasswordException();
        }
    }
}
