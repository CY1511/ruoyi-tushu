package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Book;

public interface BookMapper {
    List<Book> selectBookList(Book book);
    Book selectBookById(Long id);
    int insertBook(Book book);
    int updateBook(Book book);
    int deleteBookById(Long id);
    int updateBookStatus(Long id, Integer status);
    int updateBookViews(Long id);
    int updateBookFavorites(Long id, Integer delta);
    List<Book> selectHotBooks();
    List<Book> selectRecommendBooks();
    List<Book> selectMyBooks(Long sellerId);
    List<Book> selectRelatedBooks(Long categoryId, Long excludeId);
}