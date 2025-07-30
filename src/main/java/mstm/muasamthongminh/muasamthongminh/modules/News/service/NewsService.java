package mstm.muasamthongminh.muasamthongminh.modules.News.service;

import mstm.muasamthongminh.muasamthongminh.modules.News.model.News;
import mstm.muasamthongminh.muasamthongminh.modules.News.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public News getNewsById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
    }

    public News createNews(News news) {
        return newsRepository.save(news);
    }

    public News updateNews(Long id,News updatedNews) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("New not found"));

        existingNews.setTitle(updatedNews.getTitle());
        existingNews.setSlug(updatedNews.getSlug());
        existingNews.setContent(updatedNews.getContent());
        existingNews.setImage(updatedNews.getImage());
        existingNews.setStatus(updatedNews.getStatus());
        existingNews.setCreatedAt(updatedNews.getCreatedAt());

        return newsRepository.save(existingNews);
    }

    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("News not found");
        }
        newsRepository.deleteById(id);
    }

}
