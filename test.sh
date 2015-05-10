#!/usr/bin/env bash
URL="http://todaytrend.cafe24.com:9000"
echo "ID 입력"
read ID
echo "PASSWORD 입력"
read PASSWOD
curl -s -X POST $URL/users/signup -d "loginId=$ID&password=$PASSWOD&deviceToken=09d26515ee7ea107cc7427fc2ceacc231423c708d87af97c98473f5abf9917db&locale=ko_KR&os=iOS"
echo "--회원가입 완료--"
echo "토큰 입력"
read TOKEN

curl -X POST $URL/users/phone/token -d "phoneNumber=01087389278" -H "apiToken:$TOKEN"

echo "--인증번호 보냄--"
echo "전번인증번호 입력"
read phoneAuthToken

curl -X POST $URL/users/phone/auth -d "phoneNumber=01087389278&phoneNumberToken=$phoneAuthToken" -H "apiToken:$TOKEN"
echo "--전번인증 함--"

curl -X POST $URL/users/signin -d "loginId=$ID&password=$PASSWOD"
echo "--로그인 함--"

echo "채팅보낼 유저아이디 입력"
read USER_ID
curl -X POST $URL/chats/new -H "apiToken:$TOKEN" -d "userId=$USER_ID&chatTypeId=1"
echo "--$USER_ID 번유저한테 챗보냄--"

echo "응답할 챗아이디 입력"
read CHAT_ID
curl -X POST $URL/chats/$CHAT_ID/ok -H "apiToken:$TOKEN"
echo "--응답 함--"


echo "전번 가져올 친구전번(ex 01087389278,01011112222)"
read FRIEND_PHONES
curl -X POST $URL/users/friend -d "phoneNumbers=$FRIEND_PHONES" -H "apiToken:$TOKEN"
echo "--친구리스트 가져옴--"

curl $URL/chats/type/all