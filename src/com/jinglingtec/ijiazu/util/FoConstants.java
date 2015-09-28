package com.jinglingtec.ijiazu.util;


public interface FoConstants
{
    // this is ONLY for test purpose,
    // MUST set it as null for product release
    String myDeviceMac = null;
    //    String myDeviceMac = "58:1F:67:00:00:D6";
    //    String myDeviceMac = "58:1F:67:00:00:DF";
    //    String myDeviceMac = "00:02:0B:00:10:88";

    //    String SELECT_FAVORITE_ADDRESS = "select_favorite_address";
    //
    //    // shared preference entry
    //    String FODRIVE_SHARED_PREFERENCE = "fodrive_preference";

    // latitude & longitude
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";

    // terminal code of Android Phone
    int PLTCODE_ANDROID_PHONE = 25;

    //导航相关
    String CITY = "city";                //城市
    String DEFAULT_CITY = "北京";         //默认城市
    String MARK = "mark";                //判断定位类型（家、公司）
    String START_ADDRESS = "start";      //起点位置信息
    String END_ADDRESS = "end";          //终点位置信息
    String HOME = "home";                //保存家庭地址标签
    String COMPANY = "company";          //保存公司地址标签
    String MODE = "route_mode";          //保存路线规划方式
    String CACHE = "cache";              //缓存
    String KEYWORD = "keyword";          //搜索关键字
    String ADDRESS_INFO = "address_info";//地址信息

    String KEY_OK = "key校验成功！";
    String KEY_FAILURE = "key校验失败！";

    String NAVI_MAIN_NAME = "invokeApps.baidunavi.BaiduNavimainActivity";

    String NAVI = "navi";
    String NAVI_INFO = "navi_info";
    String NAVI_TO = "navito";
    String IS_NAVING = "naving";          //正在导航
    String NAVI_HOME = "家";
    String NAVI_COMPANY = "公司";
    String NAVI_NAME = "navi_name";

    //fm/music


    String QTFM = "fm.qingting.qtradio";                    //蜻蜓电台
    String QTNAME = "蜻蜓fm";
    String KLFM = "com.itings.myradio";                     //考拉电台
    String KLNAME = "考拉fm";
    String KDFM = "com.appshare.android.ilisten";           //口袋听听
    String KDNAME = "口袋故事听听";

    String KWMUSIC = "cn.kuwo.player";                       //酷我音乐
    String KWNAME = "酷我音乐";
    String BDMUSIC = "com.ting.mp3.android";                 //百度音乐
    String BDNAME = "百度音乐";
    String DMMUSIC = "com.duomi.android";                    //多米音乐
    String DMNAME = "多米音乐";

    //    String HAVE_DOWN = "人家还需要做什么呢";
    //
    //    String NOT_BAIDU = "小度正在赖床中";
    //    String NOT_DUOMI = "多米正在做出门准备";
    //
    //    String NOT_KAOLA = "考拉正在树上睡觉呢";
    //    String NOT_KOUDAI = "口袋故事被塞进口袋拿不出来了";

    String FC = "fc";
    String FM = "fm.qingting.qtradio";                     //电台
    String MUSIC = "cn.kuwo.player";                 //音乐

    String FM_SELECT = "fm_select";                   //选中电台记录
    String MUSIC_SELECT = "music_select";             //选中音乐记录
    String FM_LIST = "fm_list";                       //常用电台
    String MUSIC_LIST = "music_list";                 //常用音乐

    String SEARCH_HISTORY = "search_history";         //搜索历史记录
    String SEARCH_RESULT = "search_result";         //搜索结果

    String ECAR = "ecar";
    String NAVI_ECAR = "navi_ecar";

    String USER_REMIND = "user_remind";                 //提醒用户
    //    String KEY_PRESS_BAR = "key_press_bar";                 //是否注册铃声

    String FM_DOWN_LODE = "http://qingting.fm/app/download_fujia";
    String MUSIC_DOWN_LODE = "http://down.shouji.kuwo.cn/star/mobile/kwplayer_ar_carplay.apk";

    //telephone
    String TELEPHONE = "telephone";
    String TELEPHONE_HISTORY = "telephone_history";
    String TELEPHONE_SETTING = "telephone_setting";
    String TELEPHONE_NAME = "telephonename";
    String TELEPHONE_NUMBER = "telephonenumber";
    String CONTACT_ID = "contactid";

    String TELEPHONE_SELECT = "telephone_select";                   //选中联系人记录

    //错误
    String NullPointerException = "为空";             //为空

    //用户中心
    String HAND_PHONE_TYPE = "image/*";
    String IMAGE_FILE_NAME = "faceImage.jpg";
    String PATH = "/sdcard/ijiazu/";

    //联系人头像
    String CONTACT_IMAGE_FILE_NAME = "contactImage.jpg";
    String CONTACT_PATH = "/sdcard/ijiazu/";


    // is this app run the first time ?
    String IS_FIRST_RUN = "is_first_run";
    // disable splash screen ?
    String IS_DISABLE_SPLASHSCREEN = "is_disable_splashscreen";

    // account manager related constants
    // mobile number length
    int MOBILELEN = 11;
    // Fixed telephone number length
    int FIXEDTELEPHONE = 8;
    // minimum password length
    int PASSMINLEN = 4;
    // maximum password length
    int PASSMAXLEN = 16;

    String ACCOUNT_INFO = "fodrive.accountInfo";
}

