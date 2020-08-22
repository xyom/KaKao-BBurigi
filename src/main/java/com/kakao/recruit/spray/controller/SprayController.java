package com.kakao.recruit.spray.controller;

import com.kakao.recruit.common.code.SprayCode;
import com.kakao.recruit.common.controller.BaseController;
import com.kakao.recruit.common.dto.ResponseDTO;
import com.kakao.recruit.spray.dao.SprayDAO;
import com.kakao.recruit.spray.dto.ReceivedMoneyDTO;
import com.kakao.recruit.spray.dto.SprayMoneyDTO;
import com.kakao.recruit.spray.dto.SprayMoneyStatusDTO;
import com.kakao.recruit.spray.dto.UserMappedSprayDTO;
import com.kakao.recruit.spray.service.SprayService;
import com.kakao.recruit.spray.vo.SprayVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pay")
public class SprayController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SprayService sprayService;
    private final SprayDAO sprayDAO;

    public SprayController(SprayService sprayService, SprayDAO sprayDAO) {
        this.sprayService = sprayService;
        this.sprayDAO = sprayDAO;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/spray")
    public ResponseDTO getSpray(@RequestHeader(value = "X-USER-ID") String requesterId,
                                @RequestHeader(value = "X-SPRAY-TOKEN") String sprayToken) throws Exception {

        SprayMoneyDTO targetSprayMoney = sprayDAO.getSprayMoney(sprayToken);

        SprayCode status = sprayService.handleInvalidInquiryRequest(targetSprayMoney, requesterId);
        if (!status.getName().equals(SprayCode.SUCCESS.getName())) {
            return sendResponse(status.getValue(), status.getName());
        }

        List<UserMappedSprayDTO> allocatedSprayList = sprayDAO.getUserMappedSprayByTokenAndReceived(sprayToken, true);
        List<ReceivedMoneyDTO> receivedMoneyDTOList = allocatedSprayList.stream()
                .map(allocatedSpray -> new ReceivedMoneyDTO(allocatedSpray.getUserId(), allocatedSpray.getAllocatedMoney()))
                .collect(Collectors.toList());
        SprayMoneyStatusDTO sprayMoneyStatusDTO = new SprayMoneyStatusDTO(targetSprayMoney.getRegDate(), targetSprayMoney.getTotalMoney(), targetSprayMoney.getRemainMoney(), receivedMoneyDTOList);

        return sendResponse(SprayCode.SUCCESS.getValue(), sprayMoneyStatusDTO);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/spray")
    public ResponseDTO makeSpray(@RequestHeader(value = "X-USER-ID") String requesterId,
                                 @RequestHeader(value = "X-ROOM-ID") String roomId,
                                 @RequestBody SprayVO sprayVO) throws Exception {

        String resultToken = sprayService.makeSpray(requesterId, roomId, sprayVO);
        Map<String, Object> result = new HashMap<>();
        result.put("token", resultToken);

        return sendResponse(SprayCode.SUCCESS.getValue(), result);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/spray/user")
    public ResponseDTO receiveSpray(@RequestHeader(value = "X-USER-ID") String requesterId,
                                    @RequestHeader(value = "X-SPRAY-TOKEN") String sprayToken) throws Exception {

        SprayMoneyDTO targetSprayMoney = sprayDAO.getSprayMoney(sprayToken);
        SprayCode status = sprayService.handleInvalidAllocateRequest(targetSprayMoney, sprayToken, requesterId);

        if (!status.getName().equals(SprayCode.SUCCESS.getName())) {
            return sendResponse(status.getValue(), status.getName());
        }

        int allocatedMoney = sprayService.allocateSpray(targetSprayMoney, sprayToken, requesterId);
        Map<String, Object> result = new HashMap<>();
        result.put("received", allocatedMoney);

        return sendResponse(SprayCode.SUCCESS.getValue(), result);
    }

}
