package com.king;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class IoUtil {
    private static final Logger logger= Logger.getLogger(IoUtil.class.getName());

    /**
     * 读取方法，  从流中获取数据，存到内存缓存区，再一次性的以 byte[]返回
     * @param iis
     * @return
     */
    public static byte[] readFromInputStream(   InputStream iis){
        try(ByteArrayOutputStream baos=new ByteArrayOutputStream(); InputStream iis0=iis;) {
            int length = -1;
            byte[] bs = new byte[1024 * 10];
            while (   (length = iis0.read(bs, 0, bs.length)) != -1   ) {
                baos.write(bs, 0, length);
            }
            return baos.toByteArray();
        }catch( Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
