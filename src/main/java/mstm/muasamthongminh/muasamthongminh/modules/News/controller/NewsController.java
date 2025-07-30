package mstm.muasamthongminh.muasamthongminh.modules.News.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import mstm.muasamthongminh.muasamthongminh.modules.News.model.News;
import mstm.muasamthongminh.muasamthongminh.modules.News.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/new")
public class NewsController {
    private final NewsService newsService;
    private final Cloudinary cloudinary;

    public NewsController(NewsService newsService, Cloudinary cloudinary) {
        this.newsService = newsService;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/{id}")
    public News getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id);
    }

    @PostMapping
    public News createNews(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("status") String status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            imageUrl = uploadResult.get("secure_url").toString();
        }

        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setStatus(status);
        news.setImage(imageUrl);

        return newsService.createNews(news);
    }

    @PostMapping("/{id}")
    public News updateNews(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("status") String status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            imageUrl = uploadResult.get("secure_url").toString();
        }

        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setStatus(status);
        news.setImage(imageUrl);

        return newsService.updateNews(id, news);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok("Xóa tin tức thành công");
    }
}
