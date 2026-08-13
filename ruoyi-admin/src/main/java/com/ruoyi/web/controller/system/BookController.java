package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Book;
import com.ruoyi.system.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private IBookService bookService;

    @GetMapping("/list")
    public R getBookList(Book book) {
        List<Book> books = bookService.selectBookList(book);
        return R.ok(books);
    }

    @GetMapping("/{id}")
    public R getBookById(@PathVariable Long id) {
        Book book = bookService.selectBookById(id);
        if (book != null) {
            bookService.updateBookViews(id);
        }
        return R.ok(book);
    }

    @PostMapping("/add")
    public R addBook(@RequestBody Book book) {
        bookService.insertBook(book);
        return R.ok("发布成功");
    }

    @PutMapping("/update")
    public R updateBook(@RequestBody Book book) {
        bookService.updateBook(book);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public R deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return R.ok("删除成功");
    }

    @PutMapping("/status")
    public R updateBookStatus(@RequestBody Book book) {
        bookService.updateBookStatus(book.getId(), book.getStatus());
        return R.ok("状态更新成功");
    }

    @GetMapping("/hot")
    public R getHotBooks() {
        List<Book> books = bookService.selectHotBooks();
        return R.ok(books);
    }

    @GetMapping("/recommend")
    public R getRecommendBooks() {
        List<Book> books = bookService.selectRecommendBooks();
        return R.ok(books);
    }

    @GetMapping("/my")
    public R getMyBooks(@RequestParam Long sellerId) {
        List<Book> books = bookService.selectMyBooks(sellerId);
        return R.ok(books);
    }

    @GetMapping("/related")
    public R getRelatedBooks(@RequestParam Long categoryId, @RequestParam Long excludeId) {
        List<Book> books = bookService.selectRelatedBooks(categoryId, excludeId);
        return R.ok(books);
    }
}