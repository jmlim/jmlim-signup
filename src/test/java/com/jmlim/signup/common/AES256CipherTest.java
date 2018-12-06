package com.jmlim.signup.common;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AES256CipherTest {
    String id = "hackerljm";
    String custrnmNo = "1234";
    String custNm = "정묵테스트";

    @Test
    public void encDesTest() throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        AES256Cipher a256 = AES256Cipher.getInstance();

        String enId = a256.AES_Encode(id);
        String enYyyymmdd = a256.AES_Encode(custrnmNo);
        String enCustNm = a256.AES_Encode(custNm);

        String desId = a256.AES_Decode(enId);
        String desYyyymmdd = a256.AES_Decode(enYyyymmdd);
        String desCustNm = a256.AES_Decode(enCustNm);

        assertThat(id, is(desId));
        assertThat(custrnmNo, is(desYyyymmdd));
        assertThat(custNm, is(desCustNm));
    }
}