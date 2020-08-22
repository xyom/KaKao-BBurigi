package com.kakao.recruit.common;

import com.kakao.recruit.common.util.RandomUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
public class RandomUtilTests {

    @Test
    public void testGetNotDuplicatedNumbers(){
        List<Integer> picked = RandomUtil.getNotDuplicatedNumbers(5,3);

        for(Integer num : picked){
            if(num < 0  || num >=5){
                Assert.fail("randomRange fail");
            }
        }
        Assert.assertEquals(3 ,picked.size());


        picked = RandomUtil.getNotDuplicatedNumbers(3,7);
        Assert.assertEquals(3 ,picked.size());
    }

    @Test
    public void getRandomSprayMoney(){
        int totalMoney = 5000;
        int remain = totalMoney;
        int sum=0;

        for(int i=0;i<4;i++){
            int money =0;
            if(i==3){
                money = remain;
            }else{
                money = RandomUtil.getRandomValue(remain);
            }
            System.out.println("money : " + money);
            sum+=money;
            remain-=money;
        }

        Assert.assertEquals(totalMoney, sum);
    }
}
