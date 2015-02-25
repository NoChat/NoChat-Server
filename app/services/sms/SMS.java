package services.sms;

import play.Logger;

/**
 * Created by Rangken on 15. 2. 25..
 */
public class SMS {
    public static void send(String phoneNumber, String phoneNumberToken, String phoneLocale){
        Coolsms coolsms = new Coolsms();

        // 부가정보
        Set set = new Set();
        set.setTo(phoneNumber);
        set.setFrom("01000000000");
        set.setText("Nochat! 인증번호 : ["+phoneNumberToken+"]");
        // TODO : 국가코드를 설정해 주어야 한다. 일단 KR로 고정
        set.setCountry(phoneLocale); // 국가코드 한국:KR 일본:JP 미국:US 중국:CN
        SendResult result = coolsms.send(set); // 보내기&전송결과받기

        if (result.getErrorString() == null) {
			/*
			 *  메시지 보내기 성공 및 전송결과 출력
			 */
            Logger.info("SMS 성공");
            Logger.info(result.getGroup_id()); // 그룹아이디
            Logger.info(result.getResult_code()); // 결과코드
            Logger.info(result.getResult_message());  // 결과 메시지
            Logger.info(result.getSuccessCount()); // 메시지아이디
            Logger.info(result.getErrorCount());  // 여러개 보낼시 오류난 메시지 수
        } else {
			/*
			 * 메시지 보내기 실패
			 */
            Logger.info("SMS 실패");
            Logger.info(result.getErrorString()); // 에러 메시지
        }
    }
}
