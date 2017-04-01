package com.pathnet.interfaces;

/**
 * @version V2.2.0
 * @Project: BCS-Android
 * @Filename: IAscConstants.java
 * @Desciption: 常量抽取
 * @Author: sunbo
 * @Date: 2016/12/29 9:37
 * @Copyright: 2016 AgileSC, Inc. China All rights reserved.
 * <p>
 * Modification History
 * Date				Author	Version 	Desciption
 */
public interface IBcsConstants {
    //【以下广播相关参数】
    //订单tab栏--action
    String ORDER_LIST_TAB_TYPE_ACTION = "com.asc.businesscontrol.IAscConstants.orderListType";
    String ORDER_LIST_TAB_TYPE_KEY = "orderTypeKey";
    int ORDER_LIST_TAB_TYPE_VALUE_0 = 0;
    int ORDER_LIST_TAB_TYPE_VALUE_1 = 1;
    int ORDER_LIST_TAB_TYPE_VALUE_2 = 2;
    int ORDER_LIST_TAB_TYPE_VALUE_3 = 3;
    int ORDER_LIST_TAB_TYPE_VALUE_4 = 4;
    //关闭订单webview展示界面--action
    String ORDER_INFO_UI_ACTION = "finishOrderInfoUi";
    String ORDER_INFO_UI_KEY = "finishOrderInfoUIKey";
    String ORDER_INFO_UI_VALUE = "finishOrderInfoUIValue";
    //订单列表界面刷新
    String ORDER_LIST_UPDATE_KEY = "orderListUpdateKey";
    String ORDER_LIST_UPDATE_VALUE = "orderListUpdateValue";
    String FLOAT_VIEW_SERVICE_ACTION = "floatViewService";
    String FLOAT_VIEW_SERVICE_KEY = "floatViewServiceKey";
    int FLOAT_VIEW_SERVICE_VALUE = 200;
    //【以上广播相关参数】
    //【----------------------------------------------------】
    String TAG = "url";
    String PATH = "path";
    String DOMAIN = "domain";
    String NAME = "name";
    String GESTURE_ENABLE = "gestureenable";
    String GESTURE_OUTTIME = "gestureouttime";// 积分页面离开时间
    String GESTURE_CHANGE = "gesturechange";// 是否修改了手势密码
    String GESTURE_FIVETIME = "gestureFiveTime";// 手势密码错误次数及时间存储
    String SECURITY_SETTING = "scurity_setting";// 是否设置了密码保护问题
    String GESTURE_FIRST = "gesturefirst";
    String REMEMBER_PW_STRING = "IBcmpApi_remember_pw";//是否记住密码
    String AUTOMATICLOGIN_STRING = "automaticlogin";//是否自动登录
    String SERACHHISTORY_STRING = "serachhistory";//活动搜索历史
    String USERTYPE_STRING = "usertype";//企业类型（1：药企；2：商业公司；3：终端）
    String SESSIONId = "sessionid";
    String PROTOCOL_TAG_STRING = "protocol";//服务协议
    String USERID_STRING = "userId";//用户id
    String ORGNAME_STRING = "orgName";//企业名称
    String USERICON_STRING = "usericon";//用户图像
    String USERNAME_STRING = "username";
    String ACOUNT_STRING = "acount";//登录账号不包含手机号
    String PASSWORD_STRING = "password";
    String ORGID_STRING = "orgId";//药企id  orgId==1为平台
    String AUTH = "auth";//auth (V1.6.0)
    String SEESIONID = "sessionid";
    //【---------------------------------以上user相关--------------------------】
    String SERVICE_PHONE = "4006661205";//客服电话 请求失败
    String CERTIFICATE_OK = "恭喜您已成功通过终端认证";//终端认证审核成功
    String CERTIFICATE_FAIL = "认证失败";//终端认证审核失败
    /**
     * 拍照回调
     */
    int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
    int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
    int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像
    int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
    int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
    int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置
    String EXTRA_STRING = "extra_string";
    String ACTION_DETAILS_INFOR_ACTIVITY_ACTION = "actionDetailsInforActivityAction";
    int ACTION_DETAILS_INFOR_ACTIVITY_VALUE = 1;
    String ACTION_DETAILS_INFOR_ACTIVITY_KEY = "actionDetailsInforActivity_key";
}
