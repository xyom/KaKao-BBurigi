package com.kakao.recruit.spray.dao;

import com.kakao.recruit.spray.dto.SprayMoneyDTO;
import com.kakao.recruit.spray.dto.UserMappedSprayDTO;
import com.kakao.recruit.spray.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SprayDAO {
    public List<UserDTO> getRoomParticipant(String roomId);
    public UserDTO getRoomParticipantById(String roomId, String userId);
    public SprayMoneyDTO getSprayMoney(String token);
    public void updateRemainSprayMoney(SprayMoneyDTO sprayMoneyDTO);
    public void updateRegDateSprayMoney(String regDate, String token);
    public void insertSprayMoney(SprayMoneyDTO sprayMoneyDTO);
    public void insertUserMappedSpray(UserMappedSprayDTO userMappedSprayDTO);
    public UserMappedSprayDTO getUserMappedSprayByTokenAndUserId(String token, String userId);
    public List<UserMappedSprayDTO> getUserMappedSprayByTokenAndReceived(String token, boolean received);
    public List<UserMappedSprayDTO> getUserMappedSprayByTokenAndReceivedForUpdate(String token, boolean received);
    public void updateUserMappedSpray(UserMappedSprayDTO userMappedSprayDTO);
}