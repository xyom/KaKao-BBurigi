package com.kakao.recruit.spray.dto;

import java.util.List;

public class SprayMoneyStatusDTO extends SprayMoneyDTO {
    private List<ReceivedMoneyDTO> allocatedUserList;

    public SprayMoneyStatusDTO(){}
    public SprayMoneyStatusDTO(String regDate, int totalMoney, int remainMoney, List<ReceivedMoneyDTO> allocatedUserList){
        setRegDate(regDate);
        setTotalMoney(totalMoney);
        setRemainMoney(remainMoney);
        setAllocatedUserList(allocatedUserList);
    }

    public List<ReceivedMoneyDTO> getAllocatedUserList() {
        return allocatedUserList;
    }

    public void setAllocatedUserList(List<ReceivedMoneyDTO> allocatedUserList) {
        this.allocatedUserList = allocatedUserList;
    }
}
