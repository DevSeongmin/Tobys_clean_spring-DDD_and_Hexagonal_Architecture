package tobyspring.splearn.application.member;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tobyspring.splearn.application.member.provided.MemberFinder;
import tobyspring.splearn.application.member.required.MemberRepository;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.PasswordEncoder;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class MemberQueryService implements MemberFinder {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Member find(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. id: " + memberId));
	}
}
