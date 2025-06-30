package tobyspring.splearn.domain;

import static java.util.Objects.*;
import static org.springframework.util.Assert.*;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NaturalIdCache
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@NaturalId
	private Email email;

	private String nickname;

	private String passwordHash;

	@Enumerated(EnumType.STRING)
	private MemberStatus status;

	public static Member register(MemberRegisterRequest registerRequest, PasswordEncoder passwordEncoder) {
		Member member = new Member();

		member.email = new Email(registerRequest.email());
		member.nickname = requireNonNull(registerRequest.nickname());
		member.passwordHash = passwordEncoder.encode(requireNonNull(registerRequest.password()));

		member.status = MemberStatus.PENDING;

		return member;
	}

	public void activate() {
		state(status == MemberStatus.PENDING, "PENDING 상태가 아닙니다.");

		this.status = MemberStatus.ACTIVE;
	}

	public void deactivate() {
		state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다.");

		this.status = MemberStatus.DEACTIVATED;
	}

	public boolean verifyPassword(String secret, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(secret, passwordHash);
	}

	public void changeNickName(String nickname) {
		this.nickname = requireNonNull(nickname);
	}

	public void changePassword(String password, PasswordEncoder passwordEncoder) {
		this.passwordHash = passwordEncoder.encode(requireNonNull(password));
	}

	public boolean isActive() {
		return this.status == MemberStatus.ACTIVE;
	}
}
