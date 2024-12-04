package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class QRCodeViewController {

    @FXML
    private ImageView qrCodeImageView;

    /**
     * Thiết lập hình ảnh QR code vào ImageView.
     * @param image Đối tượng Image chứa QR code.
     */
    public void setQrCodeImage(Image image) {
        qrCodeImageView.setImage(image);
    }
}
