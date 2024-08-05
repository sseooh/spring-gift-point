package gift.member.domain;

import gift.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    private boolean isOauthAccount;

    @Embedded
    private Point point;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    protected Member() {
    }

    public Member(String username, String password, boolean oAuthAccount) {
        this.username = username;
        this.password = password;
        this.isOauthAccount = oAuthAccount;
        this.role = Role.USER;
        this.point = Point.of(0);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isOauthAccount() {
        return isOauthAccount;
    }

    public boolean isNotOAuthAccount() {
        return !isOauthAccount();
    }

    public void usePoint(Integer value) {
        point.use(value);
    }

    public Integer accumulatePoint(Integer price) {
        return point.accumulate(price);
    }

    public Integer getPoint() {
        return point.getValue();
    }
}