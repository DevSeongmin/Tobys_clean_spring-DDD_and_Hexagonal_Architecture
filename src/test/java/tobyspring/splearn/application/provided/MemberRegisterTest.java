package tobyspring.splearn.application.provided;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.DuplicateEmailException;
import tobyspring.splearn.domain.Member;
import tobyspring.splearn.domain.MemberFixture;
import tobyspring.splearn.domain.MemberRegisterRequest;
import tobyspring.splearn.domain.MemberStatus;

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
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
		em.flush();
		em.clear();

		member = memberRegister.activate(member.getId());

		em.flush();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
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
