const express = require('express');
const bodyParser = require('body-parser');
const resetYourMail = require('./Send_Request.js'); // Giả sử là hàm gửi email
const { checkMailExists, changePassword } = require('./mysql.js'); // Import các hàm từ mysql.js
const fs = require('fs'); // Để đọc file HTML

const app = express();
const PORT = 3000;

// Middleware
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json()); // Thêm cái này để xử lý JSON

// Lưu mã xác nhận tạm thời
const verificationCodes = {}; // Dùng để lưu {email: code}

// Route cho trang gửi email
app.get('/', (req, res) => {
    res.sendFile(__dirname + '/sendMail.html');
});

let email;
app.post('/reset', async (req, res) => {
    email = req.body.email;

    // Kiểm tra email có tồn tại
    checkMailExists(email, (error, exists) => {
        if (error || !exists) {
            res.status(500).send("Mail existed or errored.");
            return;
        }

        // Tạo mã xác nhận
        const code = Math.floor(Math.random() * 90000) + 100000;
        verificationCodes[email] = code;

        // Gửi email mã xác nhận
        resetYourMail(email, code)
            .then(() => {
                // Đọc file HTML và chèn email vào
                fs.readFile(__dirname + '/verifycode.html', 'utf8', (err, html) => {
                    if (err) {
                        return res.status(500).send('Error loading confirmation page.');
                    }

                    // Thay thế {{email}} bằng giá trị email thực
                    html = html.replace('{{email}}', email);
                    res.send(html); // Trả về HTML với email đã được thay thế
                });
            })
            .catch((error) => {
                res.status(500).send('Have error when sending email.');
            });
    });
});

// Route để kiểm tra mã xác nhận
app.post('/verify', (req, res) => {
    const { email, code } = req.body;
    const savedCode = verificationCodes[email];

    if (parseInt(code) === savedCode) {
        delete verificationCodes[email]; // Xóa mã sau khi sử dụng
        res.redirect(`/resetpass?email=${email}`); // Chuyển hướng đến trang reset password
    } else {
        res.send('Confirm code is incorrect.');
    }
});

// Route cho trang reset password
app.get('/resetpass', (req, res) => {
    res.sendFile(__dirname + '/resetpassword.html'); // Gửi file HTML đặt lại mật khẩu
});

app.post('/updatePassword', async (req, res) => {
    const { password, confirm } = req.body;
    console.log('Request body:', req.body);
    
    // Kiểm tra nếu không có email trong yêu cầu
    if (!email) {
        return res.status(400).send('Email is required.');
    }

    if (password !== confirm) {
        return res.status(400).send('Passwords do not match');
    }

    try {
        const result = await changePassword(email, password);
        if (result === 'Email không tồn tại') {
            return res.status(404).send(result);``
        }
        res.status(200).send(result);
    } catch (error) {
        console.error('Detailed error:', error);  // Log chi tiết lỗi
        console.error('Stack trace:', error.stack); // Log stack trace
        res.status(500).send('Error updating password.');
    }
});

app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
