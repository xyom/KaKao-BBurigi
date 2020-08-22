package com.kakao.recruit.spray.service;

import com.kakao.recruit.common.code.SprayCode;
import com.kakao.recruit.common.util.DateUtil;
import com.kakao.recruit.common.util.RandomUtil;
import com.kakao.recruit.spray.dao.SprayDAO;
import com.kakao.recruit.spray.dto.SprayMoneyDTO;
import com.kakao.recruit.spray.vo.SprayVO;
import com.kakao.recruit.spray.dto.UserMappedSprayDTO;
import com.kakao.recruit.spray.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprayService {
    private final Logger logger = LoggerFactory.getLogger(SprayService.class);
    private final SprayDAO sprayDAO;

    public SprayService(SprayDAO sprayDAO) {
        this.sprayDAO = sprayDAO;
    }

    @Transactional(rollbackFor = Exception.class)
    public String makeSpray(String requesterId, String roomId, SprayVO sprayVO) {
        int pickCount = sprayVO.getSprayCount();
        int totalMoney = sprayVO.getMoney();

        String sprayToken = "";
        try {
            do {
                sprayToken = RandomUtil.getSprayToken(requesterId, roomId);
            } while (sprayDAO.getSprayMoney(sprayToken) != null);
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        logger.debug("####made sprayToken:" + sprayToken);

        SprayMoneyDTO sprayMoneyDTO = new SprayMoneyDTO(sprayToken, requesterId, totalMoney, totalMoney, roomId);
        sprayDAO.insertSprayMoney(sprayMoneyDTO);

        //requester 본인 제외
        List<UserDTO> participantList = sprayDAO.getRoomParticipant(roomId).stream()
                .filter(participant -> !participant.getUserId()
                        .equals(requesterId)).collect(Collectors.toList());

        pickCount = Math.min(pickCount, participantList.size());

        for (int i = 0; i < pickCount; i++) {
            int allocatedMoney = 0;
            if (i == pickCount - 1) {
                allocatedMoney = totalMoney;
            } else {
                allocatedMoney = RandomUtil.getRandomValue(totalMoney);
                totalMoney -= allocatedMoney;
            }
            sprayDAO.insertUserMappedSpray(new UserMappedSprayDTO(sprayToken, allocatedMoney));
        }

        return sprayToken;
    }

    @Transactional(rollbackFor = Exception.class)
    public int allocateSpray(SprayMoneyDTO targetSprayMoney, String sprayToken, String userId) throws Exception {
        List<UserMappedSprayDTO> notAllocatedSpray = sprayDAO.getUserMappedSprayByTokenAndReceivedForUpdate(sprayToken, false);
        if (notAllocatedSpray.size() == 0)
            return 0;

        int allocatedIndex = RandomUtil.getRandomValue(notAllocatedSpray.size());
        UserMappedSprayDTO willAllocate = notAllocatedSpray.get(allocatedIndex);
        willAllocate.setUserId(userId);
        willAllocate.setReceived(1);

        int allocatedMoney = willAllocate.getAllocatedMoney();
        targetSprayMoney.setRemainMoney(targetSprayMoney.getRemainMoney() - allocatedMoney);

        logger.debug(String.format("##### allocate money to %s amount: %s", userId, allocatedMoney));
        sprayDAO.updateUserMappedSpray(willAllocate);
        sprayDAO.updateRemainSprayMoney(targetSprayMoney);

        return allocatedMoney;
    }

    public SprayCode handleInvalidInquiryRequest(SprayMoneyDTO targetSprayMoney, String requesterId) throws Exception {
        if (targetSprayMoney == null)
            return SprayCode.SPRAY_NOT_EXISTS;

        if (DateUtil.isExpiredInQuiryTime(targetSprayMoney.getRegDate()))
            return SprayCode.EXPIRED_REQUEST;

        if (!targetSprayMoney.getUserId().equals(requesterId))
            return SprayCode.INVALID_REQUEST;

        return SprayCode.SUCCESS;
    }

    public SprayCode handleInvalidAllocateRequest(SprayMoneyDTO targetSprayMoney, String sprayToken, String userId) throws Exception {
        if (targetSprayMoney == null)
            return SprayCode.SPRAY_NOT_EXISTS;

        if (DateUtil.isExpiredAllocateTime(targetSprayMoney.getRegDate()))
            return SprayCode.EXPIRED_REQUEST;

        if (targetSprayMoney.getUserId().equals(userId))
            return SprayCode.REQUEST_BY_SELF;

        UserDTO participant = sprayDAO.getRoomParticipantById(targetSprayMoney.getRoomId(), userId);
        if (participant == null)
            return SprayCode.NOT_PARTICIPANT;

        UserMappedSprayDTO allocatedUserMap = sprayDAO.getUserMappedSprayByTokenAndUserId(sprayToken, userId);
        if (allocatedUserMap != null)
            return SprayCode.ALREADY_ALLOCATED;

        return SprayCode.SUCCESS;
    }

}

