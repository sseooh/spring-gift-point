package gift.web.dto.response.productoption;

import gift.domain.ProductOption;

public class SubtractProductOptionQuantityResponse {

    private final Long id;

    private final String name;

    private final Integer stock;

    public SubtractProductOptionQuantityResponse(Long id, String name, Integer stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public static SubtractProductOptionQuantityResponse fromEntity(ProductOption productOption) {
        return new SubtractProductOptionQuantityResponse(productOption.getId(), productOption.getName(), productOption.getStock());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStock() {
        return stock;
    }
}