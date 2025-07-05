package tobyspring.splearn.domain.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProfileTest {

	@Test
	void profile() {
		new Profile("address");
		new Profile("addr123");
		new Profile("12345");
	}

	@Test
	void profileFail() {
		assertThatThrownBy(() -> new Profile("longlonglonglonglonglong"))
			.isInstanceOf(IllegalArgumentException.class);


		assertThatThrownBy(() -> new Profile("A"))
			.isInstanceOf(IllegalArgumentException.class);


		assertThatThrownBy(() -> new Profile("프로필"))
			.isInstanceOf(IllegalArgumentException.class);


	}

	@Test
	void url() {
		var profile = new Profile("devsm");

		assertThat(profile.url()).isEqualTo("@devsm");
	}
}