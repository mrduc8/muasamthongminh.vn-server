package mstm.muasamthongminh.muasamthongminh.common.mailcontent;

public class RegisterAccount {
    public static String emailVerify(String email) {
        return """
                <h1>Chào %s,</h1>
                  <p>Cảm ơn bạn đã đăng ký tại <strong>Mua Sắm Thông Minh</strong>. Vui lòng xác nhận email của bạn để hoàn tất quá trình đăng ký.</p>
                  <p><strong>Ngân hàng:</strong> %s</p>
                  <p><strong>Số tài khoản:</strong> %s</p>
                  <p>Bấm nút bên dưới để xác minh địa chỉ email của bạn:</p>
                  <a href="%s" class="button" aria-label="Xác thực email">Xác thực Email</a>
                  <p>Nếu bạn không thực hiện hành động này, vui lòng liên hệ Admin ngay.</p>
                  <p>Trân trọng,</p>
                  <p>Đội ngũ hỗ trợ Mua Sắm Thông Minh</p>
                  <footer>
                    <p>© 2025 Mua Sắm Thông Minh. Bảo lưu mọi quyền.</p>
                """;
    }
}
