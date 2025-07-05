package tobyspring.splearn.domain.member;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tobyspring.splearn.domain.member.MemberFixture.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

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
		assertThat(member.getDetail().getRegisterAt()).isNotNull();
	}

	@Test
	void activate() {
		member.activate();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(member.getDetail().getActivatedAt()).isNotNull();
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
		assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
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

	@Test
	void updateInfo() {
		member.activate();

		MemberInfoUpdateRequest request = new MemberInfoUpdateRequest("hsm", "devsm100", "자기소개");
		member.updateInfo(request);

		assertThat(member.getNickname()).isEqualTo(request.nickname());
		assertThat(member.getDetail().getProfile().address()).isEqualTo(request.profileAddress());
		assertThat(member.getDetail().getIntroduction()).isEqualTo(request.introduction());
	}

	@Test
	void updateInfoFail() {
		assertThatThrownBy(() -> {
			MemberInfoUpdateRequest request = new MemberInfoUpdateRequest("hsm", "devsm100", "자기소개");
			member.updateInfo(request);
		}).isInstanceOf(IllegalStateException.class);
	}
}