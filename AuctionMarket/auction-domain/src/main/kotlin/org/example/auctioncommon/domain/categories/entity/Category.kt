package org.example.auctioncommon.domain.categories.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity(name = "categories")
@Table(
    name = "categories",
    indexes = [Index(name = "idx_category_parent", columnList = "parent_id")]
)
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE category_id = ?")
@SQLRestriction("is_deleted = false")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    var categoryId: Long? = null,

    @Column(name = "category")
    var category: String,

    @Column(name = "path")
    var path: String? = null,

    @Column(name = "is_deleted")
    var isDeleted: String? = "false",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    var children: MutableList<Category?> = mutableListOf()

) {

    fun addChildrenCategory(children: Category) {
        this.children.add(children)
        children.parent = this
    }
}