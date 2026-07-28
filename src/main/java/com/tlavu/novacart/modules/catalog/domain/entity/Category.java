package com.tlavu.novacart.modules.catalog.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    private Long id;
    private String name;
    private String description;
    private String slug;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public static Category create(
            String name,
            String description,
            String slug
    ) {

        return new Category(
                null,
                name,
                description,
                slug,
                true,
                null,
                null,
                null
        );
    }

    public void rename(String name, String slug) {

        this.name = name;
        this.slug = slug;
    }

    public void changeDescription(String description) {

        this.description = description;
    }

    public void changeActive(boolean active) {

        this.active = active;
    }

    public void softDelete(Instant deletedAt) {

        this.active = false;
        this.deletedAt = deletedAt;
    }
}
