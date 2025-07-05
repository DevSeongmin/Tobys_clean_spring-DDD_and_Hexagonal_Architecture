package tobyspring.splearn.application.member.provided;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.MemberFixture;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
record MemberFinderTest(MemberFinder memberFinder, MemberRegister memberRegister, EntityManager em) {

	@Test
	void find() {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
		em.flush();
		em.clear();

		Member found = memberFinder.find(member.getId());

		assertThat(found.getId()).isEqualTo(member.getId());
	}

	@Test
	void fndFail() {
		assertThatThrownBy(() -> memberFinder.find(999L))
			.isInstanceOf(IllegalArgumentException.class);
	}
}