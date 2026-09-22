package com.ruoyi.system.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IdleGoodsMapper;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.service.IIdleGoodsService;

/**
 * 商品信息Service业务层处理
 *
 * <p><b>缓存策略（cache-aside + 主动失效）：</b></p>
 * <ul>
 *   <li>读：{@link #selectGoodsDetail(Long)} 先读 Redis，未命中再查库并回填
 *       （key：{@code idleGoods:{goodsId}}，TTL 10 分钟兜底）；</li>
 *   <li>写：所有写路径（修改、审核、上下架、被下单、删除）都<b>主动删除</b>这条缓存，
 *       避免"商品已经下架，详情页还能看到"的脏窗口；</li>
 *   <li>TTL 只做兜底：万一某条写路径漏了删除，最多脏 10 分钟。</li>
 * </ul>
 *
 * <p><b>为什么用 RedisCache 而不是 @Cacheable / @CacheEvict：</b>
 * 本机环境是 Redis 3.2（Windows 版）+ Spring Data Redis 4，实测
 * {@code RedisCache.evict()/clear()} 是<b>静默空操作</b>（用代码探针验证过：
 * cache.get 命中为 true，evict 之后 key 仍在 Redis 里，而 RedisTemplate.delete 正常）。
 * 换成框架自带的 {@link RedisCache} 后读写删三条路径都验证通过，
 * 并且在任何 Redis 版本上行为一致。</p>
 *
 * @author ruoyi
 * @date 2026-07-12
 */
@Service
public class IdleGoodsServiceImpl implements IIdleGoodsService 
{
    /** 商品详情缓存 key 前缀，完整 key 形如 idleGoods:1001 */
    private static final String CACHE_KEY_PREFIX = "idleGoods:";

    /** 商品详情缓存时长（分钟），仅兜底，正确性靠写路径主动删除 */
    private static final int CACHE_TTL_MINUTES = 10;

    @Autowired
    private IdleGoodsMapper idleGoodsMapper;

    @Autowired
    private RedisCache redisCache;

    // =========================================================================
    // 商品详情缓存
    // =========================================================================

    /**
     * 查询商品详情（cache-aside：先查缓存，未命中查库并回填）
     *
     * <p>缓存的是<b>原始行数据</b>，不做可见性判断 —— 可见性和当前登录用户有关，
     * 属于请求级逻辑，不能进缓存。</p>
     *
     * @param goodsId 商品信息主键
     * @return 商品信息，不存在返回 null
     */
    @Override
    public IdleGoods selectGoodsDetail(Long goodsId)
    {
        if (goodsId == null)
        {
            return null;
        }
        String key = CACHE_KEY_PREFIX + goodsId;
        IdleGoods cached = redisCache.getCacheObject(key);
        if (cached != null)
        {
            return cached;
        }
        IdleGoods goods = idleGoodsMapper.selectIdleGoodsByGoodsId(goodsId);
        if (goods != null)
        {
            redisCache.setCacheObject(key, goods, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        return goods;
    }

    /**
     * 删除单个商品详情缓存
     */
    private void evictGoodsCache(Long goodsId)
    {
        if (goodsId == null)
        {
            return;
        }
        redisCache.deleteObject(CACHE_KEY_PREFIX + goodsId);
    }

    /**
     * 清空商品详情缓存（批量操作场景）
     */
    private void clearGoodsCache()
    {
        Collection<String> keys = redisCache.keys(CACHE_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty())
        {
            redisCache.deleteObject(keys);
        }
    }

    /**
     * 查询商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 商品信息
     */
    @Override
    public IdleGoods selectIdleGoodsByGoodsId(Long goodsId)
    {
        return idleGoodsMapper.selectIdleGoodsByGoodsId(goodsId);
    }

    /**
     * 查询商品信息列表
     * 
     * @param idleGoods 商品信息
     * @return 商品信息
     */
    @Override
    public List<IdleGoods> selectIdleGoodsList(IdleGoods idleGoods)
    {
        return idleGoodsMapper.selectIdleGoodsList(idleGoods);
    }

    /**
     * 新增商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    @Override
    public int insertIdleGoods(IdleGoods idleGoods)
    {
        idleGoods.setCreateTime(DateUtils.getNowDate());
        return idleGoodsMapper.insertIdleGoods(idleGoods);
    }

    /**
     * 修改商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    @Override
    public int updateIdleGoods(IdleGoods idleGoods)
    {
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        int rows = idleGoodsMapper.updateIdleGoods(idleGoods);
        evictGoodsCache(idleGoods.getGoodsId());
        return rows;
    }

    /**
     * 批量删除商品信息
     * 
     * @param goodsIds 需要删除的商品信息主键
     * @return 结果
     */
    @Override
    public int deleteIdleGoodsByGoodsIds(Long[] goodsIds)
    {
        int rows = idleGoodsMapper.deleteIdleGoodsByGoodsIds(goodsIds);
        clearGoodsCache();
        return rows;
    }

    /**
     * 删除商品信息信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    @Override
    public int deleteIdleGoodsByGoodsId(Long goodsId)
    {
        int rows = idleGoodsMapper.deleteIdleGoodsByGoodsId(goodsId);
        evictGoodsCache(goodsId);
        return rows;
    }

    /**
     * 审核商品信息
     * 
     * @param idleGoods 商品信息
     * @return 结果
     */
    @Override
    public int auditIdleGoods(IdleGoods idleGoods)
    {
        // audit_user 存的是审核人ID：IdleGoodsMapper 用 `g.audit_user = u2.user_id` 关联 sys_user
        // 取用户名，前端审核人下拉框传的也是 userId。原来写入 SecurityUtils.getUsername()，
        // 与关联条件语义相反，导致列表里「审核人名称」永远为空。
        idleGoods.setAuditUser(String.valueOf(SecurityUtils.getUserId()));
        idleGoods.setAuditUserName(SecurityUtils.getUsername());
        idleGoods.setAuditTime(DateUtils.getNowDate());
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        if (IdleGoods.AUDIT_PASSED.equals(idleGoods.getAuditStatus())) {
            idleGoods.setGoodsStatus(IdleGoods.STATUS_ON_SALE);
        } else if (IdleGoods.AUDIT_REJECTED.equals(idleGoods.getAuditStatus())) {
            idleGoods.setGoodsStatus(IdleGoods.STATUS_OFFLINE);
        }
        int rows = idleGoodsMapper.updateIdleGoods(idleGoods);
        evictGoodsCache(idleGoods.getGoodsId());
        return rows;
    }

    /**
     * 上架商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    @Override
    public int onlineIdleGoods(Long goodsId)
    {
        IdleGoods idleGoods = new IdleGoods();
        idleGoods.setGoodsId(goodsId);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_ON_SALE);
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        int rows = idleGoodsMapper.updateIdleGoods(idleGoods);
        evictGoodsCache(goodsId);
        return rows;
    }

    /**
     * 下架商品信息
     * 
     * @param goodsId 商品信息主键
     * @return 结果
     */
    @Override
    public int offlineIdleGoods(Long goodsId)
    {
        IdleGoods idleGoods = new IdleGoods();
        idleGoods.setGoodsId(goodsId);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_OFFLINE);
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        int rows = idleGoodsMapper.updateIdleGoods(idleGoods);
        evictGoodsCache(goodsId);
        return rows;
    }

    @Override
    public int countGoodsByPublisherId(Long publisherId)
    {
        return idleGoodsMapper.countGoodsByPublisherId(publisherId);
    }

    // =========================================================================
    // C 端接口实现（归属校验收口在这一层）
    // =========================================================================

    /**
     * 发布商品（C 端）
     */
    @Override
    public int publishGoods(IdleGoods idleGoods)
    {
        // 发布人只认当前登录用户，请求体里传什么都不算
        idleGoods.setGoodsId(null);
        idleGoods.setPublisherId(SecurityUtils.getUserId());
        idleGoods.setAuditStatus(IdleGoods.AUDIT_PENDING);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_PENDING);
        idleGoods.setViewCount(0L);
        idleGoods.setCollectCount(0L);
        // 审核信息由审核动作写入，发布时先清空，防止请求体伪造"已过审"
        idleGoods.setAuditUser(null);
        idleGoods.setAuditTime(null);
        idleGoods.setAuditRemark(null);
        idleGoods.setCreateBy(SecurityUtils.getUsername());
        return insertIdleGoods(idleGoods);
    }

    /**
     * 修改我发布的商品（C 端）
     */
    @Override
    public int updateMyGoods(IdleGoods idleGoods)
    {
        IdleGoods existing = requireOwnerGoods(idleGoods.getGoodsId());
        // 归属不可改：用库里的值覆盖请求体，防止把商品"过继"给别人
        idleGoods.setPublisherId(existing.getPublisherId());
        // 商品信息变更后需要重新审核
        idleGoods.setAuditStatus(IdleGoods.AUDIT_PENDING);
        idleGoods.setGoodsStatus(IdleGoods.STATUS_PENDING);
        // 浏览量、收藏量是系统累加值，不允许客户端覆盖
        idleGoods.setViewCount(null);
        idleGoods.setCollectCount(null);
        idleGoods.setUpdateBy(SecurityUtils.getUsername());
        idleGoods.setUpdateTime(DateUtils.getNowDate());
        int rows = idleGoodsMapper.updateIdleGoods(idleGoods);
        evictGoodsCache(idleGoods.getGoodsId());
        return rows;
    }

    /**
     * 删除我发布的商品（C 端）
     */
    @Override
    public int deleteMyGoods(Long goodsId)
    {
        requireOwnerGoods(goodsId);
        int rows = idleGoodsMapper.deleteIdleGoodsByGoodsId(goodsId);
        evictGoodsCache(goodsId);
        return rows;
    }

    /**
     * 查询商品详情（C 端可见性规则）
     */
    @Override
    public IdleGoods selectVisibleGoodsByGoodsId(Long goodsId)
    {
        IdleGoods goods = selectGoodsDetail(goodsId);
        if (goods == null)
        {
            throw new ServiceException("商品不存在");
        }
        boolean publicVisible = IdleGoods.AUDIT_PASSED.equals(goods.getAuditStatus())
                && !IdleGoods.STATUS_OFFLINE.equals(goods.getGoodsStatus());
        if (publicVisible)
        {
            return goods;
        }
        // 未过审 / 已下架：只有发布者本人能看（详情接口是匿名可访问的，所以这里要容错取用户）
        Long currentUserId = currentUserIdOrNull();
        if (currentUserId != null && currentUserId.equals(goods.getPublisherId()))
        {
            return goods;
        }
        throw new ServiceException("该商品尚未通过审核或已下架");
    }

    // =========================================================================
    // 商品状态流转（由订单状态机驱动）
    // 商品状态一变，缓存里的详情就过期了，所以每个方法都要失效缓存
    // =========================================================================

    @Override
    public int bookGoods(Long goodsId)
    {
        // 条件更新：只有"上架"状态才能被预订，并发下单时天然只有一个成功
        int rows = idleGoodsMapper.updateGoodsStatusIfMatch(goodsId,
                IdleGoods.STATUS_ON_SALE, IdleGoods.STATUS_BOOKED);
        evictGoodsCache(goodsId);
        return rows;
    }

    @Override
    public int markGoodsSold(Long goodsId)
    {
        int rows = idleGoodsMapper.updateGoodsStatusIfMatch(goodsId,
                IdleGoods.STATUS_BOOKED, IdleGoods.STATUS_SOLD);
        evictGoodsCache(goodsId);
        return rows;
    }

    @Override
    public int releaseGoods(Long goodsId)
    {
        int rows = idleGoodsMapper.updateGoodsStatusIfMatch(goodsId,
                IdleGoods.STATUS_BOOKED, IdleGoods.STATUS_ON_SALE);
        evictGoodsCache(goodsId);
        return rows;
    }

    /**
     * 校验商品归属：不存在或不是当前用户的商品，一律拒绝
     *
     * @param goodsId 商品ID
     * @return 数据库中的商品
     */
    private IdleGoods requireOwnerGoods(Long goodsId)
    {
        if (goodsId == null)
        {
            throw new ServiceException("商品ID不能为空");
        }
        IdleGoods goods = idleGoodsMapper.selectIdleGoodsByGoodsId(goodsId);
        if (goods == null)
        {
            throw new ServiceException("商品不存在");
        }
        Long currentUserId = SecurityUtils.getUserId();
        if (!currentUserId.equals(goods.getPublisherId()))
        {
            throw new ServiceException("无权操作他人发布的商品", HttpStatus.FORBIDDEN.value());
        }
        return goods;
    }

    /**
     * 取当前登录用户ID，未登录返回 null（匿名可访问的接口用）
     */
    private Long currentUserIdOrNull()
    {
        try
        {
            return SecurityUtils.getUserId();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
