package gift.domain.product.service;

import gift.domain.category.entity.Category;
import gift.domain.category.exception.CategoryNotFoundException;
import gift.domain.category.repository.CategoryRepository;
import gift.domain.option.service.OptionService;
import gift.domain.product.dto.ProductDetailResponse;
import gift.domain.product.dto.ProductRequest;
import gift.domain.product.dto.ProductResponse;
import gift.domain.product.entity.Product;
import gift.domain.product.exception.ProductNotFoundException;
import gift.domain.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private final OptionService optionService;

    public ProductService(ProductRepository productRepository,
        CategoryRepository categoryRepository,
        OptionService optionService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.optionService = optionService;
    }

    public ProductDetailResponse getProduct(Long id) {
        Product product = productRepository
            .findById(id)
            .orElseThrow(() -> new ProductNotFoundException("찾는 상품이 존재하지 않습니다."));

        return new ProductDetailResponse(product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl(),
            optionService.getProductOptions(id));
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable, Long categoryId) {
        if (categoryId == null) {
            return productRepository.findAll(pageable).map(this::entityToDto);
        }

        Category findCategory = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리가 존재하지 않습니다."));
        return productRepository.findAllByCategory(pageable, findCategory).map(this::entityToDto);
    }

    @Transactional
    public void createProduct(ProductRequest productRequest) {
        Category savedCategory = categoryRepository.findById(productRequest.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("해당 카테고리가 존재하지 않습니다."));
        Product savedProduct = productRepository.save(dtoToEntity(productRequest, savedCategory));
        optionService.addOptionToProduct(savedProduct.getId(), productRequest.getOptionRequest());

    }

    @Transactional
    public void updateProduct(Long id, ProductRequest productRequest) {
        Product savedProduct = productRepository
            .findById(id)
            .orElseThrow(() -> new ProductNotFoundException("찾는 상품이 존재하지 않습니다."));

        Category savedCategory = categoryRepository.findById(productRequest.getCategoryId())
            .orElseThrow();

        savedProduct.updateAll(productRequest.getName(), productRequest.getPrice(),
            productRequest.getImageUrl(), savedCategory);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product savedProduct = productRepository
            .findById(id)
            .orElseThrow(() -> new ProductNotFoundException("찾는 상품이 존재하지 않습니다."));

        productRepository.delete(savedProduct);
    }

    private ProductResponse entityToDto(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(),
            product.getImageUrl(), product.getCategory().getId());
    }

    private Product dtoToEntity(ProductRequest productRequest, Category category) {

        return new Product(productRequest.getName(), productRequest.getPrice(),
            productRequest.getImageUrl(), category);
    }

}