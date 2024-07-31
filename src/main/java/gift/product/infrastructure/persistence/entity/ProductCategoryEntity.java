package gift.product.infrastructure.persistence.entity;

import gift.core.BaseEntity;
import gift.core.domain.product.ProductCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "product_category")
public class ProductCategoryEntity extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "description", nullable = false)
    private String description;

    protected ProductCategoryEntity() {
    }

    protected ProductCategoryEntity(Long id, String name) {
        super(id);
        this.name = name;
    }

    protected ProductCategoryEntity(String name) {
        this.name = name;
    }

    public static ProductCategoryEntity fromDomain(ProductCategory productCategory) {
        return new ProductCategoryEntity(productCategory.id(), productCategory.name());
    }

    public String getName() {
        return name;
    }

    public ProductCategory toDomain() {
        return new ProductCategory(getId(), name, color, imageUrl, description);
    }
}