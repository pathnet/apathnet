package com.pathnet.interfaces;

/**
 * @version V2.2.0
 * @Project: BCS-Android
 * @Filename: IBcmpRequest.java
 * @Desciption: 请求接口
 * @Author: sunbo
 * @Date: 2017/1/13 13:40
 * @Copyright: 2017 AgileSC, Inc. China All rights reserved.
 * <p>
 * Modification History
 * Date				Author	Version 	Desciption
 */
public interface IBcsRequest {
    String user = "user";
    String setPortrait = "setPortrait";
    String register = "register";
    String terminal = "terminal";
    String getInfoByTel = "getInfoByTel";
    String captcha = "captcha";
    String sendByTel = "sendByTel";
    String login = "login";
    String order = "order";
    String detail = "detail";
    String v2 = "v2";
    String home = "home";
    String point = "point";
    String rewardTotalList = "rewardTotalList";
    String orgTotalList = "orgTotalList";
    String terms = "terms";
    String auth = "auth";
    String getAuthInfo = "getAuthInfo";
}
