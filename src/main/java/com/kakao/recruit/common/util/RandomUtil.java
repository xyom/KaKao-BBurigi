package com.kakao.recruit.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class RandomUtil {
    public static List<Integer> getNotDuplicatedNumbers(int pickCount, int maxRange) {
        pickCount = Math.min(pickCount, maxRange);

        Random rand = new Random();
        Set<Integer> generated = new HashSet<>();
        List<Integer> pickedList = new ArrayList<>();

        while (pickedList.size() < pickCount) {
            Integer next = rand.nextInt(maxRange);
            if (!generated.contains(next)) {
                generated.add(next);
                pickedList.add(next);
            }
        }

        return pickedList;
    }

    public static Integer getRandomValue(int maxRange) {
        return new Random().nextInt(maxRange);
    }

    public static String getSprayToken(String makerId, String roomId) throws NoSuchAlgorithmException {
        String hashTarget = makerId + roomId + new Random().nextInt(Integer.MAX_VALUE);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(hashTarget.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) {
            sb.append(String.format("%02x", b));
        }

        return sb.substring(0, 3);
    }
}
