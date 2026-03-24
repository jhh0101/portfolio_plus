package com.portfolio.auctionmarket.domain.categories.repository;

import com.portfolio.auctionmarket.domain.categories.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIsNull();

    List<Category> findByParent_CategoryId(Long parentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE categories c SET c.path = REPLACE(c.path, :oldPrefix, :newPrefix) " +
    "WHERE c.path LIKE CONCAT(:oldPrefix, '%')")
    void updatePathPrefix(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);
}
