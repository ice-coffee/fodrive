package com.jinglingtec.ijiazu.util.SMS;

import java.util.Random;

/**
 * 生成验证码
 */
public class GenerateValidation
{

    public static String getValidation(int verifyCodeLen)
    {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < verifyCodeLen; i++)
        {
            Random r = new Random();
            buffer.append(r.nextInt(10));//[0,10)
        }

        return buffer.toString();
    }
}
