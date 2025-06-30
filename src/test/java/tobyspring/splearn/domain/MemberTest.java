package tobyspring.splearn.domain;

import static org.assertj.core.api.Assertions.*;
import static tobyspring.splearn.domain.MemberFixture.*;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTest {
	Member member;
	PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		passwordEncoder = MemberFixture.createPasswordEncoder();

		member = Member.register(createMemberRegisterRequest(), passwordEncoder);
	}

	@Test
	void registerMember() {
		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
	}

	@Test
	void activate() {
		member.activate();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
	}

	@Test
	void activateFail() {
		member.activate();

		assertThatThrownBy(() -> {
			member.activate();
		}).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void deactivate() {
		member.activate();

		member.deactivate();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
	}


	@Test
	void deactivateFAIL() {
		assertThatThrownBy(() -> member.deactivate()).isInstanceOf(IllegalStateException.class);

		member.activate();
		member.deactivate();

		assertThatThrownBy(() -> member.deactivate()).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void verifyPassword() {
		assertThat(member.verifyPassword("secretsecret", passwordEncoder)).isTrue();
		assertThat(member.verifyPassword("false", passwordEncoder)).isFalse();
	}

	@Test
	void changeNickname() {
		assertThat(member.getNickname()).isEqualTo("DevSeongMin");

		member.changeNickName("tmp");

		assertThat(member.getNickname()).isEqualTo("tmp");
	}

	@Test
	void changePassword() {
		member.changePassword("verysecret", passwordEncoder);

		assertThat(member.verifyPassword("verysecret", passwordEncoder)).isTrue();
	}

	@Test
	void shouldBeActive() {
		assertThat(member.isActive()).isFalse();

		member.activate();

		assertThat(member.isActive()).isTrue();

		member.deactivate();

		assertThat(member.isActive()).isFalse();
	}

	@Test
	void invalidEmail() {
		assertThatThrownBy(
			() -> Member.register(createMemberRegisterRequest("invalid email"), passwordEncoder)
		).isInstanceOf(IllegalArgumentException.class);

		Member.register(new MemberRegisterRequest("qq221qq@naver.com", "SM", "secret"), passwordEncoder);

	}
}