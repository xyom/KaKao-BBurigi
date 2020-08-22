package com.kakao.recruit.spray.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SprayMoneyDTO {
    String token;
    String userId;
    int totalMoney;
    int remainMoney;
    String roomId;
    String regDate;

    public SprayMoneyDTO() {
    }

    public SprayMoneyDTO(String token, String userId, int totalMoney, int remainMoney, String roomId) {
        this.token = token;
        this.userId = userId;
        this.totalMoney = totalMoney;
        this.remainMoney = remainMoney;
        this.roomId = roomId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public int getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(int remainMoney) {
        this.remainMoney = remainMoney;
    }
}
