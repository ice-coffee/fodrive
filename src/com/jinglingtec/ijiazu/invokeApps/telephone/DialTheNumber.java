package com.jinglingtec.ijiazu.invokeApps.telephone;//package com.jinglingtec.ijiazu.invokeApps.telephone;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import com.jinglingtec.ijiazu.R;
//import com.jinglingtec.ijiazu.util.FoContacts;
//import com.jinglingtec.ijiazu.util.TTSPlayerUtil;
//
///**
// * Created by coffee on 15-1-6.
// */
//public class DialTheNumber
//{
//
//    public static void TelephoneCall(Context ctx, String phone_number)
//    {
//
//        if (null == phone_number)
//        {
//            return;
//        }
//
//        FoContacts contacts = new FoContacts(ctx);
//        String name = contacts.getPhoneContacts(phone_number);
//
//        if (name != null)
//        {
//            TTSPlayerUtil.play(ctx,ctx.getResources().getString(R.string.call_to) + name);
//        }
//        else
//        {
//            TTSPlayerUtil.play(ctx,ctx.getResources().getString(R.string.call_to) + phone_number);
//        }
//
//        //替换号码间隔符
//        String number = phone_number.replace(" ", "").replace("-", "");
//
//        if (null == number)
//        {
//            return;
//        }
//
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ctx.startActivity(intent);
//    }
//
//}
