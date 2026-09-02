# Exam Workflow Sample

실제 담당 기능에서 핵심 처리 방식만 추려 Java 17로 다시 작성한 예제입니다.

## 구현한 예제

### 시험 진행과 답안 제출

- `READY → IN_PROGRESS → FINISHED` 상태 전이
- 진행 중인 시험에서만 답안 제출 허용
- 제출 ID를 이용한 중복 요청 방지
- 새 제출 ID로 기존 답안 수정
- 시험 종료 후 답안 변경 차단

### 검토계획 구성 저장

- 계획 존재 여부 확인
- 분야와 필요 문항 수 검증
- 분야·문항 중복 차단
- 모든 검증이 끝난 뒤 전체 구성 교체

### 선정위원 자동 배정

- 기존 배정 건수 반영
- 담당 문항이 가장 적은 위원 우선 배정
- 이미 배정된 문항 제외
- 중복된 문항·위원 입력 차단

### 시험 준비와 좌석 배정

- 접수자의 시험·일정 데이터 완전성 확인
- 시험실 정원, 좌석과 수험번호 중복 검증
- 동일 요청 멱등 처리와 재배정 이력 생성

## 코드 구조

```text
com.portfolio.exam
├── ExamSession                 시험 상태와 답안 제출
├── review
│   ├── ReviewPlan             검토계획 구성
│   ├── ReviewPlanRepository   저장소 경계
│   └── ReviewPlanService      구성 검증과 교체
└── assignment
    ├── ReviewerAssignment
    └── ReviewerAssignmentService
```

## 테스트

Java 17과 Maven이 필요합니다.

```bash
mvn test
```

정상 상태 전이, 중복 제출, 검토계획 검증, 위원 균등 배정과 중복 배정을 확인합니다.

회사 소스를 옮기지 않고 포트폴리오를 위해 새로 작성한 코드입니다.

