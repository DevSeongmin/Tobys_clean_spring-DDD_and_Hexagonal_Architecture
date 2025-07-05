package tobyspring.splearn.application.member;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import tobyspring.splearn.application.member.provided.MemberFinder;
import tobyspring.splearn.application.member.provided.MemberRegister;
import tobyspring.splearn.application.member.required.EmailSender;
import tobyspring.splearn.application.member.required.MemberRepository;
import tobyspring.splearn.domain.member.DuplicateEmailException;
import tobyspring.splearn.domain.member.DuplicateProfileException;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.MemberInfoUpdateRequest;
import tobyspring.splearn.domain.member.MemberRegisterRequest;
import tobyspring.splearn.domain.member.PasswordEncoder;
import tobyspring.splearn.domain.member.Profile;
import tobyspring.splearn.domain.shared.Email;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class MemberModifyService implements MemberRegister {
	private final MemberFinder memberFinder;
	private final MemberRepository memberRepository;
	private final EmailSender emailSender;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Member register(MemberRegisterRequest registerRequest) {

		checkDuplicateEmail(registerRequest);

		Member member = Member.register(registerRequest, passwordEncoder);

		memberRepository.save(member);

		sendWelcomeEmail(member);

		return member;
	}

	@Override
	public Member activate(Long memberId) {
		Member member = memberFinder.find(memberId);

		member.activate();

		return memberRepository.save(member);
	}

	@Override
	public Member deactivate(Long memberId) {
		Member member = memberFinder.find(memberId);

		member.deactivate();

		return memberRepository.save(member);
	}

	@Override
	public Member updateInfo(Long memberId, MemberInfoUpdateRequest memberInfoUpdateRequest) {
		Member member = memberFinder.find(memberId);

		checkDuplicateProfile(member, memberInfoUpdateRequest.profileAddress());

		member.updateInfo(memberInfoUpdateRequest);

		return memberRepository.save(member);
	}

	private void checkDuplicateProfile(Member member, String profileAddress) {
		if (profileAddress.isEmpty()) return;

		Profile currentProfile = member.getDetail().getProfile();
		if (currentProfile != null && member.getDetail().getProfile().address().equals(profileAddress)) return;

		if (memberRepository.findByProfile(new Profile(profileAddress)).isPresent()) {
			throw new DuplicateProfileException("이미 존재하는 프로필 주소입니다. : " + profileAddress);
		}
	}

	private void sendWelcomeEmail(Member member) {
		emailSender.send(member.getEmail(), "등록을 완료해주세요.", "아래 링크를 클릭해서 등록을 완료해주세요");
	}

	private void checkDuplicateEmail(MemberRegisterRequest registerRequest) {
		if (memberRepository.findByEmail(new Email(registerRequest.email())).isPresent()) {
			extracted(registerRequest);
		}
	}

	private static void extracted(MemberRegisterRequest registerRequest) {
		throw new DuplicateEmailException("이미 가입된 이메일 입니다 : " + registerRequest.email());
	}
}
