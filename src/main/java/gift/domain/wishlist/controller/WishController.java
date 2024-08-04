package gift.domain.wishlist.controller;

import gift.annotation.LoginMember;
import gift.domain.member.entity.Member;
import gift.domain.wishlist.dto.ProductIdRequest;
import gift.domain.wishlist.dto.WishRequest;
import gift.domain.wishlist.dto.WishResponse;
import gift.domain.wishlist.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishes")
@Tag(name = "WishController", description = "WishController API(JWT 인증 필요)")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    @Operation(summary = "전체 위시리스트 조회", description = "전체 위시리스트 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.(Page 내부 값은 WishResponse 입니다.)", content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "text/plain;charset=UTF-8")),
        @ApiResponse(responseCode = "403", description = "인가 실패", content = @Content(mediaType = "text/plain;charset=UTF-8")),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "text/plain;charset=UTF-8"))
    })
    public ResponseEntity<Page<WishResponse>> getWishes(
        @Parameter(hidden = true) @LoginMember Member member,
        @ParameterObject Pageable pageable
    ) {
        Page<WishResponse> response = wishService.getWishesByMember(member, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "위시리스트 생성", description = "위시 리스트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "요청에 성공하였습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "text/plain;charset=UTF-8")),
        @ApiResponse(responseCode = "403", description = "인가 실패", content = @Content(mediaType = "text/plain;charset=UTF-8")),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "text/plain;charset=UTF-8"))
    })
    public ResponseEntity<Void> createWish(@RequestBody ProductIdRequest productIdRequest,
        @Parameter(hidden = true) @LoginMember Member member) {
        WishRequest wishRequest = new WishRequest(member.getId(), productIdRequest.getProductId());
        wishService.createWish(wishRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/{productId}")
    @Operation(summary = "위시 리스트 삭제", description = "해당 위시 리스트를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "요청에 성공하였습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "text/plain;charset=UTF-8")),
        @ApiResponse(responseCode = "403", description = "인가 실패", content = @Content(mediaType = "text/plain;charset=UTF-8")),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "text/plain;charset=UTF-8"))
    })
    @Parameter(name = "id", description = "삭제할 위시 리스트의 ID", example = "1")
    public ResponseEntity<Void> deleteWish(@PathVariable("productId") Long id,
        @Parameter(hidden = true) @LoginMember Member member) {
        wishService.deleteWish(id, member);

        return ResponseEntity.noContent().build();
    }
}