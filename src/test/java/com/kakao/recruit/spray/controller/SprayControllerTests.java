package com.kakao.recruit.spray.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.recruit.common.code.SprayCode;
import com.kakao.recruit.common.dto.ResponseDTO;
import com.kakao.recruit.common.util.DateUtil;
import com.kakao.recruit.spray.dao.SprayDAO;
import com.kakao.recruit.spray.dto.SprayMoneyDTO;
import com.kakao.recruit.spray.dto.UserMappedSprayDTO;
import com.kakao.recruit.spray.vo.SprayVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
public class SprayControllerTests {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SprayDAO sprayDAO;

    @Autowired
    MockMvc mockMvc;

    private final String testMakerId = "1597932451";
    private final String testRequesterId = "1599932417";
    private final String[] testUserList = new String[]{"1593932475", "1591932486", "1596932453", "1591932468", "1599232462"};
    private final String testRoomId = "TEST_ROOM";

    @Test
    void testMakeSpray() throws Exception {
        int sprayMoney = 5000;
        int sprayCount = 10000;

        JsonNode makeSprayResponse = requestMakeSpray("1597932451", sprayMoney, sprayCount);
        String token = makeSprayResponse.path("result").path("token").asText();

        //토큰 생성 및 3자리 토큰 생성되었는지 체크
        Assert.assertNotNull(token);
        Assert.assertEquals(3, token.length());



    }

    //뿌린만큼 생겼는지 확인 최대 방 인원수 만큼만 생김.
    @Test
    void madeAllocateCountTests() throws Exception{
        int sprayMoney = 5000;
        int sprayCount = 10000;

        JsonNode makeSprayResponse = requestMakeSpray("1597932451", sprayMoney, sprayCount);
        String token = makeSprayResponse.path("result").path("token").asText();

        List<UserMappedSprayDTO> notAllocatedSprays = sprayDAO.getUserMappedSprayByTokenAndReceived(token, false);
        int participantCountExceptMe = sprayDAO.getRoomParticipant(testRoomId).size() - 1;
        int expectCount = Math.min(sprayCount, participantCountExceptMe);
        Assert.assertEquals(expectCount, notAllocatedSprays.size());
    }

    //토큰 값 고유한지 체크
    @Test
    void tokenUniqueTest() throws Exception{
        int sprayMoney = 5000;
        int sprayCount = 10000;

        Set<String> tokenSet = new HashSet<>();
        int tokenTestCount = 3000;

        for(int i=0;i<tokenTestCount;i++){
            JsonNode makeSprayResponse = requestMakeSpray("1597932451", sprayMoney, sprayCount);
            String token = makeSprayResponse.path("result").path("token").asText();
            tokenSet.add(token);
        }
        //token 만든값이랑 set의 크기랑 같으면 중복이 없음.
        Assert.assertEquals(tokenTestCount, tokenSet.size());
    }

    //받기 에러 체크 테스트
    @Test
    void allocateErrorTest() throws Exception{
        int sprayMoney = 100000;
        int remainMoney = 100000;
        int sprayCount = 3;

        //spray 토큰 생성
        JsonNode makeSprayResponse = requestMakeSpray(testMakerId, sprayMoney, sprayCount);
        String token = makeSprayResponse.path("result").path("token").asText();

        //잘못된 토큰 전달 에러 체크
        JsonNode wrongTokenResponse = requestAllocateSpray(testRequesterId, "-1234");
        Assert.assertEquals(SprayCode.SPRAY_NOT_EXISTS.getName(), wrongTokenResponse.path("message").asText());

        //자기 자신이 호출 받기 에러 체크
        JsonNode requestBySelfResponse = requestAllocateSpray(testMakerId, token);
        Assert.assertEquals(SprayCode.REQUEST_BY_SELF.getName(), requestBySelfResponse.path("message").asText());


        //받기 완료후 다시받게 에러 체크
        requestAllocateSpray("1596932453", token);
        JsonNode reAllocateResponse = requestAllocateSpray("1596932453", token);
        Assert.assertEquals(SprayCode.ALREADY_ALLOCATED.getName(), reAllocateResponse.path("message").asText());

        //방에 참여하지 않은 사람이 받기 에러 체크
        JsonNode requestNotParticipantResponse = requestAllocateSpray("1234", token);
        Assert.assertEquals(SprayCode.NOT_PARTICIPANT.getName(), requestNotParticipantResponse.path("message").asText());

        //10분이상 지난 받기 에러 체크
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String elevenMinuteAgo = dateFormat.format(new Date(System.currentTimeMillis() - 11*60*1000));
        sprayDAO.updateRegDateSprayMoney(elevenMinuteAgo, token);

        JsonNode requestExpiredResponse = requestAllocateSpray(testRequesterId, token);
        Assert.assertEquals(SprayCode.EXPIRED_REQUEST.getName(), requestExpiredResponse.path("message").asText());
    }

    //할당 정합성 테스트
//        @Test
    @RepeatedTest(1000)
    void allocateSprayTest() throws Exception{
        int sprayMoney = 100000;
        int remainMoney = 100000;
        int sprayCount = 3;

        //spray 토큰 생성
        JsonNode makeSprayResponse = requestMakeSpray(testMakerId, sprayMoney, sprayCount);
        String token = makeSprayResponse.path("result").path("token").asText();

        //여러명의 유저가 받고 남은돈이 맞는지 체크
        for (String testUser : testUserList) {
            //받기 완료 status 체크
            JsonNode allocateResponse = requestAllocateSpray(testUser, token);
            Assert.assertEquals(SprayCode.SUCCESS.getValue(), allocateResponse.path("status").asInt());

            //받기 완료 후 남은 돈 체크
            SprayMoneyDTO sprayMoneyDTO = sprayDAO.getSprayMoney(token);
            int allocatedMoney = allocateResponse.path("result").path("received").asInt();
            Assert.assertEquals(remainMoney -= allocatedMoney, sprayMoneyDTO.getRemainMoney());
        }
    }

    @RepeatedTest(10000)
    void allocateSprayConcurrencyTest() throws Exception{
        int sprayMoney = 100000;
        int sprayCount = 5;

        //spray 토큰 생성
        JsonNode makeSprayResponse = requestMakeSpray(testMakerId, sprayMoney, sprayCount);
        String token = makeSprayResponse.path("result").path("token").asText();

        //동시에 받기 쓰레드 요청
        ExecutorService service = Executors.newFixedThreadPool(sprayCount);
        CountDownLatch latch = new CountDownLatch(sprayCount);
        for(int i=0;i<sprayCount;i++){
            String requestUser = testUserList[i];
            service.execute(() -> {
                try {
                    requestAllocateSpray(requestUser, token);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                latch.countDown();
            });
        }
        latch.await();

        List<UserMappedSprayDTO> allocatedList = sprayDAO.getUserMappedSprayByTokenAndReceived(token, true);

        //중복업데이트가 되면 뿌린 카운트 수와 다르니 뿌린갯수와 allocated된 리스트의 갯수가 같아야함.
        //forUpdate를 지우면 중복 업데이트 발생
        Assert.assertEquals(sprayCount, allocatedList.size());
    }

    @Test
    void inquiryErrorTest() throws Exception{
        int sprayMoney = 100000;
        int sprayCount = 3;

        //spray 토큰 생성
        JsonNode makeSprayResponse = requestMakeSpray(testMakerId, sprayMoney, sprayCount);
        String token = makeSprayResponse.path("result").path("token").asText();

        //다른사람이 조회 호출했을때 에러 체크
        JsonNode requestInvalidInquiryResponse = requestInquirySpray(testRequesterId, token);
        Assert.assertEquals(SprayCode.INVALID_REQUEST.getName(), requestInvalidInquiryResponse.path("message").asText());

        //7일이상 지났을때 조회 호출했을때 에러 체크
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sevenDayAgo = dateFormat.format(new Date(System.currentTimeMillis() - 7 * DateUtil.day));
        sprayDAO.updateRegDateSprayMoney(sevenDayAgo, token);

        JsonNode requestExpiredInquiryResponse = requestInquirySpray(testMakerId, token);
        Assert.assertEquals(SprayCode.EXPIRED_REQUEST.getName(), requestExpiredInquiryResponse.path("message").asText());
    }
    @Test
    void inquirySprayTest() throws Exception{
        int sprayMoney = 100000;
        int remainMoney = sprayMoney;
        int sprayCount = 3;

        //spray 토큰 생성
        JsonNode makeSprayResponse = requestMakeSpray(testMakerId, sprayMoney, sprayCount);
        String token = makeSprayResponse.path("result").path("token").asText();


        //조회 값이 제대로 들어있는지 확인.
        JsonNode requestInquiryResponse = requestInquirySpray(testMakerId, token);
        int totalMoney = requestInquiryResponse.path("result").path("totalMoney").asInt();
        int responseRemainMoney = requestInquiryResponse.path("result").path("remainMoney").asInt();

        Assert.assertEquals(sprayMoney, totalMoney);
        Assert.assertEquals(sprayMoney, responseRemainMoney);
        Assert.assertNotNull(requestInquiryResponse.path("result").path("regDate"));

        for(int i=0;i<4;i++){
            JsonNode allocateResponse = requestAllocateSpray(testUserList[i], token);
            int allocatedMoney = allocateResponse.path("result").path("received").asInt();
            remainMoney -= allocatedMoney;
        }

        //할당받은 돈들의 차와 조회 api의 remainMoney가 같은지 확인.
        requestInquiryResponse = requestInquirySpray(testMakerId, token);
        responseRemainMoney = requestInquiryResponse.path("result").path("remainMoney").asInt();
        Assert.assertEquals(remainMoney, responseRemainMoney);
    }


    public JsonNode requestMakeSpray(String requesterId, int money, int sprayCount) throws Exception{
        Map<String, Object> body = new HashMap<>();
        body.put("money", money);
        body.put("sprayCount", sprayCount);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/pay/spray")
                .header("X-USER-ID", requesterId)
                .header("X-ROOM-ID", testRoomId)
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    public JsonNode requestAllocateSpray(String userId, String token) throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/pay/spray/user")
                .header("X-USER-ID", userId)
                .header("X-SPRAY-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    public JsonNode requestInquirySpray(String userId, String token) throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/pay/spray")
                .header("X-USER-ID", userId)
                .header("X-SPRAY-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
