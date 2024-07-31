package gift.product.application;

import gift.global.error.CustomException;
import gift.global.error.ErrorCode;
import gift.product.dao.CategoryRepository;
import gift.product.dao.ProductRepository;
import gift.product.dto.GetProductResponse;
import gift.product.dto.ProductRequest;
import gift.product.dto.ProductResponse;
import gift.product.entity.Category;
import gift.product.entity.Product;
import gift.product.util.OptionMapper;
import gift.product.util.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<GetProductResponse> getPagedProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toGetResponseDto);
    }

    @Transactional(readOnly = true)
    public GetProductResponse getProductByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toGetResponseDto)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        Product product = productRepository.save(ProductMapper.toEntity(request, category));
        product.addOptions(
                request.options()
                        .stream()
                        .map(option -> OptionMapper.toEntity(option, product))
                        .toList()
        );

        return ProductMapper.toResponseDto(product);
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    @Transactional
    public void updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        product.update(request, category);
    }

}