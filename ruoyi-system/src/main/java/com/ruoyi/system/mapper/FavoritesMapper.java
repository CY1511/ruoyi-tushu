package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Favorites;

@Mapper
public interface FavoritesMapper {
    List<Favorites> selectFavoritesList(Long userId);
    Favorites selectFavoritesById(Long id);
    Favorites selectFavoritesByUserBook(Long userId, Long bookId);
    int insertFavorites(Favorites favorites);
    int deleteFavoritesById(Long id);
    int deleteFavoritesByUserBook(Long userId, Long bookId);
    int countFavorites(Long bookId);
}