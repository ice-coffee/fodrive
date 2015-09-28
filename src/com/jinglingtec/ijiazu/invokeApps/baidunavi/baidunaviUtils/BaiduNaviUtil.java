package com.jinglingtec.ijiazu.invokeApps.baidunavi.baidunaviUtils;

import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;

public interface BaiduNaviUtil
{
    //路线规划模式
    int TRAFFIC = RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_AVOID_TAFFICJAM;     //避免拥堵
    int TOLL = RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TOLL;               //少收费
    int TIME = RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME;               //最短时间
    int DIST = RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_DIST;               //最短距离
    int COMMED = RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_RECOMMEND;            //默认

}
