package org.example.auctioncommon.domain.categories.repository

import org.example.auctioncommon.domain.categories.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@org.springframework.stereotype.Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByParentIsNull(): List<Category>

    fun findByParent_CategoryId(parentId: Long?): List<Category>

    @Modifying(clearAutomatically = true)
    @Query(
        "UPDATE categories c SET c.path = REPLACE(c.path, :oldPrefix, :newPrefix) " +
                "WHERE c.path LIKE CONCAT(:oldPrefix, '%')"
    )
    fun updatePathPrefix(@Param("oldPrefix") oldPrefix: String,
                         @Param("newPrefix") newPrefix: String)
}
