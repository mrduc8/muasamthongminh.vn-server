package mstm.muasamthongminh.muasamthongminh.modules.News.repository;

import mstm.muasamthongminh.muasamthongminh.modules.News.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

}
