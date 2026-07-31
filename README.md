# 프로젝트명


## 📁 프로젝트 구조

```text
src/main/java
└── auth
    ├── code
    │   └── errorCode
    ├── controller
    ├── domain
    ├── dto
    ├── repository
    └── service
```

    
## 🌿 Branch Convention

브랜치 이름은 아래 규칙을 따릅니다.

```text
type/이슈번호-작업내용
```

### Branch Type

| Type | 의미 | 예시 |
|------|------|------|
| feature | 새로운 기능 개발 | `feature/1-signup` |
| fix | 버그 수정 | `fix/2-login-error` |
| docs | 문서 수정 | `docs/3-readme` |
| refactor | 코드 리팩토링 | `refactor/4-member-service` |
| chore | 설정, 빌드, 기타 작업 | `chore/5-github-template` |
| test | 테스트 코드 작성 | `test/6-member-service` |

---


## 💬 Commit Convention

커밋 메시지는 아래 형식을 사용합니다.

```text
[Type] 작업 내용
```

### Commit Type

| Type | 의미 |
|------|------|
| Feat | 새로운 기능 추가 |
| Fix | 버그 수정 |
| Docs | 문서 수정 |
| Style | 코드 포맷팅, 세미콜론 수정 등 기능 변화 없는 수정 |
| Refactor | 코드 리팩토링 |
| Test | 테스트 코드 추가 또는 수정 |
| Chore | 빌드 설정, 패키지 설정, 기타 작업 |
| Rename | 파일 또는 폴더명 변경 |
| Remove | 파일 삭제 |
