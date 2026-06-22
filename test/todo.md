POST https://upload.imagekit.io/api/v1/files/upload
Content-Type: multipart/form-data

file        : 실제 이미지 파일 binary
fileName    : ImageKit에 저장될 파일명
publicKey   : ImageKit public key
token       : 백엔드에서 받은 일회성 token
signature   : 백엔드에서 받은 signature
expire      : 백엔드에서 받은 만료 시간
folder      : 저장 폴더, 선택값 (community)

  → 내 서버에서 ImageKit 인증값 요청
  → ImageKit에 multipart 직접 업로드
  → 반환된 image url을 내 서버 DB에 저장


-----
-----

  /image/auth (CDN 서버 서명 발급)

  Response
  {
  "token": "string",
  "expire": 0,
  "signature": "string"
}

-----
-----

/image (CDN 업로드 후 이미지 등록)

Request
{
  "cdnId": "abc123xyz",
  "imageUrl": "https://ik.imagekit.io/xxx/photo.jpg"
}

Response 
{
  "id": 0,
  "cdnId": "string",
  "imageUrl": "string",
  "status": "TEMP"
}

-----
-----

/user/profile/image (사용자 프로필 이미지 업데이트) PUT

Request
imageFieldId (integer)

Response 
{
  "userId": 1,
  "email": "user@example.com",
  "nickname": "홍길동",
  "profileImage": {
    "id": 0,
    "imageUrl": "string"
  },
  "snsProvider": "KAKAO",
  "ownerRequested": true,
  "ownerRequestImage": {
    "id": 0,
    "imageUrl": "string"
  },
  "role": "USER",
  "status": "ACTIVE"
}