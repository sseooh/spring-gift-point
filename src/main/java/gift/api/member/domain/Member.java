package gift.api.member.domain;

import gift.api.member.enums.Role;
import gift.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_member", columnNames = {"email"}))
public class Member extends BaseEntity {
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    private String kakaoAccessToken;

    protected Member() {
    }

    public Member(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void saveKakaoToken(String accessToken) {
        this.kakaoAccessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    @Override
    public String toString() {
        return "Member{" +
            "id=" + getId() +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", role=" + role +
            '}';
    }
}
