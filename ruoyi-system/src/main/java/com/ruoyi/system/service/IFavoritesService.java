package com.ruoyi.system.service;

import com.ruoyi.system.domain.Favorites;

import java.util.List;

public interface IFavoritesService {

    List<Favorites> selectFavoritesList(Long userId);

    Favorites selectFavoritesByUserBook(Long userId, Long bookId);

    int insertFavorites(Favorites favorites);

    int deleteFavoritesById(Long id);

    int deleteFavoritesByUserBook(Long userId, Long bookId);

    int countFavorites(Long bookId);

    int toggleFavorites(Long userId, Long bookId);
}