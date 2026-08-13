package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Book;
import com.ruoyi.system.mapper.BookMapper;
import com.ruoyi.system.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements IBookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<Book> selectBookList(Book book) {
        return bookMapper.selectBookList(book);
    }

    @Override
    public Book selectBookById(Long id) {
        return bookMapper.selectBookById(id);
    }

    @Override
    @Transactional
    public int insertBook(Book book) {
        book.setStatus(1);
        book.setSales(0);
        book.setViews(0);
        book.setFavorites(0);
        book.setCreateTime(DateUtils.getNowDate());
        book.setUpdateTime(DateUtils.getNowDate());
        return bookMapper.insertBook(book);
    }

    @Override
    @Transactional
    public int updateBook(Book book) {
        book.setUpdateTime(DateUtils.getNowDate());
        return bookMapper.updateBook(book);
    }

    @Override
    @Transactional
    public int deleteBookById(Long id) {
        return bookMapper.deleteBookById(id);
    }

    @Override
    @Transactional
    public int updateBookStatus(Long id, Integer status) {
        return bookMapper.updateBookStatus(id, status);
    }

    @Override
    @Transactional
    public int updateBookViews(Long id) {
        return bookMapper.updateBookViews(id);
    }

    @Override
    @Transactional
    public int updateBookFavorites(Long id, Integer delta) {
        return bookMapper.updateBookFavorites(id, delta);
    }

    @Override
    public List<Book> selectHotBooks() {
        return bookMapper.selectHotBooks();
    }

    @Override
    public List<Book> selectRecommendBooks() {
        return bookMapper.selectRecommendBooks();
    }

    @Override
    public List<Book> selectMyBooks(Long sellerId) {
        return bookMapper.selectMyBooks(sellerId);
    }

    @Override
    public List<Book> selectRelatedBooks(Long categoryId, Long excludeId) {
        return bookMapper.selectRelatedBooks(categoryId, excludeId);
    }
}