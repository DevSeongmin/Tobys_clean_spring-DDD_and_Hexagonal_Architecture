package tobyspring.splearn.application.member.provided;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.member.DuplicateEmailException;
import tobyspring.splearn.domain.member.DuplicateProfileException;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.MemberFixture;
import tobyspring.splearn.domain.member.MemberInfoUpdateRequest;
import tobyspring.splearn.domain.member.MemberRegisterRequest;
import tobyspring.splearn.domain.member.MemberStatus;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
record MemberRegisterTest(MemberRegister memberRegister, EntityManager em) {

	@Test
	void register() {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

		System.out.println(member);

		assertThat(member.getId()).isNotNull();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
	}


	@Test
	void duplicateEmailFail() {
		memberRegister.register(MemberFixture.createMemberRegisterRequest());

		assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
			.isInstanceOf(DuplicateEmailException.class);
	}

	@Test
	void activate() {
		Member member = registerMember();

		member = memberRegister.activate(member.getId());

		em.flush();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(member.getDetail().getActivatedAt()).isNotNull();
	}

	private Member registerMember() {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
		em.flush();
		em.clear();
		return member;
	}

	private Member registerMember(String email) {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest(email));
		em.flush();
		em.clear();
		return member;
	}

	@Test
	void dectivate() {
		Member member = registerMember();

		member = memberRegister.activate(member.getId());

		em.flush();
		em.clear();

		member = memberRegister.deactivate(member.getId());
		em.flush();
		em.clear();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
		assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
	}

	@Test
	void updateInfo() {
		Member member = registerMember();

		member = memberRegister.activate(member.getId());
		em.flush();
		em.clear();

		member = memberRegister.updateInfo(member.getId(),
			new MemberInfoUpdateRequest("hsmgdg", "devsm100", "자기소개"));

		assertThat(member.getDetail().getProfile().address()).isEqualTo("devsm100");
	}

	@Test
	void updateInfoFail() {
		Member member = registerMember();
		memberRegister.activate(member.getId());
		memberRegister.updateInfo(member.getId(),
			new MemberInfoUpdateRequest("hsmgdg", "devsm100", "자기소개"));

		Member member2 = registerMember("devSM2@splearn.app");
		memberRegister.activate(member2.getId());
		em.flush();
		em.clear();

		// member2는 기존의 member와 같은 profile을 사용할 수 없다.
		assertThatThrownBy(() -> {
			memberRegister.updateInfo(member2.getId(),
				new MemberInfoUpdateRequest("James", "devsm100", "Introduction"));
		}).isInstanceOf(DuplicateProfileException.class);

		// 다른 프로필로는 변경 가능
		memberRegister.updateInfo(member2.getId(),
			new MemberInfoUpdateRequest("James", "devsm100a", "Introduction"));

		// 기존 프로필로도 변경 가능
		memberRegister.updateInfo(member.getId(),
			new MemberInfoUpdateRequest("hsmgdg", "devsm100", "자기소개"));
		
		// 프로필 주소를 제거하는 것도 가능
		memberRegister.updateInfo(member.getId(),
			new MemberInfoUpdateRequest("hsmgdg", "", "자기소개"));

		// 프로필 주소 중복은 허용하지 않음
		assertThatThrownBy(() -> {
			memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("hsmgdg", "devsm100a", "자기소개"));
		}).isInstanceOf(DuplicateProfileException.class);

	}

	
	@Test
	void memberRegisterRequestFail() {

		checkValidation(new MemberRegisterRequest("qq221qq@naver.com", "DSM", "secret"));
		checkValidation(new MemberRegisterRequest("qq221qq@naver.com", "DSMgwgrgwrgwrgwrgwrgwrgwrgfdvsdbsrtbrs", "secret"));
		checkValidation(new MemberRegisterRequest("naver.app", "DSMgwgrgwrgwrgwrgwrgwrgwrgfdvsdbsrtbrs", "secret"));
	}

	private void checkValidation(MemberRegisterRequest invalid) {
		assertThatThrownBy(() -> memberRegister.register(invalid))
			.isInstanceOf(ConstraintViolationException.class);
	}
}
