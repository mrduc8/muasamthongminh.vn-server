package mstm.muasamthongminh.muasamthongminh.common.mailcontent;

public class NotificationAddCard {
    public static String buildCardCreationContent(String username, String cardNumber, String cardHolderName) {
        return """
                <h2>Chào %s,</h2>
                <p>Bạn vừa thêm thành công một thẻ ngân hàng ngân hàng vào hệ thống.</p>
                <p><strong>Số thẻ:</strong> %s</p>
                <p><strong>Tên thẻ:</strong> %s</p>
                <p>Nếu bạn không thực hiện hành động này, vui lòng liên hệ Admin ngay.</p>
                <br/>
                <p>Trân trọng,</p>
                <p>Đội ngũ hỗ trợ Mua Sắm Thông Minh</p>
                """.formatted(username, cardNumber, cardHolderName);
    }
}
