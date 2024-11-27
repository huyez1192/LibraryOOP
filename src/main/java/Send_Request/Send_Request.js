require("dotenv").config();
const nodemailer = require('nodemailer');

const sendMail = async ({
                            email,
                            subject,
                            html
                        }) => {
    const transporter = nodemailer.createTransport({
        host: 'smtp.gmail.com',
        service: 'Gmail',
        auth: {
            user: process.env.USER,
            pass: process.env.Password_Application
        }
    });

    const message = {
        from: 'ADMIN FROM MEOLIB',
        to: email,
        subject: subject,
        html: html
    };

    const result = await transporter.sendMail(message);
    return result;
};

module.exports = sendMail;

const resetYourMail = async (Myemail,code) => {
    try {
        const result = await sendMail({
            email: Myemail,  // use the Myemail variable directly
            subject: '[MEOLIB] Please reset your password\n',
            html: '<h1>MEOLIB Reset</h1>' +
                '<p>We have received a request to reset your password.</p>' +
                `<p>Enter this code: <strong>${code}</strong></p>` +
                '<p>Do not share this code with anyone.</p>'
        });
        console.log('Email sent successfully:', result);
    } catch (error) {
        console.error('Error sending email:', error);
    }
};

module.exports = resetYourMail;
