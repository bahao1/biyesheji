package com.example.biyesheji.service;

import com.example.biyesheji.dto.CreateProductDTO;
import com.example.biyesheji.dto.QueryProductDTO;
import com.example.biyesheji.entity.Product;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.enums.ProductStatus;
import com.example.biyesheji.repository.ProductRepository;
import com.example.biyesheji.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品Service
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 创建商品
     */
    @Transactional
    public Product createProduct(Long userId, CreateProductDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setUserId(userId);
        product.setSellerNickname(user.getNickname());
        product.setImages(dto.getImagesString());
        product.setContact(dto.getContact());
        product.setLocation(dto.getLocation());
        product.setProductCondition(dto.getProductCondition());
        product.setStatus(ProductStatus.ON_SALE);

        return productRepository.save(product);
    }

    /**
     * 查询商品列表
     */
    public Page<Product> queryProducts(QueryProductDTO dto) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(dto.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                dto.getSortBy()
        );
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize(), sort);

        return productRepository.queryProducts(
                dto.getCategory(),
                dto.getStatus(),
                dto.getKeyword(),
                dto.getUserId(),
                pageable
        );
    }

    /**
     * 根据ID获取商品详情
     */
    @Transactional
    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (product.getDeleted()) {
            throw new RuntimeException("商品已删除");
        }

        // 增加浏览量
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return product;
    }

    /**
     * 更新商品
     */
    @Transactional
    public Product updateProduct(Long userId, Long productId, CreateProductDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此商品");
        }

        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setImages(dto.getImagesString());
        product.setContact(dto.getContact());
        product.setLocation(dto.getLocation());
        product.setProductCondition(dto.getProductCondition());

        return productRepository.save(product);
    }

    /**
     * 删除商品
     */
    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此商品");
        }

        product.setDeleted(true);
        productRepository.save(product);
    }

    /**
     * 更新商品状态
     */
    @Transactional
    public void updateProductStatus(Long userId, Long productId, ProductStatus status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此商品");
        }

        product.setStatus(status);
        productRepository.save(product);
    }

    /**
     * 查询用户的商品
     */
    public Page<Product> getUserProducts(Long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return productRepository.findByUserIdAndDeletedFalse(userId, pageable);
    }
}
