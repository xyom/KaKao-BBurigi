# 뿌리기 기능 구현
##API SPEC
뿌리기 조회
---

Request:
```
GET /pay/spray

Headers:
X-USER-ID : 1597932451
X-SPRAY-TOKEN : 04f 
```

Response:

```
{
    "status": 1000,
    "result": {
        "totalMoney": 3000,
        "remainMoney": 60,
        "regDate": "2020-08-22 19:10:33.653514",
        "allocatedUserList": [
            {
                "userId": "1596932453",
                "allocatedMoney": 2940
            }
        ]
    }
}
```
구현방법: 
```
1. 뿌리기 token 값을 요청으로 받는다.
2. SprayMoney 테이블을 조회한다(총 뿌린금액, 남은금액(=받기에서 차감), 뿌린시간을 가져온다.
3. 뿌리기 token값으로 현재 받은 상태가 true인 할당건을 가져온다.
4. 2, 3의 결과를 바탕으로 조회 dto를 만들어서 리턴한다. 
```


뿌리기 생성
---
Request:
```
POST /pay/spray

Headers:
X-USER-ID : 1597932451
X-ROOM-ID : TEST_ROOM 
```
Response:
```
{
    "status": 1000,
    "result": {
        "token": "04f"
    }
}
```
구현방법: 
```
1. userId, roomId, random난수를 합친 스트링으로 sh256키를 만들어 앞에서 3자리로 token을 생성 (생성후 중복된 토큰값이 있는 지 확인하고 계속 생성)
2. roomId를 쿼리로 하여 방에 참여한 참여자 목록을 가져온후 할당할 랜덤 분배건의 max를 뿌리기 카운트로 설정한다.
3. 뿌리기 카운트 수 만큼 totalMoney 범위 안의 값을 랜덤 할당값을 만든다.(만들어진 값은 totalMoney에서 차감되며 마지막은 남은 것을 모두 갖는다.)
4. SprayMoney 테이블에 뿌린 금액, 남은 금액(=뿌린금액),생성자 id, 토큰, roomId, regDate를 저장한다.
5. UserMappedSprayMoney 테이블에 할당된 랜덤 금액들과 할당된 유저아이디를 null값으로 저장한다. 
```
뿌리기 받기
---
Request:
```
POST /pay/spray/user
Headers:
X-USER-ID : 1596932453
X-SPRAY-TOKEN : 04f 
```

Response:
```
{
    "status": 1000,
    "result": {
        "received": 493
    }
}
```
구현방법: 
```
1. SELECT FOR UPDATE문으로 할당되지 않은 할당건들을 가져온다. 
   (랜덤으로 가져와야하기 때문에 많은 요청자가 동시에 할당되지 않은 결과를 가져와 처리하면 
    중복 업데이트가 발생하므로 배타적 락을 건다.)
2. 할당되지 않은 건을 랜덤으로 선택하고 할당된 건에 자신의 유저아이디를 넣는다..
3. 선택한 건의 할당된 money를 SprayMoney의 remainMoney에 차감하고 업데이트한다.
```

예외처리
---

에러코드:
```
SUCCESS: 요청 성공
SPRAY_NOT_EXISTS : 해당 토큰에 해당하는 뿌리기 건이 없음
NOT_PARTICIPANT : 뿌리건에 해당하는 참여 사용자가 아님
ALREADY_ALLOCATED : 이미 할당건을 분배 받았던 사용자
REQUEST_BY_SELF : 뿌리기 생성한 자신이 받기를 호출
EXPIRED_REQUEST : 받기 10분, 뿌리기 7일이 지나서 만료되었을 때 응답
INVALID_REQUEST : 뿌리기 생성자가 아닌 사람이 조회를 요청했을때
```
  
핸들되지 못한 에러 처리:

BaseContrller를 상속하며
Exception 핸들러로 처리.

테스트
---

####뿌리기
 - 토큰 3자리인지 테스트
 - 토큰 값이 고유한지 테스트 (토큰 생성을 여러번 호출 후 Set에 넣은뒤 요청횟수와 값이 같은지 비교)
 - 뿌리기 인원만큼 할당건이 생겼는지 테스트 ()
####받기
 - 쓰레드로 동시에 요청하여 중복으로 할당되는 건이 있는지 테스트 (요청후 뿌린 개수와 할당된 개수가 같은지 확인)
 - 뿌리기를 생성한뒤 매 받기마다 reamin값이 맞는지 체크
 - 받기를 이미 한 요청자가 다시 받기를 요청하는지 체크 (할당된 건에서 해당 아이디건이 있는지 조회)
 - 자신이 뿌리기한 건을 받지 못하는지 체크 (뿌리기 건에 생성자 ID가 요청자 ID랑 같은지 체크)
 - 같은 방의 참여자만 받을 수 있는지 체크 (방의 참여자들 목록을 받은 후 request user-id 토큰과 비교)
 - 뿌리기 받기 10분 Expired 체크 (뿌리기 생성시각과 받기 요청한 현재 시간을 비교)
####조회
 - 뿌린 시각, 뿌린 금액, 받기 완료 금액의 필드가 나오는지 체크
 - 할당 호출 후 받기 완료 금액의 정합성이 맞는지 체크
 - 조회 시 request user_id 와 뿌리기 생성 user_id를 비교하여 본인만 조회할 수 있도록 체크
 - 뿌리기 조회 7일 Expired 체크 (뿌리기 생성시각과 받기 요청한 현재 시간을 비교)  