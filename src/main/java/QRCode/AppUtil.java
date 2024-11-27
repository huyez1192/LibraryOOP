package QRCode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;

import javax.imageio.ImageIO;

public class AppUtil {
    public static String generateQrCode(String data, int wid, int hei) {
        StringBuilder result = new StringBuilder();

        // Kiểm tra dữ liệu đầu vào
        if (data != null && !data.isEmpty()) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                // Tạo đối tượng QRCodeWriter để mã hóa dữ liệu thành mã QR
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, wid, hei);

                // Chuyển đổi BitMatrix thành BufferedImage
                BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

                // Ghi ảnh vào ByteArrayOutputStream dưới định dạng PNG
                ImageIO.write(image, "png", os);

                // Mã hóa dữ liệu ảnh PNG dưới dạng Base64
                result.append("data:image/png;base64,");
                result.append(Base64.getEncoder().encodeToString(os.toByteArray()));
            } catch (Exception e) {
                // Xử lý lỗi, có thể in ra hoặc ghi log
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        // Dữ liệu cần mã hóa thành QR code
        String input = "Lập trình B2A";

        // Gọi hàm generateQrCode và lấy kết quả
        String qrCodeBase64 = generateQrCode(input, 300, 300);

        // In kết quả mã QR dưới dạng Base64
        System.out.println(qrCodeBase64);
    }
}
