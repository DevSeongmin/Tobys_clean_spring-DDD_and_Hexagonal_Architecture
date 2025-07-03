package tobyspring.splearn.adapter.security;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SecurePasswordEncoderTest {

	@Test
	void securePasswordEncoder() {
		SecurePasswordEncoder securePasswordEncoder = new SecurePasswordEncoder();

		String passwordHash = securePasswordEncoder.encode("secret");

		assertThat(securePasswordEncoder.matches("secret", passwordHash)).isTrue();
		assertThat(securePasswordEncoder.matches("false", passwordHash)).isFalse();
	}
}