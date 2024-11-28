const sendMail = require('./Send_Request.js');

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
console.log(resetYourMail("ntnlinh41@vnu.edu.vn",412005));