# 🥋 JIU JITSU Project


</br>

## :eyes: 환경 세팅

- Android Studio Narwhal Feature Drop | 2025.1.2 Patch 2 </br>
![alt](./img/img_as.png "Android Studio Narwhal Feature Drop")</br></br>

- AGP : 8.12.2</br>

</br>

## 🌿 브랜치 전략 (GitHub Flow)

```bash
main (프로덕션)
├── develop (개발 통합)
├── feature/user-auth (기능 개발)
├── feature/video-upload
├── hotfix/critical-bug (긴급 수정)
└── release/v1.0.0 (릴리스 준비)
```

## 💬 커밋 컨벤션 (Conventional Commits)

```bash
형식: <type>(scope): <description>

feat(auth): 소셜 로그인 구현
fix(video): 업로드 실패 이슈 해결
docs(readme): 개발 환경 설정 가이드 추가
style(ui): 메인 화면 레이아웃 개선
refactor(api): 사용자 API 구조 개선
test(unit): 로그인 기능 테스트 케이스 추가
chore(deps): iOS 라이브러리 업데이트
```

## :open_file_folder: 프로젝트 구조 

- Clean Architecture</br></br>
**기능 단위(Feature-based)**로 디렉토리를 나누고,</br>
**비즈니스 로직과 프레젠테이션(UI)**를 계층적으로 분리해서</br>
협업과 확장에 최적화된 아키텍처입니다.</br></br>
```bash
src/
├── app/                    # 앱 전역 설정 (navigation, theme 등)
├── features/               # 기능별 폴더 구조 
│   ├── feature_1/
│   │     ├── screens/      # 화면 단위 
│   │     ├── components/   # 해당 feature에서만 사용하는 UI 컴포넌트
│   │     ├── services/     # API 통신, 비즈니스 로직
│   │     └── store/        # feature-local 상태관리 (slice 또는 zustand)
│   ├── feature_2/
│   │   ...
│   └── feature_3/
├── shared/                 # 모든 feature에서 공통 
│   ├── components/         # 공통 UI
│   ├── hooks/              # 공통 Hook
│   └── utils/              # 공통 
├── navigation/             # react-navigation 설정
├── store/                  # 전역 상태관리 store
└── index.tsx               # 앱 진입
```

</br>
