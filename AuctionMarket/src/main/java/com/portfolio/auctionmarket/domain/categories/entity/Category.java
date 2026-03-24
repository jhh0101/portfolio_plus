package com.portfolio.auctionmarket.domain.categories.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "categories")
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_parent", columnList = "parent_id")
})
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE category_id = ?")
@SQLRestriction("is_deleted = false")
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category")
    private String category;

    @Column(name = "path")
    private String path;

    @Column(name = "is_deleted")
    private String isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    public void addChildrenCategory(Category children) {
        this.children.add(children);
        children.setParent(this);
    }
}