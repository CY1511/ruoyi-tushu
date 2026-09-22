package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品信息对象 idle_goods
 *
 * @author ruoyi
 * @date 2026-07-12
 */
public class IdleGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 审核状态：待审核 */
    public static final String AUDIT_PENDING  = "0";
    /** 审核状态：通过 */
    public static final String AUDIT_PASSED   = "1";
    /** 审核状态：驳回 */
    public static final String AUDIT_REJECTED = "2";

    /** 商品状态：待审核 */
    public static final String STATUS_PENDING  = "0";
    /** 商品状态：已上架 */
    public static final String STATUS_ON_SALE  = "1";
    /** 商品状态：已预订 */
    public static final String STATUS_BOOKED   = "2";
    /** 商品状态：已售出 */
    public static final String STATUS_SOLD     = "3";
    /** 商品状态：已下架 */
    public static final String STATUS_OFFLINE  = "4";

    /** 商品ID */
    private Long goodsId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 分类名称（非数据库字段） */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String goodsName;

    /** 封面图片 */
    @Excel(name = "封面图片")
    private String coverImage;

    /** 售价 */
    @Excel(name = "售价")
    private BigDecimal price;

    /** 原价 */
    @Excel(name = "原价")
    private BigDecimal originalPrice;

    /** 成色 */
    @Excel(name = "成色")
    private String qualityLevel;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** ISBN编号 */
    @Excel(name = "ISBN编号")
    private String isbn;

    /** 商品描述 */
    @Excel(name = "商品描述")
    private String description;

    /** 发布人ID */
    @Excel(name = "发布人ID")
    private Long publisherId;

    /** 发布人名称（非数据库字段） */
    @Excel(name = "发布人")
    private String publisherName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 交易地点 */
    @Excel(name = "交易地点")
    private String tradeAddress;

    /** 浏览量 */
    @Excel(name = "浏览量")
    private Long viewCount;

    /** 收藏量 */
    @Excel(name = "收藏量")
    private Long collectCount;

    /** 审核状态（0待审核 1通过 2驳回） */
    @Excel(name = "审核状态", readConverterExp = "0=待审核,1=通过,2=驳回")
    private String auditStatus;

    /** 商品状态（0待审核 1上架 2已预订 3已售出 4已下架） */
    @Excel(name = "商品状态", readConverterExp = "0=待审核,1=上架,2=已预订,3=已售出,4=已下架")
    private String goodsStatus;

    /** 审核人 */
    @Excel(name = "审核人")
    private String auditUser;

    /** 审核人名称（非数据库字段） */
    @Excel(name = "审核人名称")
    private String auditUserName;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String auditRemark;

    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId()
    {
        return goodsId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setCoverImage(String coverImage)
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage()
    {
        return coverImage;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setOriginalPrice(BigDecimal originalPrice)
    {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOriginalPrice()
    {
        return originalPrice;
    }

    public void setQualityLevel(String qualityLevel)
    {
        this.qualityLevel = qualityLevel;
    }

    public String getQualityLevel()
    {
        return qualityLevel;
    }

    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setPublisherId(Long publisherId)
    {
        this.publisherId = publisherId;
    }

    public Long getPublisherId()
    {
        return publisherId;
    }

    public String getPublisherName()
    {
        return publisherName;
    }

    public void setPublisherName(String publisherName)
    {
        this.publisherName = publisherName;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setTradeAddress(String tradeAddress)
    {
        this.tradeAddress = tradeAddress;
    }

    public String getTradeAddress()
    {
        return tradeAddress;
    }

    public void setViewCount(Long viewCount)
    {
        this.viewCount = viewCount;
    }

    public Long getViewCount()
    {
        return viewCount;
    }

    public void setCollectCount(Long collectCount)
    {
        this.collectCount = collectCount;
    }

    public Long getCollectCount()
    {
        return collectCount;
    }

    public void setAuditStatus(String auditStatus)
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus()
    {
        return auditStatus;
    }

    public void setGoodsStatus(String goodsStatus)
    {
        this.goodsStatus = goodsStatus;
    }

    public String getGoodsStatus()
    {
        return goodsStatus;
    }

    public void setAuditUser(String auditUser)
    {
        this.auditUser = auditUser;
    }

    public String getAuditUser()
    {
        return auditUser;
    }

    public String getAuditUserName()
    {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName)
    {
        this.auditUserName = auditUserName;
    }

    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime()
    {
        return auditTime;
    }

    public void setAuditRemark(String auditRemark)
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark()
    {
        return auditRemark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("goodsId", getGoodsId())
                .append("categoryId", getCategoryId())
                .append("categoryName", getCategoryName())
                .append("goodsName", getGoodsName())
                .append("coverImage", getCoverImage())
                .append("price", getPrice())
                .append("originalPrice", getOriginalPrice())
                .append("qualityLevel", getQualityLevel())
                .append("courseName", getCourseName())
                .append("isbn", getIsbn())
                .append("description", getDescription())
                .append("publisherId", getPublisherId())
                .append("publisherName", getPublisherName())
                .append("contactPhone", getContactPhone())
                .append("tradeAddress", getTradeAddress())
                .append("viewCount", getViewCount())
                .append("collectCount", getCollectCount())
                .append("auditStatus", getAuditStatus())
                .append("goodsStatus", getGoodsStatus())
                .append("auditUser", getAuditUser())
                .append("auditUserName", getAuditUserName())
                .append("auditTime", getAuditTime())
                .append("auditRemark", getAuditRemark())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}