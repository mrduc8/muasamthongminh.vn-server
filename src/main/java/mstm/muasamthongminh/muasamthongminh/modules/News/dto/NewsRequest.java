package mstm.muasamthongminh.muasamthongminh.modules.News.dto;

import lombok.Data;

@Data
public class NewsRequest {
    private String title;
    private String content;
    private String image;
    private String status;
}
