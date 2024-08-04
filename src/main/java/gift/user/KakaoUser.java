package gift.user;

import gift.point.PointDTO;
import gift.wishList.WishList;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KAKAOUSERS")
public class KakaoUser implements IntegratedUser {
    @Id
    Long id;
    @Column
    String accessToken;
    @Column
    String refreshToken;
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "kakaouser", orphanRemoval = true)
    private List<WishList> wishLists = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private Long point = 0L;

    public KakaoUser() {
    }

    public KakaoUser(Long id, String accessToken, String refreshToken) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.point = 0L;
    }

    public void addWishList(WishList wishList) {
        this.wishLists.add(wishList);
        wishList.setKakaouser(this);
    }

    public void removeWishList(WishList wishList) {
        wishList.setKakaouser(null);
        this.wishLists.remove(wishList);
    }

    public void removeWishLists() {
        for (WishList wishList : wishLists) {
            removeWishList(wishList);
        }
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getPoint() {
        return point;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    public void chargePoint(Long point){
        this.point += point;
    }

    public void usePoint(Long point){
        this.point -= point;
    }
}
