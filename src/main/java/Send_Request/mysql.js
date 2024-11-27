const mysql = require('mysql2');

const connection = mysql.createConnection({ 
    host: 'localhost', 
    database: 'libraryoop',
    user: 'linhdaynee',
    password: 'ntnlinh41'
});

connection.connect(function (err) {
    if (err) {
        console.log('Error connecting: ' + err.stack);
        return;
    } else {
        console.log('Connected as id ' + connection.threadId);
    }
});

// Hàm kiểm tra email có tồn tại không
const checkMailExists = (email, callback) => {
    const query = 'SELECT * FROM admin WHERE mail = ?';
    connection.query(query, [email], (error, result) => {
        if (error) {
            console.error('Error executing query', error);
            callback(error, null);
            return;
        }
        // Nếu kết quả trả về có bản ghi, nghĩa là email tồn tại
        if (result.length > 0) {
            callback(null, true); // Email tồn tại
        } else {
            callback(null, false); // Email không tồn tại
        }
    });
};

const changePassword = (email, newPassword) => {
    return new Promise((resolve, reject) => {
        const updateQuery = 'UPDATE admin SET password = ? WHERE mail = ?'; // Định nghĩa updateQuery ở đây

        connection.query(updateQuery, [newPassword, email], (err, result) => {
            if (err) {
                console.error('Error executing query', err);
                reject(err);
                return;
            }

            // Kiểm tra nếu không có hàng nào bị ảnh hưởng => email không tồn tại
            if (result.affectedRows === 0) {
                resolve('Email không tồn tại');
                return;
            }

            resolve('Password updated successfully');
        });
    });
};

module.exports = { checkMailExists, changePassword };
