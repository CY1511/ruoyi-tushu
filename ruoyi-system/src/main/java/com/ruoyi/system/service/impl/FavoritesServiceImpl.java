package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Favorites;
import com.ruoyi.system.mapper.FavoritesMapper;
import com.ruoyi.system.service.IBookService;
import com.ruoyi.system.service.IFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoritesServiceImpl implements IFavoritesService {

    @Autowired
    private FavoritesMapper favoritesMapper;

    @Autowired
    private IBookService bookService;

    @Override
    public List<Favorites> selectFavoritesList(Long userId) {
        return favoritesMapper.selectFavoritesList(userId);
    }

    @Override
    public Favorites selectFavoritesByUserBook(Long userId, Long bookId) {
        return favoritesMapper.selectFavoritesByUserBook(userId, bookId);
    }

    @Override
    @Transactional
    public int insertFavorites(Favorites favorites) {
        bookService.updateBookFavorites(favorites.getBookId(), 1);
        return favoritesMapper.insertFavorites(favorites);
    }

    @Override
    @Transactional
    public int deleteFavoritesById(Long id) {
        Favorites favorites = favoritesMapper.selectFavoritesById(id);
        if (favorites != null) {
            bookService.updateBookFavorites(favorites.getBookId(), -1);
        }
        return favoritesMapper.deleteFavoritesById(id);
    }

    @Override
    @Transactional
    public int deleteFavoritesByUserBook(Long userId, Long bookId) {
        bookService.updateBookFavorites(bookId, -1);
        return favoritesMapper.deleteFavoritesByUserBook(userId, bookId);
    }

    @Override
    public int countFavorites(Long bookId) {
        return favoritesMapper.countFavorites(bookId);
    }

    @Override
    @Transactional
    public int toggleFavorites(Long userId, Long bookId) {
        Favorites favorites = selectFavoritesByUserBook(userId, bookId);
        if (favorites != null) {
            return deleteFavoritesByUserBook(userId, bookId);
        } else {
            Favorites newFavorites = new Favorites();
            newFavorites.setUserId(userId);
            newFavorites.setBookId(bookId);
            return insertFavorites(newFavorites);
        }
    }
}