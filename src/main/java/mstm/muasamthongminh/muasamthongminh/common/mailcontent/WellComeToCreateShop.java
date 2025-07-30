package mstm.muasamthongminh.muasamthongminh.common.mailcontent;

public class WellComeToCreateShop {

    public static String buildBankCreationContent(String username, String bankName, String accountNumber) {
        return """
                <h2>Chào %s,</h2>
                <p>Bạn vừa thêm thành công một tài khoản ngân hàng vào hệ thống.</p>
                <p><strong>Ngân hàng:</strong> %s</p>
                <p><strong>Số tài khoản:</strong> %s</p>
                <p>Nếu bạn không thực hiện hành động này, vui lòng liên hệ Admin ngay.</p>
                <br/>
                <p>Trân trọng,</p>
                <p>Đội ngũ hỗ trợ Mua Sắm Thông Minh</p>
                """.formatted(username, bankName, accountNumber);
    }
}
