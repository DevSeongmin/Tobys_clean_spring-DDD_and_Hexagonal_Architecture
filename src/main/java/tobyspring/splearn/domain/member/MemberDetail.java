package tobyspring.splearn.domain.member;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.util.Assert;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tobyspring.splearn.domain.AbstractEntity;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail extends AbstractEntity {
	private Profile profile;

	private String introduction;

	private LocalDateTime registerAt;

	private LocalDateTime activatedAt;

	private LocalDateTime deactivatedAt;

	static MemberDetail create() {
		MemberDetail memberDetail = new MemberDetail();
		memberDetail.registerAt = LocalDateTime.now();
		return memberDetail;
	}

	void activate() {
		Assert.isTrue(activatedAt == null, "이미 활성화된 아이디 입니다.");
		this.activatedAt = LocalDateTime.now();
	}

	void deactivate() {
		Assert.isTrue(deactivatedAt == null, "이미 비활성화 된 아이디입니다.");
		this.deactivatedAt = LocalDateTime.now();
	}

	void updateInfo(MemberInfoUpdateRequest updateRequest) {
		this.profile = new Profile(updateRequest.profileAddress());
		this.introduction = Objects.requireNonNull(updateRequest.introduction());
	}
}
