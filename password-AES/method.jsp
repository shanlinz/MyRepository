 encrypt: function(word) {
        var key = CryptoJS.enc.Utf8.parse('r2z0KDyPlqBL3LmW');
        var iv = CryptoJS.enc.Utf8.parse("r2z0KDyPlqBL3LmW");
        var srcs = CryptoJS.enc.Utf8.parse(word);
        var encrypted = CryptoJS.AES.encrypt(srcs, key, {
            iv: iv,
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.ciphertext.toString()
    }