# OPEN_SUBVIEW 네이티브 상단 바와 부모 WebView 보존

- 상태: 구현 반영 — 사용자 테스트·작업 검토 대기
- 작성일: 2026-09-07
- 범위: 2026-09-07 후속 요청으로 기능 구현 승인. 사용자가 테스트·작업 검토를 직접 진행하므로 빌드·테스트·검토를 실행하지 않는다.
- 기준: [ADR 0003](../architecture/decisions/0003-isolate-shared-webview-runtime.md), [WebView 개발](../development/webview.md), [Adaptive UI](../development/adaptive-ui.md).
- 기존 ADR의 단일 Activity/보존 스택 결정을 유지하는 UI 확장 제안이다. 새로운 Activity 분리 결정은 하지 않는다.

## 구현 반영 기록 (2026-09-07)

- 기존 createEntry/entries를 유지하고 별도 Activity 없이 서브 화면별 네이티브 상단 바를 추가했다.
- WebSubviewTopBar는 상태바 inset을 중복 적용하지 않고, 본문에만 loading/error를 배치한다.
- WebSubviewActions의 nullable callback으로 알림·더보기를 각각 표시한다. 미지정 시 숨김이다.
  URL별 정책은 WebContentRoute.subviewActions에서 주입한다. 실제 목적지/메뉴는 아직 미연결이다.
- 상단/시스템 Back을 통합하고 표면·로그인 → IME → guard → 히스토리 → pop 순서로 처리한다.
  실패한 서브 화면은 WebView 히스토리를 호출하지 않고 닫는다. CLOSE_SUBVIEW는 기존 의미를 유지한다.
- app의 기존 fullscreen 상태를 재사용해 흰색 상태바 배경을 그리고 종료 시 제거한다.
- 부모는 composition에 유지하고 숨김/접근성 제외 및 기존 GONE/onPause를 사용한다.
- 사용자가 추가한 브릿지/URL 로그는 그대로 유지했다. 로그 진단 고도화는 이번에 추가하지 않았다.
- 테스트, 빌드, 구조 검사, diff 검토, 기기 검증은 사용자 요청에 따라 실행하지 않았다.
- 웹 헤더 중복 여부, 알림/더보기 실제 동작, 아래 수용 기준의 통과 여부는 미확인이다.

이하 표의 '현재 코드'는 구현 전 분석 기준이며, '제안하는 변경'은 위 기록 범위까지 반영했다.
미합의 웹 계약은 여전히 후속 협의 대상이다.

## 요구사항

`OPEN_SUBVIEW`의 `payload.url`을 새 WebView에서 열고 부모는 그대로 보존한다.
서브 화면은 흰색 상태바 아래 전체 영역을 채우는 WebView 위에 투명한 네이티브 상단 바를 겹쳐 표시한다.
상단 바 콘텐츠 높이는 55dp로 고정하고 좌측 끝의 뒤로가기 버튼을 수직 중앙에 배치한다(2026-09-10 변경).
상태바 inset은 이 높이에 포함하지 않으며 웹뷰에 상단 바 높이만큼의 여백을 추가하지 않는다.
상단 바는 로딩/오류 표면보다 위에 배치한다. 버튼은 기존 IME/guard 처리와 히스토리 우선,
히스토리가 없으면 현재 서브 화면 종료 경로를 그대로 사용한다. 알림·더보기 버튼은 현재 노출하지 않는다.
이번 변경의 빌드·테스트·기기 검증은 미실행이다. 아래 구현 전 제안의 분리된 본문 배치보다 이 요구사항이 우선한다.
기존 브릿지·인증·파일 선택·공유·오류·뒤로가기 보호 기능을 동일하게 사용할 수 있어야 한다.

## 현재 구현과 차이

| 항목 | 현재 코드 | 제안하는 변경 |
| --- | --- | --- |
| OPEN_SUBVIEW | WebHostController.message → open → createEntry로 새 WebViewPage 생성 | 기존 경로 재사용 |
| 부모 보존 | entries에 부모 유지, 화면별 document.id 키, 숨김/onPause | 부모 인스턴스·문서·히스토리 보존을 검증 |
| 서브 상단 바 | 일부 정책 경로/실패 화면에 닫기 TextButton만 표시 | 모든 OPEN_SUBVIEW 화면에 네이티브 상단 바 |
| 뒤로가기 | 보호 처리 후 서브 화면이면 히스토리보다 닫기 우선 | 보호 처리 후 현재 WebView 히스토리 우선, 없으면 pop |
| 시스템 바 | app에서 fullscreen 시 systemBars inset과 어두운 아이콘 사용 | 흰색 상태바 배경을 명시하고 기존 chrome 복원 |
| 로딩/실패 | 전체 영역 overlay 가능 | 상단 바 아래 본문에만 표시, 뒤로가기 유지 |
| 알림/더보기 | OPEN_SUBVIEW에 관련 계약 없음 | 네이티브 표시 상태/이벤트 분리, 동작·숨김 정책 검토 필요 |

현재 요청 수신만으로 화면 생성이 보장되지는 않는다. 최상단 page의 요청인지,
surface/loginVisible 상태, trusted URL, 총 entries 수 제한(현재 루트 포함 8)을 확인해야 한다.
`/community/27`은 허용 경로지만 예시 URL의 origin이 실제 policy.origin과 일치하는지도 확인한다.
이번 분석은 실제 서버·기기 재현 결과가 아니다.

## 권장 구조와 대안

**MainActivity 하나 + 기존 feature:web의 보존 스택 + entry별 독립 WebViewPage**를 권장한다.
WebView는 View이므로 새 인스턴스를 만들기 위해 새 Activity가 필요하지 않다.

| 방식 | 평가 |
| --- | --- |
| 기존 Compose 스택 확장 | 추천. 부모 인스턴스와 로그인 slot, reply target, chooser를 유지하며 상단 바만 조합 가능 |
| 별도 Activity | 독립 task/외부 진입 등 별도 요구가 있을 때 검토. 현재는 세션 변화·결과 전달·시스템 바·수명 조합이 추가되고 부모 생존도 보장하지 않음 |
| 새 NavHost destination으로 매번 이동 | 가능하지만 부모 composition 해제 및 WebView 재생성 방지 설계가 필요. 이미 존재하는 스택을 대체할 근거가 없음 |

```text
MainActivity / AppRoot                    ← 시스템 바·하단 탭
└─ WebContentRoute / WebHostController    ← 보존 스택·네이티브 표면
   ├─ Entry A: 기존 WebViewPage           ← 숨김 상태로 유지
   └─ Entry B: 새 서브 화면              ← 현재 표시
      ├─ 네이티브 상단 바: 뒤로 / 알림 / 더보기
      └─ WebPageView(new WebViewPage)    ← payload.url 최초 로드
```

### 모듈 책임

- `:feature:web`: 서브 화면 컨테이너, 상태를 전달받는 상단 바, entry별 표시 옵션과 이벤트,
  스택·히스토리·BACK_GUARD 조정. 예시 이름 WebSubviewScreen/WebSubviewTopBar는 확정 API가 아니다.
- `:core:webview`: 기존 WebViewPage/WebPageView, URL 정책, transport, 문서 세대와 해제 유지.
  상단 바나 알림 도메인 동작을 런타임에 넣지 않는다.
- `:app`: feature의 의미 있는 chrome 요청을 받아 흰색 상태바 배경/어두운 아이콘과 하단 탭 숨김 처리.
  기존 onFullscreenChanged Boolean으로 부족하면 명시적 상태 계약으로 확장하고 실제 표시 화면에 따라 복원한다.
- `:core:data`: 기존 세션·쿠키 repository 재사용. 서브 화면마다 로그인/세션 초기화를 재실행하지 않는다.
- WebView는 composition/Activity 수명 안에서만 보유한다. ViewModel/싱글톤으로 이동하지 않는다.

## 부모 보존의 정확한 의미

- OPEN_SUBVIEW 때문에 부모에 loadUrl/reload/goBack/clearHistory/destroy를 호출하지 않는다.
- 부모 WebViewPage와 AndroidView를 composition에서 제거했다가 재생성하지 않는다.
  최상단 항목만 composition에 남기는 단순 조건문으로 바꾸지 않는다.
- 부모는 입력·접근성 포커스 대상에서 제외하고 숨김/onPause, 복귀 시 표시/onResume한다.
- 새 entry에서만 payload.url을 최초 한 번 로드한다. 리컴포지션마다 로드하지 않는다.
- 닫을 때 최상단 자식만 해제하고 부모 URL·스크롤·DOM·폼·히스토리를 유지한다.
- “아무 동작 없음”은 앱이 부모에 새 탐색/초기화를 강제하지 않는다는 뜻으로 제안한다.
  onPause는 JavaScript를 완전히 중지하지 않으며, 웹의 자체 타이머·통신·상태 변경은 계속될 수 있다.
  pauseTimers는 모든 WebView에 영향을 주므로 사용하지 않는다. 완전한 백그라운드 정지가 필요하면 웹과 별도 계약한다.
- 새 Activity도 회전/프로세스 종료 시 부모 DOM을 보장하지 않는다. 보존 범위는 살아 있는 동일 host의 push/pop이다.
  기존 logout/계정 전환·오류 재시도·host 해제에 따른 폐기는 계속 필요하다.

## 화면·상단 바 계약 제안

- 상태바 영역은 흰색 배경과 어두운 아이콘. 네이티브 상단 바는 상태바 바로 아래부터 시작한다.
- app이 system inset을 전달하고 feature가 정해진 소유권대로 한 번 적용한다.
  상단 바의 기본 inset과 부모 padding을 중복 적용하지 않는다. 상태바 높이를 고정 dp로 가정하지 않는다.
- 상단 바 아래 본문이 남은 공간을 채운다. WebView를 세로 스크롤 컨테이너에 넣지 않는다.
  navigation bar/IME/cutout과 가로 화면 안전영역을 보존한다.
- loading/error/retry는 본문에만 표시한다. 실패 상태에서도 네이티브 뒤로가기는 누를 수 있다.
- 좌측 뒤로가기는 항상 제공한다. 우측 알림·더보기는 각각 표시 상태와 callback을 갖는다.
  숨긴 버튼은 터치·접근성 노드도 남기지 않는다. 최소 터치 영역 48 dp와 리소스 설명을 사용한다.
- 알림 목적지, 더보기 항목/권한 및 화면별 노출 기본값은 아직 미정이다.
  검토 전 임의 URL, 무반응 버튼, 새 JS 메시지를 구현하지 않는다.
- payload.title의 선택적 표시는 검토 대상으로 두고 URL을 제목으로 대신 노출하지 않는다.
- 웹의 기존 헤더 유무를 확인하고 웹 담당자와 중복 제거를 합의한다.
  네이티브에서 임의 DOM/CSS 주입으로 웹 헤더를 숨기지 않는다.

## 뒤로가기·닫기 계약 제안

상단 뒤로가기와 시스템 뒤로가기는 같은 판단 로직을 사용한다.
기존 표면/로그인 처리와 IME 동작을 보존하고, 실제 탐색에 도달하면 다음 순서를 따른다.

1. 현재 네이티브 dialog/sheet 또는 로그인 UI가 있으면 먼저 기존 취소/닫기 처리를 한다.
2. IME가 열려 있으면 먼저 닫는다(상단 버튼에도 적용하는 제안이며 사용자 검토 대상).
3. BACK_GUARD가 켜져 있으면 BACK_PRESSED를 원래 문서로 보내고 웹 판단을 기다린다.
   이때 즉시 goBack/pop을 동시에 실행하지 않는다.
4. 보호 대상이 아니고 현재 WebView에 히스토리가 있으면 goBack한다.
5. 히스토리가 없으면 현재 서브 entry를 pop/destroy하고 부모를 표시한다.
6. 실패한 서브 문서는 닫아 부모로 돌아갈 수 있어야 한다. 루트에는 서브 pop을 적용하지 않는다.

`CLOSE_SUBVIEW`는 웹이 명시적으로 현재 서브를 닫는 기존 계약이므로 히스토리가 있어도
최상단 자식을 닫는다. 이를 일반 뒤로가기와 합치지 않는다. 웹의 guard 승인 후 CLOSE도 동일하다.
guard 승인 후 히스토리 이동까지 원한다면 현재 메시지만으로는 별도 승인을 구분할 수 없으므로
웹과 계약 확장이 필요하다. 임의 ACK를 추가하지 않는다.

## 기존 기능과 호환성

- 모든 새 entry는 기존 createEntry를 통해 같은 설정·AppBridge·READY·응답 큐를 사용한다.
- 응답은 page id + generation + requestId(있는 메시지)에 연결하고 다른 entry로 보내지 않는다.
- 로그인, 갱신, 로그아웃, 확인창, 선택, 공유, chooser, BACK_GUARD,
  URL/SSL 정책, READY timeout, renderer 오류와 retry를 회귀 검증한다.
- 요청 payload의 url/title/presentation 계약은 유지한다. presentation 생략은 현재처럼 push.
  push/modal 모두 새 독립 WebView와 상단 바를 사용하며 애니메이션 차이는 이번 필수 요구가 아니다.
- 상단 바 표시를 웹에서 동적으로 바꿔야 한다면 별도 웹·앱 계약 검토가 필요하다.
  기존 payload에 미합의 필드를 추가하지 않는다. 우선 네이티브 상태/경로 정책을 검토한다.
- 수신/거절/entry 생성/로드/READY/닫기 로그는 디버그에서 type, page id, generation,
  stack depth, 거절 사유만 중심으로 기록한다. 토큰·전체 payload·민감 URL은 기록하지 않는다.

## 작업 순서와 후속 확인

1. 알림/더보기 노출·동작, 제목, 웹 헤더, guard와 IME 정책을 확정한다.
2. 기존 controller의 스택 생성 경로를 재사용하고 뒤로가기 판단을 정리한다.
3. feature:web에 서브 화면 컨테이너와 상태 기반 상단 바를 조합한다.
4. app chrome과 status/navigation/IME inset 소유권을 연결한다.
5. 실제 웹과 native header 중복 및 브릿지 회귀를 검증한다.
6. 사용자가 수행한 검증 결과를 기록한 뒤 검증 상태를 갱신한다.

## 수용 기준과 후속 검증

- A에서 OPEN_SUBVIEW(B) 시 A의 id/generation/URL/히스토리가 유지되고 B의 id는 새 값이다.
- 앱이 A에 탐색/새로고침을 실행하지 않으며 B만 요청 URL을 최초 로드한다.
- B→C 중첩 후 C→B→A 복귀 시 동일 인스턴스와 스크롤·입력 상태를 확인한다.
- B에 히스토리가 있으면 뒤로가기 시 B 내부 이동, 없으면 B만 제거한다.
- BACK_GUARD/IME/dialog/login 우선 처리, 중복 뒤로가기, 명시적 CLOSE를 검증한다.
- 숨은 부모의 OPEN 요청, 불허 URL, 스택 상한은 새 화면을 만들지 않고 원인을 진단할 수 있다.
- 신규 WebView에서도 기존 모든 브릿지 기능과 파일 선택 취소·문서 변경 후 응답 폐기를 확인한다.
- 흰색 상태바/어두운 아이콘, 상단 바 고정, 오류 중 뒤로가기, 하단 탭 복원,
  알림·더보기 각각 숨김, 큰 글꼴/가로/IME/cutout을 검증한다.
- 순수 뒤로가기 판단 테스트와 상단 바 Compose preview를 사용하고 실제 WebView 보존은
  테스트 전용 에뮬레이터 및 개발 웹에서 검증한다. 기존 connected 테스트의 앱 제거 주의사항을 따른다.
- 변경 후 core:webview 테스트, feature:web 컴파일, app 빌드 및 필요한 구조 검사를 수행한다.
  이번 단계는 사용자 요청으로 검증을 생략했으므로 기능 테스트/빌드를 완료했다고 주장하지 않는다.

## 근거

- [Android navigation design](https://developer.android.com/guide/navigation/design): 단일 Activity 안에서 화면 구성.
- [Android WebView](https://developer.android.com/develop/ui/views/layout/webapps/webview): Activity 레이아웃에 넣는 View.
- [WebView onPause/pauseTimers](https://developer.android.com/reference/android/webkit/WebView#onPause()): JS 정지 범위와 제한.

## 후속 임시 대응: pageFinished 보조 초기화

OPEN_SUBVIEW 생성 경로에만 pageFinished 보조 초기화를 적용했다. 웹 READY도 계속 사용하고
두 진입점은 공통 초기화 함수를 호출한다. 수신 함수 미등록은 기존 READY 제한 시간까지
후속 이벤트를 받을 수 있도록 유지하며, 문서 변경/해제와 중복 성공은 차단한다.
세부 동작은 [개발 지침](../development/webview.md)의 OPEN_SUBVIEW READY 임시 보완을 따른다.
이번 변경도 테스트·빌드·작업 검토 미실행 상태다.
