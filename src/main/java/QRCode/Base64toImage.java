package QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import static QRCode.AppUtil.generateQrCode;

public class Base64toImage {

    // Hàm để chuyển chuỗi Base64 thành ảnh và lưu vào tệp
    public static void base64ToImage(String base64String, String outputPath) {
        // Loại bỏ phần "data:image/png;base64," nếu có trong chuỗi Base64
        String imageDataString = base64String.split(",")[1];

        // Giải mã Base64 thành mảng byte
        byte[] imageBytes = Base64.getDecoder().decode(imageDataString);

        // Lưu mảng byte vào một tệp PNG
        try (FileOutputStream fos = new FileOutputStream(new File(outputPath))) {
            fos.write(imageBytes);
            System.out.println("Ảnh đã được lưu tại: " + outputPath);
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu ảnh: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Giả sử bạn đã có chuỗi Base64 của ảnh QR code
        String base64String = generateQrCode("https://www.facebook.com/camille4105?locale=vi_VN",300,300);

        // Đường dẫn nơi bạn muốn lưu ảnh
        String outputPath = "qr_code_image.png";

        // Gọi hàm chuyển Base64 thành ảnh
        base64ToImage(base64String, outputPath);
    }
}
