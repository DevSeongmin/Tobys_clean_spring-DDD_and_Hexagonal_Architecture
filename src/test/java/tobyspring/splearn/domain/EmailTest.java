package tobyspring.splearn.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailTest {
	@Test
	void equality() {
		var email1 = new Email("qq221qq@naver.com");
		var email2 = new Email("qq221qq@naver.com");

		assertThat(email1).isEqualTo(email2);
	}
}