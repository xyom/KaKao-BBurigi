package com.kakao.recruit.spray.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.recruit.spray.dto.SprayMoneyDTO;
import com.kakao.recruit.spray.dto.UserDTO;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class SprayDAOTests {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SprayDAO sprayDAO;

    @Autowired
    MockMvc mockMvc;

    @Test
    void testGetParticipant(){
        List<UserDTO> participants = sprayDAO.getRoomParticipant("TEST_ROOM");
        boolean exist = false;
        for(UserDTO participant : participants){
            if("1597932451".equals(participant.getUserId()))
                exist = true;
        }

        Assert.assertEquals(true, exist);
    }

    @Test
    void testUpdateRegDate() throws Exception{
        JsonNode makeSprayResponse = requestMakeSpray("1597932451", 5000, 3);
        String token = makeSprayResponse.path("result").path("token").asText();
        sprayDAO.updateRegDateSprayMoney("2020-08-22 10:32:00", token);

        Assert.assertEquals("2020-08-22 10:32:00", sprayDAO.getSprayMoney(token).getRegDate());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String elevenMinuteAgo = dateFormat.format(new Date(System.currentTimeMillis() - 11*60*1000));

        //업데이트 됬는지 체크
        sprayDAO.updateRegDateSprayMoney(elevenMinuteAgo, token);
        Assert.assertEquals(elevenMinuteAgo, sprayDAO.getSprayMoney(token).getRegDate());
    }

    public JsonNode requestMakeSpray(String requesterId, int money, int sprayCount) throws Exception{
        Map<String, Object> body = new HashMap<>();
        body.put("money", money);
        body.put("sprayCount", sprayCount);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/pay/spray")
                .header("X-USER-ID", requesterId)
                .header("X-ROOM-ID", "TEST_ROOM")
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
