package com.bjpowernode.util.kq;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Pkipair {

    //生成签名： 商家向快钱发起请求时使用
    public String signMsg( String signMsg) throws Exception {
        String base64 = "";
        try {

            KeyStore ks = KeyStore.getInstance("PKCS12");

            //加载商户的私钥文件
            String file = Pkipair.class.getResource("10012140356.pfx").getPath().replaceAll("%20", " ");
            System.out.println(file);

            FileInputStream ksfis = new FileInputStream(file);

            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);

            //证书使用的密码
            char[] keyPwd = "123456".toCharArray();
            //char[] keyPwd = "YaoJiaNiLOVE999Year".toCharArray();
            ks.load(ksbufin, keyPwd);

            //证书的别名
            PrivateKey priK = (PrivateKey) ks.getKey("test-alias", keyPwd);
            //算法类型 RSA
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(priK);
            signature.update(signMsg.getBytes("utf-8"));
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            base64 = encoder.encode(signature.sign());

        } catch(FileNotFoundException e){
            e.printStackTrace();
            throw e;
        }catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return base64;
    }

    //验证签名使用的
    public boolean enCodeByCer( String val, String msg) {
        boolean flag = false;
        try {

            //加载快钱公钥证书
            String file = Pkipair.class.getResource("99bill[1].cert.rsa.20140803.cer").toURI().getPath();//99bill[1].cert.rsa.20140803.cer
            System.out.println(file);
            FileInputStream inStream = new FileInputStream(file);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            PublicKey pk = cert.getPublicKey();

            //算法使用的RSA
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(pk);
            signature.update(val.getBytes());

            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            System.out.println(new String(decoder.decodeBuffer(msg)));
            //判断签名的
            flag = signature.verify(decoder.decodeBuffer(msg));
            System.out.println(flag);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("no");
        }
        return flag;
    }

}