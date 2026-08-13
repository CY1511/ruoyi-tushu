package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

public class Book extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private BigDecimal sellerRating;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String edition;
    private Integer publishYear;
    private BigDecimal originalPrice;
    private String coverImage;
    private Integer condition;
    private String conditionDesc;
    private String images;
    private BigDecimal price;
    private Integer categoryId;
    private String categoryName;
    private Long courseId;
    private String courseName;
    private String university;
    private Integer stock;
    private Integer status;
    private String description;
    private Integer sales;
    private Integer views;
    private Integer favorites;
    private Integer priceEvaluation;
    private BigDecimal suggestedPriceMin;
    private BigDecimal suggestedPriceMax;
    private Integer isFavorited;
    private String keyword;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getSellerAvatar() { return sellerAvatar; }
    public void setSellerAvatar(String sellerAvatar) { this.sellerAvatar = sellerAvatar; }
    public BigDecimal getSellerRating() { return sellerRating; }
    public void setSellerRating(BigDecimal sellerRating) { this.sellerRating = sellerRating; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
    public Integer getPublishYear() { return publishYear; }
    public void setPublishYear(Integer publishYear) { this.publishYear = publishYear; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getCondition() { return condition; }
    public void setCondition(Integer condition) { this.condition = condition; }
    public String getConditionDesc() { return conditionDesc; }
    public void setConditionDesc(String conditionDesc) { this.conditionDesc = conditionDesc; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    public Integer getFavorites() { return favorites; }
    public void setFavorites(Integer favorites) { this.favorites = favorites; }
    public Integer getPriceEvaluation() { return priceEvaluation; }
    public void setPriceEvaluation(Integer priceEvaluation) { this.priceEvaluation = priceEvaluation; }
    public BigDecimal getSuggestedPriceMin() { return suggestedPriceMin; }
    public void setSuggestedPriceMin(BigDecimal suggestedPriceMin) { this.suggestedPriceMin = suggestedPriceMin; }
    public BigDecimal getSuggestedPriceMax() { return suggestedPriceMax; }
    public void setSuggestedPriceMax(BigDecimal suggestedPriceMax) { this.suggestedPriceMax = suggestedPriceMax; }
    public Integer getIsFavorited() { return isFavorited; }
    public void setIsFavorited(Integer isFavorited) { this.isFavorited = isFavorited; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId()).append("sellerId", getSellerId()).append("title", getTitle())
                .append("price", getPrice()).append("status", getStatus()).toString();
    }
}