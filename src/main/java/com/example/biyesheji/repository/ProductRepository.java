package com.example.biyesheji.repository;

import com.example.biyesheji.entity.Product;
import com.example.biyesheji.enums.ProductCategory;
import com.example.biyesheji.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 商品Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 查询未删除的商品(分页)
     */
    Page<Product> findByDeletedFalse(Pageable pageable);

    /**
     * 根据分类查询商品
     */
    Page<Product> findByCategoryAndDeletedFalse(ProductCategory category, Pageable pageable);

    /**
     * 根据状态查询商品
     */
    Page<Product> findByStatusAndDeletedFalse(ProductStatus status, Pageable pageable);

    /**
     * 根据用户ID查询商品
     */
    Page<Product> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    /**
     * 搜索商品(标题或描述包含关键词)
     */
    @Query("SELECT p FROM Product p WHERE (p.title LIKE %?1% OR p.description LIKE %?1%) AND p.deleted = false")
    Page<Product> searchProducts(String keyword, Pageable pageable);

    /**
     * 多条件查询商品
     */
    @Query("SELECT p FROM Product p WHERE " +
            "(?1 IS NULL OR p.category = ?1) AND " +
            "(?2 IS NULL OR p.status = ?2) AND " +
            "(?3 IS NULL OR p.title LIKE %?3% OR p.description LIKE %?3%) AND " +
            "(?4 IS NULL OR p.userId = ?4) AND " +
            "p.deleted = false")
    Page<Product> queryProducts(ProductCategory category, ProductStatus status, String keyword, Long userId, Pageable pageable);

    /**
     * 统计未删除的商品数量
     */
    long countByDeletedFalse();

    /**
     * 根据标题或描述模糊查询
     */
    Page<Product> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);
}
