package com.sage.application;

import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;

import java.util.UUID;

/**
 * Created by tamar.twena on 5/3/2016.
 */
public class RecipeCategoryContainer {

    private UUID id;

    public RecipeCategoryContainer() {
        id = UUID.randomUUID();
    }

    private RecipeDetails details;

    private RecipeCategory category;


    public RecipeDetails getDetails() {
        return details;
    }

    public void setDetails(RecipeDetails details) {
        this.details = details;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public UUID getId() {
        return id;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        RecipeCategoryContainer other = (RecipeCategoryContainer) obj;
        return this.id.equals(other.id);

    }

    @Override
    public int hashCode() {
        return (int) id.hashCode();
    }

}
