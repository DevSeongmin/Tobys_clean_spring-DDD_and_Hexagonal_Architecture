package tobyspring.splearn.domain.member;

import java.util.Locale;

public class MemberFixture {
	public static MemberRegisterRequest createMemberRegisterRequest(String email) {
		return new MemberRegisterRequest(email, "DevSeongMin", "secretsecret");
	}

	public static MemberRegisterRequest createMemberRegisterRequest() {
		return createMemberRegisterRequest("qq221qq@naver.com");
	}

	public static PasswordEncoder createPasswordEncoder() {
		return new PasswordEncoder() {
			@Override
			public String encode(String password) {
				return password.toUpperCase(Locale.ROOT);
			}

			@Override
			public boolean matches(String password, String passwordHash) {
				return encode(password).equals(passwordHash);
			}
		};
	}
}
